package ntg.project.ZakahCalculator.controller;


import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
}
