package ntg.project.ZakahCalculator.exception;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;
import java.util.List;

import static ntg.project.ZakahCalculator.exception.ErrorCode.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleException(final BusinessException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(ex.getErrorCode().getCode())
                .message(ex.getMessage())
                .build();
        log.info("Business exception: {}", ex.getMessage());
        log.debug(ex.getMessage(), ex);
        return ResponseEntity
                .status(ex.getErrorCode().getStatus() != null
                        ? ex.getErrorCode().getStatus()
                        : HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleException(final DisabledException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(ERR_USER_DISABLED.getCode())
                .message(ERR_USER_DISABLED.getDefaultMessage())
                .build();
        return ResponseEntity.status(ERR_USER_DISABLED.getStatus())
                .body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(final BadCredentialsException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(BAD_CREDENTIALS.getCode())
                .message(BAD_CREDENTIALS.getDefaultMessage())
                .build();
        return ResponseEntity.status(BAD_CREDENTIALS.getStatus())
                .body(body);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final UsernameNotFoundException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code(USERNAME_NOT_FOUND.getCode())
                .message(USERNAME_NOT_FOUND.getDefaultMessage())
                .build();
        return new ResponseEntity<>(body, USERNAME_NOT_FOUND.getStatus());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(final EntityNotFoundException ex) {
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code("TDB")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(body, NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(final MethodArgumentNotValidException ex) {
        log.error(ex.getMessage(), ex);

        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    final String fieldName = ((FieldError) error).getField();
                    final String errorCode = error.getDefaultMessage();
                    errors.add(ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(errorCode)
                            .build());
                });

        final ErrorResponse body = ErrorResponse.builder()
                .validationErrors(errors)
                .build();
        return ResponseEntity.status(BAD_REQUEST)
                .body(body);
    }


    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleException(final MaxUploadSizeExceededException ex) {
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code(MAXIMUM_UPLOAD_SIZE_INVALID.getCode())
                .message(MAXIMUM_UPLOAD_SIZE_INVALID.getDefaultMessage())
                .build();
        return new ResponseEntity<>(body, MAXIMUM_UPLOAD_SIZE_INVALID.getStatus());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleException(final HttpRequestMethodNotSupportedException ex) {
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code("HTTP_REQUEST_METHOD_NOT_SUPPORTED")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(body, BAD_REQUEST);
    }

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ErrorResponse> handleException(final MessagingException ex) {
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code(FORGET_PASSWORD_SENDING_OTP_FAILED.getCode())
                .message(FORGET_PASSWORD_SENDING_OTP_FAILED.getDefaultMessage())
                .build();
        return new ResponseEntity<>(body, FORGET_PASSWORD_SENDING_OTP_FAILED.getStatus());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleException(final MissingServletRequestParameterException ex) {
        log.debug(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code("MISSING_REQUEST_PARAMETERS")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(body, BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        log.error(ex.getMessage(), ex);
        final ErrorResponse body = ErrorResponse.builder()
                .code(INTERNAL_EXCEPTION.getCode())
                .message(INTERNAL_EXCEPTION.getDefaultMessage())
                .build();
        return new ResponseEntity<>(body, INTERNAL_EXCEPTION.getStatus());
    }
}