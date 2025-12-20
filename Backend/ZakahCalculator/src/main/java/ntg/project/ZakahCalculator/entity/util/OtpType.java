package ntg.project.ZakahCalculator.entity.util;

import lombok.Getter;

@Getter
public enum OtpType {
    EMAIL_VERIFICATION("Email Verification", "email_verification_template"),
    PASSWORD_RESET("Password Reset", "password_reset_template");

    private final String displayName;
    private final String templateFile;

    OtpType(String displayName, String templateFile) {
        this.displayName = displayName;
        this.templateFile = templateFile;
    }
}
