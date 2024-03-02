package in.surya.message;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponseMessage {
	private String errorCode;
	private String errorMsg;
	private String message;
	private boolean success;
	private HttpStatus status;
}
