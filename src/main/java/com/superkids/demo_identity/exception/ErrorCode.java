package com.superkids.demo_identity.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1111, "Can't find key exception", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User already existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002, "Username must contain at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003, "Password must contain at least 8 characters", HttpStatus.BAD_REQUEST),
    USERNAME_NOT_EXISTED(1004, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1005, "User not authenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(1006, "User not authorized", HttpStatus.FORBIDDEN),
    INVALID_DOB(1007, "Invalid date of birth", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatus status) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

    private int code;
    private HttpStatus status;
    private String message;
}
