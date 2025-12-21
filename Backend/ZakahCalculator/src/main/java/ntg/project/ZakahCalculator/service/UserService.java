package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.dto.request.ChangePasswordRequest;
import ntg.project.ZakahCalculator.dto.request.ProfileUpdateRequest;
import ntg.project.ZakahCalculator.dto.response.DeleteAccountResponse;
import ntg.project.ZakahCalculator.dto.response.ProfileUpdateResponse;
import ntg.project.ZakahCalculator.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface UserService extends UserDetailsService {

    void changePassword(ChangePasswordRequest request);
    ProfileUpdateResponse updateProfileInfo(ProfileUpdateRequest request);
    User findById(Long id);
    DeleteAccountResponse softDeleteUser();
    void restoreUser();
}
