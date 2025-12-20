package ntg.project.ZakahCalculator.entity.util;

<<<<<<< HEAD
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
=======
public enum OtpType {
    EMAIL_VERIFICATION,
    PASSWORD_RESET
>>>>>>> 014a1fd10945a19fe5b84da52a2dd6ccb772e5ba
}
