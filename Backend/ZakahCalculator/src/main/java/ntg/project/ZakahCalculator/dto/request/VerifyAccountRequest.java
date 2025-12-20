package ntg.project.ZakahCalculator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyAccountRequest {
    @NotBlank
    @Size(min = 6, max = 6)
    private String otpCode;
}