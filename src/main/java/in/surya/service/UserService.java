package in.surya.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import in.surya.binding.PageableResponse;
import in.surya.binding.UserDto;
import in.surya.entity.UserEntity;

@Service
public interface UserService {

	
	//create user
	public boolean forgotPassword(UserDto userDto);
	
   //update user
    public UserDto updateUser(UserDto userDto, String userId);
	 	
   //delete user
	public void deleteUser(String userId);
	 	
   //get single user by id
	UserDto getUserById(String userId);

	//search user
	List<UserDto> searchUser(String keyword);
	
 	//unique email
 	public boolean uniqueEmail(String email);
 	
 	//get  single user by email
    UserDto getUserByEmail(String email);
    
    //get all user by pagination and sort
    //get all users
    PageableResponse<UserDto> getAllUser(int pageNumber, int pageSize, String sortBy, String sortDir);

    
    //extra functionality
    public Optional<UserEntity> findUserByEmailOptional(String email);
    
    //recovery account
    public UserDto registerAccount(UserDto userDto);
    
}
