package ntg.project.ZakahCalculator.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ntg.project.ZakahCalculator.dto.request.*;
import ntg.project.ZakahCalculator.dto.response.*;
import ntg.project.ZakahCalculator.entity.OtpCode;
import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        final Authentication auth = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                ));
        final User user = (User) auth.getPrincipal();
        final String token = this.jwtService.generateAccessToken(user.getUsername());
        final String refreshToken = this.jwtService.generateRefreshToken(user.getUsername());
        // TODO Adding UserResponseDTO To This Return (Waiting For Mappers)
        UserResponse userResponse = UserResponse
                .builder()
                .fullName(user.getName())
                .email(user.getEmail())
                .userType(user.getRoles().isEmpty() ? null : user.getRoles().get(0).getName())
                .build();
        return AuthenticationResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .userResponse(userResponse)
                .build();
    }

    @Override
    @Transactional
    public void register(RegistrationRequest request) {
        checkEmail(request.getEmail());
        checkPasswords(request.getPassword(), request.getConfirmPassword());
        List<Role> roles = new ArrayList<>();
        roles.add(roleService.findByName(request.getUserType()));
        User user = User.builder()
                .name(request.getFirstName() + " " + request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roles)
                .build();

        User savedUser = userRepository.save(user);
        OtpCode otp = otpCodeRepository
                .findByUserIdAndType(savedUser.getId(), OtpType.EMAIL_VERIFICATION)
                .orElseGet(OtpCode::new);

        otp.setUser(user);
        otp.setCode(generateOtp());
        otp.setType(OtpType.EMAIL_VERIFICATION);
        otp.setUsed(false);
        otp.setResetToken(null);
        otpCodeRepository.save(otp);
        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                OtpType.EMAIL_VERIFICATION,
                otp.getCode()
        );
        log.info("User registered successfully with email verification OTP sent: {}", user.getEmail());
    }


    // Verifies user's email using the provided OTP and enables the account.

    @Transactional
    @Override
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

        // TODO: Use Mapper to convert User -> UserResponse
        UserResponse userResponse = UserResponse.builder()
                .fullName(user.getName())
                .email(user.getEmail())
                .userType(user.getRoles().isEmpty() ? null : user.getRoles().get(0).getName())
                .build();

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(userResponse)
                .build();
    }


    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        final String newAccessToken = this.jwtService.refreshAccessToken(request.getRefreshToken());
        final String email = this.jwtService.extractUsernameFromToken(request.getRefreshToken());
        final User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new BusinessException(ErrorCode.USER_NOT_FOUND)
        );
        UserResponse userResponse = UserResponse
                .builder()
                .fullName(user.getName())
                .email(user.getEmail())
                .userType(user.getRoles().isEmpty() ? null : user.getRoles().get(0).getName())
                .build();
        return AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken())
                .userResponse(userResponse)
                .build();
    }

    /**
     * FORGET PASSWORD STEPS
     */

    // Sends a new OTP to the user's email for password reset.
    @Transactional
    @Override
    public ForgotPasswordResponse forgetPassword(ForgetPasswordRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        OtpCode token = otpCodeRepository
                .findByUserIdAndTypeAndUsedFalse(user.getId(), OtpType.PASSWORD_RESET)
                .orElseGet(OtpCode::new);

        token.setCode(generateOtp());
        token.setUser(user);
        token.setType(OtpType.PASSWORD_RESET);
        token.setUsed(false);
        token.setResetToken(null);

        OtpCode savedNewToken = otpCodeRepository.save(token);

        // Send Email Using @Async
        CompletableFuture<String> emailFuture = emailService.sendEmail(
                user.getUsername(), user.getName(),
                OtpType.PASSWORD_RESET,
                token.getCode());

        emailFuture.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Email failed: {}", ex.getMessage());
            } else {
                log.info("Email succeeded: {}", result);
            }
        });

        return ForgotPasswordResponse.builder()
                .message("OTP Sent Successfully")
                .email(savedNewToken.getUser().getUsername())
                .build();
    }


    // Verifies the provided OTP and generates a temporary reset token
    @Transactional
    @Override
    public VerifyOtpResponse verifyOtp(VerifyOtpRequest request) {

        OtpCode otp = otpCodeRepository
                .findByCodeAndTypeAndUsedFalse(request.getOtp(), OtpType.PASSWORD_RESET)
                .orElseThrow(() -> new BusinessException(ErrorCode.OTP_TOKEN_INVALID));

        if (!otp.isValid() && !otp.getType().equals(OtpType.PASSWORD_RESET)) {
            throw new BusinessException(ErrorCode.OTP_TOKEN_INVALID);
        }

        otp.markAsUsed();

        String resetToken = UUID.randomUUID().toString();
        otp.setResetToken(resetToken);

        return VerifyOtpResponse.builder()
                .message("OTP Verified Successfully")
                .resetToken(resetToken)
                .build();
    }


    // Resets the user's password using a valid reset token.
    @Transactional
    @Override
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

        // cleanup
        otp.setResetToken(null);

        userRepository.save(user);

        return ResetPasswordResponse.builder()
                .message("Password Changed Successfully")
                .email(user.getUsername())
                .build();
    }

    private void checkEmail(String email) {
        final boolean usernameExists = this.userRepository.existsByEmail(email);
        if (usernameExists) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
    }


    private void checkPasswords(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private String generateOtp() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }

}
