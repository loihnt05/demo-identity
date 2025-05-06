package com.superkids.demo_identity.exception;

import com.superkids.demo_identity.dto.request.ApiResponse;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.View;

import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final View error;
    private final static String MIN_ATTRIBUTE = "min";

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handleAppException(AppException e) {
        ErrorCode errorCode = e.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(apiResponse);
    }
    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException e) {
        ErrorCode errorCode = ErrorCode.FORBIDDEN;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException e) {
        String enumKey = e.getFieldError().getDefaultMessage();
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> attributes = null;

        try{
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation = e.getBindingResult()
                    .getAllErrors()
                    .getFirst()
                    .unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());

        }catch(IllegalArgumentException iae){

        }

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(Objects.nonNull(attributes) ?
                mapAttribute(errorCode.getMessage(), attributes) :
                errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}
