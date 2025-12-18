package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.User;

import java.util.Optional;

public interface UserService {

    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    void softDeleteUser(Long userId);

    void restoreUser(Long userId);
}
