package ntg.project.ZakahCalculator.service;

import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.util.UserType;

import java.util.Optional;

public interface RoleService {
    Role findByName(UserType name);
}
