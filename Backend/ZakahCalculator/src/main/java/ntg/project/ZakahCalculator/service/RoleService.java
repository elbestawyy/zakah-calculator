package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.UserType;

import java.util.Optional;

public interface RoleService {

    Optional<Role> findByName(UserType name);
}
