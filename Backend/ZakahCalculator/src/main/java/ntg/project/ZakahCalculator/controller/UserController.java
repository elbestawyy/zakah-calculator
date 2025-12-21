package ntg.project.ZakahCalculator.controller;

import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.dto.request.ChangePasswordRequest;
import ntg.project.ZakahCalculator.dto.request.ProfileUpdateRequest;
import ntg.project.ZakahCalculator.dto.response.DeleteAccountResponse;
import ntg.project.ZakahCalculator.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping ("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        userService.changePassword(request, userId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/update-profile")
    public ResponseEntity<Void> updateProfile(
            @RequestBody ProfileUpdateRequest request,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        userService.updateProfileInfo(request, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<DeleteAccountResponse> softDeleteUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.softDeleteUser(userId));
    }

    @PatchMapping ("/restore/{userId}")
    public ResponseEntity<Void> restoreUser(@PathVariable Long userId) {
        userService.restoreUser(userId);
        return ResponseEntity.ok().build();
    }
}
