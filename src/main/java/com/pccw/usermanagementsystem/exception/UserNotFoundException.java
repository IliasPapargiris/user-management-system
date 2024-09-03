package com.pccw.usermanagementsystem.exception;


public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super(String.format("User not found with id %d", userId));
    }
}
