package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.UserType;
import ntg.project.ZakahCalculator.repository.RoleRepository;
import ntg.project.ZakahCalculator.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Optional<Role> findByName(UserType name) {
        return roleRepository.findByName(name);
    }
}
