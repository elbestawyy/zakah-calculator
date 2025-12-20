package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordRequest {
    @NotBlank(message = "VALIDATION.FORGET_PASSWORD.EMAIL.NOT_BLANK")
    @Email(message = "VALIDATION.FORGET_PASSWORD.EMAIL.EMAIL_FORMAT")
    @Size(min = 1, max = 40, message = "VALIDATION.FORGET_PASSWORD.EMAIL.SIZE")
    private String email;
}
