package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.OtpCode;
import ntg.project.ZakahCalculator.entity.OtpType;
import ntg.project.ZakahCalculator.entity.User;

import java.util.Optional;

public interface OtpCodeService {

    OtpCode save(OtpCode otpCode);

    Optional<OtpCode> getValidOtp(User user, OtpType type);

    Optional<OtpCode> getByCode(String code, OtpType type);

    void markAsUsed(OtpCode otpCode);
}
