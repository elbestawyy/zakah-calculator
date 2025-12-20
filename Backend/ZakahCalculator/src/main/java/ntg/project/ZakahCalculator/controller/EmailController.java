package ntg.project.ZakahCalculator.controller;


import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.service.EmailService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
}
