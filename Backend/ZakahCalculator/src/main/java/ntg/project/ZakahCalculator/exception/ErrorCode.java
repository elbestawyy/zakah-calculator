package ntg.project.ZakahCalculator.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "user not found with id %s", NOT_FOUND),
    CHANGE_PASSWORD_MISMATCH("CHANGE_PASSWORD_MISMATCH", "Current password and new password are the same", BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD", "Current password are invalid", BAD_REQUEST),
    ACCOUNT_ALREADY_DEACTIVATED("ACCOUNT_ALREADY_DEACTIVATED", "account already activated", CONFLICT),
    EMAIL_ALREADY_EXISTS("EMAIL_ALREADY_EXISTS", "email already exists", CONFLICT),
    PHONE_ALREADY_EXISTS("PHONE_ALREADY_EXISTS", "phone already exists", CONFLICT),
    PASSWORD_MISMATCH("PASSWORD_MISMATCH", "password don't match", BAD_REQUEST),
    ERR_USER_DISABLED("ERR_USER_DISABLED", "user is disabled", UNAUTHORIZED),
    BAD_CREDENTIALS("BAD_CREDENTIALS", "Username and / or password is incorrect", UNAUTHORIZED),
    USERNAME_NOT_FOUND("USERNAME_NOT_FOUND", "username not found", NOT_FOUND),
    INTERNAL_EXCEPTION("INTERNAL_EXCEPTION", "Internal server error", INTERNAL_SERVER_ERROR),
    USER_DELETED_EXCEPTION("USER_DELETED_EXCEPTION", "User is already deleted", CONFLICT),
    USER_ALREADY_ACTIVE("USER_ALREADY_ACTIVE", "User is already active", CONFLICT),
    PERIOD_EXPIRED("PERIOD_EXPIRED", "period expired", BAD_REQUEST),
    IMAGE_NOT_FOUND("IMAGE_NOT_FOUND", "no images for this user", NOT_FOUND),
    UPLOAD_IMAGE_FAILED("UPLOAD_IMAGE_FAILED", "Uploading image failed", INTERNAL_SERVER_ERROR),
    UN_EXPECTED_FOLLOW("UN_EXPECTED_FOLLOW", "Can't Complete this follow", BAD_REQUEST),
    POST_NOT_FOUND("POST_NOT_FOUND", "post not found", NOT_FOUND),
    UNAUTHORIZED_ACTION("UNAUTHORIZED_ACTION", "unauthorized action", FORBIDDEN),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "Comment Not Found", NOT_FOUND),
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "Notification Not Found", NOT_FOUND),
    LIKE_CONFLICT("LIKE_CONFLICT", "like conflict", CONFLICT),
    JWT_NOT_VALID("JWT_NOT_VALID", "access token not valid", UNAUTHORIZED),
    MAXIMUM_UPLOAD_SIZE_INVALID("MAXIMUM_UPLOAD_SIZE_INVALID", "Maximum upload size is 10MB", BAD_REQUEST),
    IMAGE_DELETE_FAILED("IMAGE_DELETE_FAILED", "Image delete failed or already image was deleted", INTERNAL_SERVER_ERROR),
    FORGET_PASSWORD_SENDING_OTP_FAILED("FORGET_PASSWORD_SENDING_OTP_FAILED", "failed to send otp to your account", INTERNAL_SERVER_ERROR),
    OTP_TOKEN_INVALID("OTP_TOKEN_INVALID", "invalid token send it again!!", INTERNAL_SERVER_ERROR),
    EMPTY_FEED_FOR_USER("EMPTY_FEED_FOR_USER", "Follow Friends To see their Posts", NOT_FOUND),
    IMAGE_TYPE_NOT_SUPPORTED("IMAGE_TYPE_NOT_SUPPORTED", "Image type not supported. Allowed types: JPG, JPEG, PNG, BMP, WEBP, SVG", BAD_REQUEST);

    private final String code;
    private final String defaultMessage;
    private final HttpStatus status;

    ErrorCode(String code, String defaultMessage, HttpStatus status) {
        this.code = code;
        this.defaultMessage = defaultMessage;
        this.status = status;
    }
}
