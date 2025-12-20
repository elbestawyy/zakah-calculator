package ntg.project.ZakahCalculator.service.impl;

import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.util.UserType;
import ntg.project.ZakahCalculator.exception.BusinessException;
import ntg.project.ZakahCalculator.exception.ErrorCode;
import ntg.project.ZakahCalculator.repository.RoleRepository;
import ntg.project.ZakahCalculator.service.RoleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findByName(UserType name) {
        return roleRepository.findByName(name)
<<<<<<< HEAD
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
=======
                .orElseThrow(()-> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
>>>>>>> 014a1fd10945a19fe5b84da52a2dd6ccb772e5ba
    }
}
