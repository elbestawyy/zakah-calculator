package ntg.project.ZakahCalculator.controller;

import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.entity.Role;
import ntg.project.ZakahCalculator.entity.util.UserType;
import ntg.project.ZakahCalculator.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping("/{type}")
    public ResponseEntity<Role> getRoleByType(@PathVariable UserType type) {
        Role role = roleService.findByName(type);
        return ResponseEntity.ok(role);
    }
}
