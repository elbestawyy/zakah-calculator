package ntg.project.ZakahCalculator.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForgetPasswordResponse {
    private String message;
    private String email;
}
