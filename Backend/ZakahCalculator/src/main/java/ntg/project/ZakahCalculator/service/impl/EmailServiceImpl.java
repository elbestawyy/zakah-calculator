package ntg.project.ZakahCalculator.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import ntg.project.ZakahCalculator.entity.util.OtpType;
import ntg.project.ZakahCalculator.service.EmailService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("emailExecutor")
    public CompletableFuture<String> sendEmail(
            String to,
            String fullName,
            OtpType otpType,
            String activationCode) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED,
                    UTF_8.name()
            );
            Map<String, Object> properties = new HashMap<>();
            properties.put("username", fullName);
            properties.put("activation_code", activationCode);

            Context context = new Context();
            context.setVariables(properties);

            ClassPathResource logoImage = new ClassPathResource("static/images/ntg-logo.png");
            helper.addInline("logoImage", logoImage);

            helper.setFrom("madel25810@gmail.com");
            helper.setTo(to);
            helper.setSubject(otpType.getDisplayName());

            String template = templateEngine.process(otpType.getTemplateFile(), context);
            helper.setText(template, true);
            mailSender.send(mimeMessage);
            return CompletableFuture.completedFuture("Email sent successfully to: " + to);
        } catch (MessagingException e) {
            return CompletableFuture.completedFuture("Failed to send email to: " + to);
        }
    }
}

