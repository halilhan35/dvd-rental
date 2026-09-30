package com.halil.dvdrental.service;

public enum PasswordChangeResult {
    SUCCESS,
    WRONG_CURRENT_PASSWORD,
    SAME_AS_CURRENT,
    TOO_SHORT,
    TOO_LONG,
    USER_NOT_FOUND
}