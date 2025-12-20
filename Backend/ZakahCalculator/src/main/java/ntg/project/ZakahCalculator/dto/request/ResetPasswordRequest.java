package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResetPasswordRequest {

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.RESET_TOKEN.NOT_BLANK")
    private String resetToken;

    @NotBlank(message = "VALIDATION.RESET_PASSWORD.PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = """
                    VALIDATION.RESET_PASSWORD.PASSWORD.WEAK ,
                    {"Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."}
                    """)
    private String newPassword;
    @NotBlank(message = "VALIDATION.RESET_PASSWORD.PASSWORD.NOT_BLANK")
    private String confirmNewPassword;
}
