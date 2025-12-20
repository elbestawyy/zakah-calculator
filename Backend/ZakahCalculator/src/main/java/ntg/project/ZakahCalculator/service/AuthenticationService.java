package ntg.project.ZakahCalculator.service;


import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import ntg.project.ZakahCalculator.dto.request.*;
import ntg.project.ZakahCalculator.dto.response.*;

public interface AuthenticationService {
    AuthenticationResponse login(AuthenticationRequest request);

    void register(RegistrationRequest request);

    @Transactional
    AuthenticationResponse verifyAccount(VerifyAccountRequest request);

    AuthenticationResponse refreshToken(RefreshRequest request);

    ForgotPasswordResponse forgetPassword(ForgetPasswordRequest request) throws MessagingException;

    @Transactional
    VerifyOtpResponse verifyOtp(VerifyOtpRequest request);

    @Transactional
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}
