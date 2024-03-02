package in.surya.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.surya.binding.PageableResponse;
import in.surya.binding.UserDto;
import in.surya.constrant.AppConstrant;
import in.surya.custom.helper.Helper;
import in.surya.entity.Role;
import in.surya.entity.UserEntity;

import in.surya.exception.RegAppException;
import in.surya.exception.ResourceNotFoundException;
import in.surya.props.AppProperties;
import in.surya.repo.RoleRepository;
import in.surya.repo.UserRepository;
import in.surya.utils.EmailUtils;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private EmailUtils emailUtils;
	
	 @Autowired
	 private ModelMapper mapper;

	
	@Autowired
	private AppProperties appProp;

	 @Value("${user.profile.image.path}")
	 private String imagePath;
	 
	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Value("${normal.role.id}")
	    private String normalRoleId;

	    @Autowired
	    private RoleRepository roleRepository;
	 
	 
	 private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	
	@Override
	public boolean forgotPassword(UserDto user) {
		
		user.setPassword(generateTempPwd());
		String userId = UUID.randomUUID().toString();
		user.setUserId(userId);
		UserEntity entity=new UserEntity();
		user.setAccStatus("UNLOCKED");
		BeanUtils.copyProperties(user, entity);
		
		return true;
	}
	
	@Override
	public boolean uniqueEmail(String email) {
		Optional<UserEntity> userEntity=userRepo.findByEmail(email);
		
		return !userEntity.isPresent();
	}
	
	private String generateTempPwd() {
		String tempPwd =null;
		
		//logic to generate password
		int leftLimit=48;
		int rightLimit=122;
		int targetStringLength=6;
		Random random=new Random();
		
		tempPwd=random.ints(leftLimit,rightLimit+1)
				.filter(i->(i<=57 || i>=65) && (i<=90 || i>=97))
				.limit(targetStringLength)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
		return tempPwd;
	}
	
	
	private boolean sendRegEmail(UserDto user) {
		boolean emailSent=false;
		try {
			Map<String, String> message=appProp.getMessages();
			String subject=message.get(AppConstrant.REG_MAIL_SUBJECT);
			String bodyFileName=message.get(AppConstrant.REG_MAILBODY_TEMPLATEFILE);
			String body=readMailBody(bodyFileName, user);
			emailSent=emailUtils.sendEmail(subject, body, user.getEmail());
			emailSent=true;
		}
		catch(Exception e) {
			throw new RegAppException(e.getMessage());
		}
		//logic to set Email
//		String subject="User Registration Successful";
//		String body=readMailBody("welcome.txt", user);
	
		return emailSent;
	}
	
	public String readMailBody(String fileName,UserDto user) {
		String mailBody=null;
		StringBuffer buffer=new StringBuffer();
		java.nio.file.Path path=Paths.get(fileName);
		
		try(Stream<String> stream=Files.lines(path)){
			stream.forEach(line->{
				buffer.append(line);
			});
	    
		  String finalmailBody=buffer.toString();
          
           mailBody = finalmailBody.replace(AppConstrant.EMAIL, user.getEmail())
		                          .replace(AppConstrant.TEMP_PWD, user.getPassword())
		                          .replace(AppConstrant.FNAME, user.getName());
		}
	
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return mailBody;
	}
	@Override
	public UserDto updateUser(UserDto userDto, String userId) {
		  UserEntity user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found with given id !!"));
	        user.setName(userDto.getName());
	        //email update
	        user.setAbout(userDto.getAbout());
	        user.setGender(userDto.getGender());
	        
	        user.setImageName(userDto.getImageName());

	        //save data
	        UserEntity updatedUser = userRepo.save(user);
	        UserDto updatedUserDto = new UserDto();
	        BeanUtils.copyProperties(updatedUser, updatedUserDto);
	        return updatedUserDto;  
	}


	@Override
	public void deleteUser(String userId) {
		UserEntity user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User not Found With Given id..!"));
		
		 //delete user profile image
        //images/user/abc.png
        String fullPath = imagePath + user.getImageName();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            logger.info("User image not found in folder");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //delete user
        userRepo.delete(user);
		
	}
	

	@Override
	public UserDto getUserById(String userId) {
		 UserEntity user = userRepo.findById(userId)
				 .orElseThrow(() -> new ResourceNotFoundException("user not found with given id !!"));
		 UserDto userDto = new UserDto();
		    BeanUtils.copyProperties(user, userDto);
		return userDto;
	}

	@Override
	public UserDto getUserByEmail(String email) {
		 UserEntity user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with given email id !!"));
		 UserDto userDto = new UserDto();
		    BeanUtils.copyProperties(user, userDto);
		return userDto;
	}
    @Override
	public List<UserDto> searchUser(String keyword) {
	    List<UserEntity> users = userRepo.findByNameContaining(keyword);

	    List<UserDto> dtoList = users.stream()
	            .map(userEntity -> {
	                UserDto userDto = new UserDto();
	                BeanUtils.copyProperties(userEntity, userDto);
	                return userDto;
	            })
	            .collect(Collectors.toList());

	    return dtoList;
	}
    @Override
    public PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir) {

       Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());

//        pageNumber default starts from 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        Page<UserEntity> page = userRepo.findAll(pageable);

        PageableResponse<UserDto> response = Helper.getPageableResponse(page, UserDto.class);

        return response;
    }
    
    @Override
    public Optional<UserEntity> findUserByEmailOptional(String email) {
        return userRepo.findByEmail(email);
    }

	@Override
	public UserDto registerAccount(UserDto userDto) {
		 //generate unique id in string format
        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        //encoding password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // dto->entity
        UserEntity user = dtoToEntity(userDto);


        //fetch role of normal and set it to user
        Role role = roleRepository.findById(normalRoleId).get();
        user.getRoles().add(role);
        UserEntity savedUser = userRepo.save(user);
        //entity -> dto
        UserDto newDto = entityToDto(savedUser);
        return newDto;
	}
	 private UserDto entityToDto(UserEntity savedUser) {

       return mapper.map(savedUser, UserDto.class);

     }
	 
	 private UserEntity dtoToEntity(UserDto userDto) {
;

       return mapper.map(userDto, UserEntity.class);
   }
    
}
