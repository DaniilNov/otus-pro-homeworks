package ru.otus.java.pro.unittests.bank.service.exception;

public class AccountException extends RuntimeException {
    public AccountException(String message) {
        super(message);
    }
}
