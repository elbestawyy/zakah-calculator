package ntg.project.ZakahCalculator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.response.VerifyOtpResponse;
import ntg.project.ZakahCalculator.entity.OtpCode;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.mapper.OtpCodeMapper;
import ntg.project.ZakahCalculator.repository.OtpCodeRepository;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.service.EmailService;
import ntg.project.ZakahCalculator.service.OtpService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OtpServiceImpl implements OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final OtpCodeMapper otpCodeMapper;

    @Override
    public OtpCode generateAndSend(User user, OtpType type) {

        OtpCode otp = otpCodeMapper.toEntity(
                user,
                generateOtp(),
                type
        );

        otpCodeRepository.save(otp);

        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                type,
                otp.getCode()
        );

        log.info("OTP sent to {} for {}", user.getEmail(), type);
        return otp;
    }

    @Override
    public OtpCode validateOtp(String code, OtpType type) {

        OtpCode otp = otpCodeRepository
                .findByCodeAndTypeAndUsedFalse(code, type)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_TOKEN_INVALID));

        if (!otp.isValid()) {
            throw new BusinessException(ErrorCode.OTP_TOKEN_INVALID);
        }

        otp.markAsUsed();
        return otp;
    }

    @Override
    public VerifyOtpResponse verifyPasswordResetOtp(String otpCode) {

        OtpCode otp = validateOtp(otpCode, OtpType.PASSWORD_RESET);
        otp.setResetToken(UUID.randomUUID().toString());

        return otpCodeMapper.toVerifyOtpResponse(otp);
    }

    @Override
    public void resendVerificationOtp(String email) {

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        OtpCode otp = otpCodeRepository.findByUserId(user.getId()).orElse(new OtpCode());
        otp.setCode(generateOtp());
        otpCodeRepository.save(otp);

        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                otp.getType(),
                otp.getCode()
        );

        log.info("OTP sent to {} for {}", user.getEmail(), otp.getType());
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 1_000_000));
    }
}
