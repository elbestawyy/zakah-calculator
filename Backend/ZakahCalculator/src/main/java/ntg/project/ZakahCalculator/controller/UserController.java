package ntg.project.ZakahCalculator.controller;

import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.dto.request.ChangePasswordRequest;
import ntg.project.ZakahCalculator.dto.request.ProfileUpdateRequest;
import ntg.project.ZakahCalculator.dto.response.DeleteAccountResponse;
import ntg.project.ZakahCalculator.dto.response.ProfileUpdateResponse;
import ntg.project.ZakahCalculator.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping ("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<ProfileUpdateResponse> updateProfile(
            @RequestBody ProfileUpdateRequest request
    ) {
        ProfileUpdateResponse response = userService.updateProfileInfo(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteAccountResponse> softDeleteUser() {
        return ResponseEntity.ok(userService.softDeleteUser());
    }

    @PatchMapping ("/restore")
    public ResponseEntity<Void> restoreUser() {
        userService.restoreUser();
        return ResponseEntity.noContent().build();
    }
}
