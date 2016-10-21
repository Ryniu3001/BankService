package bsr.bank.service;

import bsr.bank.service.message.BankServiceException;
import bsr.bank.service.message.LoginRequest;
import bsr.bank.service.message.RegisterRequest;

public class ServiceValidator {

    public static void validate(RegisterRequest request) throws BankServiceException {
        validateLogin(request.getLogin());
        validatePassword(request.getPassword());
        validateName(request.getName());
        validateSurname(request.getSurname());
    }

    public static void validate(LoginRequest request) throws BankServiceException {
        validateLogin(request.getLogin());
        validatePassword(request.getPassword());
    }

    private static void validateLogin(String login) throws BankServiceException {
        if (login.isEmpty()) throwValidationEx("login cannot be empty!");
    }

    private static void validatePassword(String password) throws BankServiceException {
        if (password.isEmpty()) throwValidationEx("password cannot be empty");
    }

    private static void validateName(String name) throws BankServiceException {
        if (name.isEmpty()) throwValidationEx("name cannot be empty");
    }

    private static void validateSurname(String surname) throws BankServiceException {
        if (surname.isEmpty()) throwValidationEx("surname cannot be empty");
    }

    private static void throwValidationEx(String message) throws BankServiceException {
        throw new BankServiceException(message, BankServiceException.VALIDATION_ERROR);
    }
}
