package ntg.project.ZakahCalculator.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.*;
import ntg.project.ZakahCalculator.dto.response.*;
import ntg.project.ZakahCalculator.entity.OtpCode;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.mapper.AuthenticationMapper;
import ntg.project.ZakahCalculator.mapper.OtpCodeMapper;
import ntg.project.ZakahCalculator.mapper.UserMapper;
import ntg.project.ZakahCalculator.repository.OtpCodeRepository;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.security.JwtService;
import ntg.project.ZakahCalculator.service.AuthenticationService;
import ntg.project.ZakahCalculator.service.EmailService;
import ntg.project.ZakahCalculator.service.RoleService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final OtpCodeRepository otpCodeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    private final UserMapper userMapper;
    private final AuthenticationMapper authenticationMapper;
    private final OtpCodeMapper otpCodeMapper;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) auth.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return authenticationMapper.toResponse(accessToken, refreshToken, user);
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(List.of(roleService.findByName(request.getUserType())));

        User savedUser = userRepository.save(user);

        OtpCode otp = otpCodeMapper.toEntity(
                savedUser,
                generateOtp(),
                OtpType.EMAIL_VERIFICATION
        );

        otpCodeRepository.save(otp);

        emailService.sendEmail(
                savedUser.getEmail(),
                savedUser.getName(),
                OtpType.EMAIL_VERIFICATION,
                otp.getCode()
        );

        log.info("User registered successfully: {}", savedUser.getEmail());
    }

    @Override
    @Transactional
    public AuthenticationResponse verifyAccount(VerifyAccountRequest request) {

        OtpCode otp = otpCodeRepository
                .findByCodeAndTypeAndUsedFalse(request.getOtpCode(), OtpType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_TOKEN_INVALID));

        if (!otp.isValid()) {
            throw new BusinessException(ErrorCode.OTP_TOKEN_INVALID);
        }

        otp.markAsUsed();

        User user = otp.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return authenticationMapper.toResponse(accessToken, refreshToken, user);
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {

        String email = jwtService.extractUsernameFromToken(request.getRefreshToken());

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtService.refreshAccessToken(request.getRefreshToken());

        return authenticationMapper.toResponse(
                newAccessToken,
                request.getRefreshToken(),
                user
        );
    }

    @Override
    @Transactional
    public ForgetPasswordResponse forgetPassword(ForgetPasswordRequest request) {

        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        OtpCode otp = otpCodeMapper.toEntity(
                user,
                generateOtp(),
                OtpType.PASSWORD_RESET
        );

        otpCodeRepository.save(otp);

        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                OtpType.PASSWORD_RESET,
                otp.getCode()
        );
        return userMapper.toForgotPasswordResponse(user);
    }

    @Override
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {

        OtpCode otp = otpCodeRepository
                .findByCodeAndTypeAndUsedFalse(request.getOtp(), OtpType.PASSWORD_RESET)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_TOKEN_INVALID));

        if (!otp.isValid()) {
            throw new BusinessException(ErrorCode.OTP_TOKEN_INVALID);
        }

        otp.markAsUsed();
        otp.setResetToken(UUID.randomUUID().toString());

        return otpCodeMapper.toVerifyOtpResponse(otp);
    }

    @Override
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        OtpCode otp = otpCodeRepository
                .findByResetToken(request.getResetToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_TOKEN_INVALID));

        User user = otp.getUser();

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.CHANGE_PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        otp.setResetToken(null);

        userRepository.save(user);


        return userMapper.toResetPasswordResponse(user);
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 1_000_000));
    }
}
