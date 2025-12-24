package ntg.project.ZakahCalculator.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.*;
import ntg.project.ZakahCalculator.dto.response.AuthenticationResponse;
import ntg.project.ZakahCalculator.dto.response.ForgetPasswordResponse;
import ntg.project.ZakahCalculator.dto.response.ResetPasswordResponse;
import ntg.project.ZakahCalculator.dto.response.VerifyOtpResponse;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.mapper.AuthenticationMapper;
import ntg.project.ZakahCalculator.mapper.UserMapper;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.security.JwtService;
import ntg.project.ZakahCalculator.service.AuthenticationService;
import ntg.project.ZakahCalculator.service.OtpService;
import ntg.project.ZakahCalculator.service.RoleService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final OtpService otpService;

    private final UserMapper userMapper;
    private final AuthenticationMapper authenticationMapper;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = (User) auth.getPrincipal();

        if (!user.isVerified()) {
            otpService.resendVerificationOtp(user.getEmail());
            throw new BusinessException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }
        return generateTokens(user);
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

        otpService.generateAndSend(savedUser, OtpType.EMAIL_VERIFICATION);

        log.info("User registered successfully: {}", savedUser.getEmail());
    }

    @Override
    @Transactional
    public AuthenticationResponse verifyAccount(VerifyAccountRequest request) {

        var otp = otpService.validateOtp(
                request.getOtpCode(),
                OtpType.EMAIL_VERIFICATION
        );

        User user = otp.getUser();
        user.setVerified(true);
        userRepository.save(user);

        return generateTokens(user);
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

        User user = getByEmail(request.getEmail());
        otpService.generateAndSend(user, OtpType.PASSWORD_RESET);

        return userMapper.toForgotPasswordResponse(user);
    }

    @Override
    @Transactional
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {
        return otpService.verifyPasswordResetOtp(request.getOtp());
    }

    @Override
    @Transactional
    public ResetPasswordResponse resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        var otp = otpService.validateOtp(
                request.getResetToken(),
                OtpType.PASSWORD_RESET
        );

        User user = otp.getUser();

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.CHANGE_PASSWORD_MISMATCH);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return userMapper.toResetPasswordResponse(user);
    }

    @Override
    public void resendVerificationOtp(ResendOtpRequest request) {

    }

    /* ================= HELPERS ================= */

    private AuthenticationResponse generateTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getUsername());
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        return authenticationMapper.toResponse(accessToken, refreshToken, user);
    }

    private User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
