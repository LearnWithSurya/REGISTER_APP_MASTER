package in.surya.binding;



import java.util.HashSet;
import java.util.Set;

import in.surya.validate.ImageNameValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDto {
	    private String userId;
	    @Size(min = 3, max = 20, message = "Invalid Name !!")
	    private String name;
	    @NotBlank(message = "Email is required !!")
	    private String email;
	    @NotBlank(message = "Password is required !!")
	    private String password;
	    @Size(min = 4, max = 6, message = "Invalid gender !!")
	    private String gender;
	    @NotBlank(message = "Write something about yourself !!")
	    private String about;
	    @ImageNameValid
	    private String imageName;
        private String accStatus;
        
        private Set<RoleDto> roles = new HashSet<>();
}
