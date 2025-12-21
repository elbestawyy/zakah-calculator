package ntg.project.ZakahCalculator.mapper;

import ntg.project.ZakahCalculator.dto.request.ProfileUpdateRequest;
import ntg.project.ZakahCalculator.dto.request.RegistrationRequest;
import ntg.project.ZakahCalculator.dto.response.*;
import ntg.project.ZakahCalculator.entity.User;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Collections;

@Component
public class UserMapper {

    /* ================= Registration ================= */
    public User toEntity(RegistrationRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setName(request.getFirstName() + " " + request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRoles(Collections.emptyList());
        user.setEnabled(false);
        user.setDeleted(false);
        return user;
    }

    /* ================= Profile Update ================= */
    public void updateUserFromRequest(ProfileUpdateRequest request, User user) {
        if (request == null || user == null) return;
        String firstName = request.getFirstName() != null ? request.getFirstName() : "";
        String lastName = request.getLastName() != null ? request.getLastName() : "";
        if (!firstName.isEmpty() || !lastName.isEmpty()) {
            user.setName(firstName + " " + lastName);
        }
    }

    public ProfileUpdateResponse userToProfileUpdateResponse(User user) {
        return ProfileUpdateResponse.builder()
                .fullName(user.getName())
                .build();
    }

    /* ================= Entity → Response ================= */
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        UserResponse response = new UserResponse();
        response.setUserId(user.getId());
        response.setFullName(user.getName());
        response.setEmail(user.getEmail());
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            response.setUserType(user.getRoles().get(0).getName());
        }
        return response;
    }

    /* ================= Delete Account Response ================= */
    public DeleteAccountResponse toDeleteAccountResponse(LocalDate deletedAt, LocalDate restoreUntil) {
        return DeleteAccountResponse.builder()
                .message("Account deleted successfully")
                .deletedAt(deletedAt)
                .restoreUntil(restoreUntil)
                .build();
    }

    /* ================= Verify Account Response ================= */
    public VerifyAccountResponse toVerifyAccountResponse() {
        return VerifyAccountResponse.builder()
                .message("Account verified successfully")
                .build();
    }

    /* ================= Forgot & Reset Password ================= */
    public ForgetPasswordResponse toForgotPasswordResponse(User user) {
        return ForgetPasswordResponse.builder()
                .email(user.getEmail())
                .message("Password reset OTP sent successfully")
                .build();
    }

    public ResetPasswordResponse toResetPasswordResponse(User user) {
        return ResetPasswordResponse.builder()
                .email(user.getEmail())
                .message("Password reset successfully")
                .build();
    }

    /* ================= Refresh Tokens Response ================= */
    public AuthenticationResponse toAuthenticationResponse(String accessToken, String refreshToken) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userResponse(null)
                .build();
    }
}
