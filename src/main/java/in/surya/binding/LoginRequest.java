package in.surya.binding;

import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

@Data
public class LoginRequest {
      private String email;
      private String pwd;
}
