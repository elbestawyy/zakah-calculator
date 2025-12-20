package ntg.project.ZakahCalculator.mapper;

<<<<<<< HEAD
import ntg.project.ZakahCalculator.dto.response.VerifyOtpResponse;
import ntg.project.ZakahCalculator.entity.OtpCode;
import org.mapstruct.Mapper;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OtpCodeMapper {

    default VerifyOtpResponse toVerifyOtpResponse(OtpCode otpCode) {
        return VerifyOtpResponse.builder()
                .message("OTP verified successfully")
                .resetToken(otpCode.getResetToken())
                .build();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "used", constant = "false")
    @Mapping(target = "resetToken", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    OtpCode toEntity(User user, String code, OtpType type);
=======
public interface OtpCodeMapper {
>>>>>>> 014a1fd10945a19fe5b84da52a2dd6ccb772e5ba
}
