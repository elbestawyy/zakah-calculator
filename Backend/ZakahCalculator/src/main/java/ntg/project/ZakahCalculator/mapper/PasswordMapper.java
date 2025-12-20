package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.response.ForgotPasswordResponse;
import ntg.project.ZakahCalculator.dto.response.ResetPasswordResponse;
import ntg.project.ZakahCalculator.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PasswordMapper {

    default ForgotPasswordResponse toForgotPasswordResponse(User user) {
        return ForgotPasswordResponse.builder()
                .email(user.getEmail())
                .message("Password reset OTP sent successfully")
                .build();
    }

    default ResetPasswordResponse toResetPasswordResponse(User user) {
        return ResetPasswordResponse.builder()
                .email(user.getEmail())
                .message("Password reset successfully")
                .build();
    }
}
