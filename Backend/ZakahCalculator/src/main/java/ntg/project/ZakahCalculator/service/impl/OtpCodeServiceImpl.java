package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.OtpCode;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.repository.OtpCodeRepository;
import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.OtpCodeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpCodeServiceImpl implements OtpCodeService {

    private final OtpCodeRepository otpCodeRepository;

    @Override
    public OtpCode save(OtpCode otpCode) {
        return otpCodeRepository.save(otpCode);
    }

    @Override
    public Optional<OtpCode> getValidOtp(User user, OtpType type) {
        return otpCodeRepository.findByUserAndTypeAndUsedFalse(user, type)
                .filter(otp -> otp.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Override
    public Optional<OtpCode> getByCode(String code, OtpType type) {
        return otpCodeRepository.findByCodeAndTypeAndUsedFalse(code, type)
                .filter(otp -> otp.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Override
    public void markAsUsed(OtpCode otpCode) {
        otpCode.setUsed(true);
        otpCodeRepository.save(otpCode);
    }
}
