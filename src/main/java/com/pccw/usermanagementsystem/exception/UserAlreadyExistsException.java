package com.pccw.usermanagementsystem.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String username) {
        super(String.format("User already exists with username: %s", username));
    }

}
