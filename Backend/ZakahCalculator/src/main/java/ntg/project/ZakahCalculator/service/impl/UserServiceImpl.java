package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.User;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.repository.UserRepository;
import ntg.project.ZakahCalculator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    //--------------------------- Soft delete a user
    public void softDeleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        if(user.isDeleted()){
            throw new BusinessException(ErrorCode.USER_ALREADY_DELETED);
        }
        userRepository.softDelete(userId, LocalDateTime.now());
    }

    //-------------------- Restore a soft-deleted user
    public void restoreUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isDeleted()) {
            throw new BusinessException(ErrorCode.USER_ALREADY_ACTIVE);
        }
        user.setDeleted(false);
        user.setDeletionDate(null);
        userRepository.save(user);
    }
}
