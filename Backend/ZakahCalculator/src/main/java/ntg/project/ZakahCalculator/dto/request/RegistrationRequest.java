package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ntg.project.ZakahCalculator.entity.util.UserType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    @NotBlank(message = "VALIDATION.REGISTRATION.FIRSTNAME.NOT_BLANK")
    @Size(min = 1, max = 50, message = "VALIDATION.REGISTRATION.FIRSTNAME.SIZE")
    private String firstName;

    @NotBlank(message = "VALIDATION.REGISTRATION.LASTNAME.NOT_BLANK")
    @Size(min = 1, max = 50, message = "VALIDATION.REGISTRATION.LASTNAME.SIZE")
    private String lastName;

    @NotBlank(message = "VALIDATION.REGISTRATION.USERNAME.NOT_BLANK")
    @Size(min = 1, max = 40, message = "VALIDATION.REGISTRATION.USERNAME.SIZE")
    @Pattern(regexp = "^[A-Za-z0-9_]+@[A-Za-z.-]+\\.[A-Za-z]{2,}$"
            , message = "VALIDATION.REGISTRATION.USERNAME.NOT_FORMATED {mohamed@ntg.com}")
    private String email;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
            message = """
                    VALIDATION.REGISTRATION.PASSWORD.WEAK ,
                    {"Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."}
                    """)
    private String password;

    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.NOT_BLANK")
    @Size(min = 8, max = 50, message = "VALIDATION.REGISTRATION.PASSWORD.SIZE")
    private String confirmPassword;


    private UserType userType;
}