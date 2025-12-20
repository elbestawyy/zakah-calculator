package ntg.project.ZakahCalculator.controller;


import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.AuthenticationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
}
