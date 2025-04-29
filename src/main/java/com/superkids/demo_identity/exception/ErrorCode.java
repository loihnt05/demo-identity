package com.superkids.demo_identity.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized Exception"),
    INVALID_KEY(1111, "Can't find key exception"),
    USER_EXISTED(1001, "User already existed"),
    USERNAME_INVALID(1002, "Username must contain at least 3 characters"),
    PASSWORD_INVALID(1003, "Password must contain at least 8 characters"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
