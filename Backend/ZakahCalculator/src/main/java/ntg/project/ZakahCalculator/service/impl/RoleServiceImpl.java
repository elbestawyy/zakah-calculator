package ntg.project.ZakahCalculator.service.impl;

import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.util.UserType;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
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
    public Role findByName(UserType name) {
        return roleRepository.findByName(name)
                .orElseThrow(()-> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
    }
}
