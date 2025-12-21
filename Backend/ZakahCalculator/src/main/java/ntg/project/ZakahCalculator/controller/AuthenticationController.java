package ntg.project.ZakahCalculator.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.dto.request.*;
import ntg.project.ZakahCalculator.dto.response.*;
import ntg.project.ZakahCalculator.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationRequest request) {
        authenticationService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<AuthenticationResponse> verifyAccount(@RequestBody VerifyAccountRequest request) {
        return ResponseEntity.ok(authenticationService.verifyAccount(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ForgotPasswordResponse> forgetPassword(@RequestBody ForgetPasswordRequest request) throws MessagingException {
        return ResponseEntity.ok(authenticationService.forgetPassword(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<VerifyOtpResponse> verifyOtp(@RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authenticationService.verifyOtp(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authenticationService.resetPassword(request));
    }
}
