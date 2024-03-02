package in.surya.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import in.surya.binding.ImageResponse;
import in.surya.binding.PageableResponse;
import in.surya.binding.UserDto;
import io.swagger.annotations.Api;
import in.surya.constrant.AppConstrant;
import in.surya.message.ApiResponseMessage;
import in.surya.service.FileService;
import in.surya.service.UserService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@Api(value = "UserController", description = "REST APIs related to perform user operations !!")

public class UserRestController {
	
	@Autowired
	private UserService service;
	
	@Autowired
	private FileService fileService;
	
	 @Value("${user.profile.image.path}")
	  private String imageUploadPath;

	 private Logger logger = LoggerFactory.getLogger(UserRestController.class);

	
	@GetMapping("/emailcheck/{email}")
	public String checkEmail(@PathVariable String email) {
		boolean uniqueEmail = service.uniqueEmail(email);
		if(uniqueEmail) {
			return AppConstrant.UNIQUE;
		}
		else {
			return AppConstrant.DUPLICATE;
		}
	}
	
	@PostMapping("/create")
	public String saveUser(@Valid @RequestBody UserDto user) {
		boolean register=service.forgotPassword(user);
	    if(register) {
	    	return AppConstrant.SUCCESFUL;
	    }
	    else {
	    	return AppConstrant.FAILL;
	    }
	}
    //get by email
	@GetMapping("/email/{email}")
	public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
	    return new ResponseEntity<>(service.getUserByEmail(email), HttpStatus.OK);
	}
	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto user,@PathVariable String userId) {
		UserDto updateUser = service.updateUser(user, userId);
		return new ResponseEntity<>(updateUser,HttpStatus.OK);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable String userId) {
        service.deleteUser(userId);
        ApiResponseMessage message
                = ApiResponseMessage
                .builder()
                .message("User is deleted Successfully !!")
                .success(true)
                .status(HttpStatus.OK)
                .build();

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
    @GetMapping("/{userId}")
    @ApiOperation(value = "Get single user by userid !!")
	public ResponseEntity<UserDto> getUser(@PathVariable String userId) {
	    return new ResponseEntity<>(service.getUserById(userId), HttpStatus.OK);
	}
	
	//search user
    @GetMapping("/search/{keywords}")
    public ResponseEntity<List<UserDto>> searchUser(@PathVariable String keywords) {
        return new ResponseEntity<>(service.searchUser(keywords), HttpStatus.OK);
    }
    
    @GetMapping
    @ApiOperation(value = "get all users", tags = {"user-controller"})
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        return new ResponseEntity<>(service.getAllUser(pageNumber, pageSize, sortBy, sortDir), HttpStatus.OK);
    }

    //upload user image
    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(@RequestParam("userImage") MultipartFile image, @PathVariable String userId) throws IOException {
        String imageName = fileService.uploadFile(image, imageUploadPath);
        UserDto user = service.getUserById(userId);
        user.setImageName(imageName);
        UserDto userDto = service.updateUser(user, userId);
        ImageResponse imageResponse = ImageResponse.builder().imageName(imageName).success(true).message("image is uploaded successfully ").status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponse, HttpStatus.CREATED);

    }

    //serve user image

    @GetMapping(value = "/image/{userId}")
    public void serveUserImage(@PathVariable String userId, HttpServletResponse response) throws IOException {
        UserDto user = service.getUserById(userId);
        logger.info("User image name : {} ", user.getImageName());
        InputStream resource = fileService.getResource(imageUploadPath, user.getImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

    }
    @PostMapping
    @ApiOperation(value = "create new user !!")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success | OK"),
            @ApiResponse(code = 401, message = "not authorized !!"),
            @ApiResponse(code = 201, message = "new user created !!")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto userDto1 = service.registerAccount(userDto);
        return new ResponseEntity<>(userDto1, HttpStatus.CREATED);
    }

}

	


