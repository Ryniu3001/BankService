package bsr.bank.service;

import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.message.AccountMsg;
import bsr.bank.service.message.NewAccountRequest;
import bsr.bank.service.message.TransferRequest;
import bsr.bank.service.message.exception.BankServiceException;
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

    public static void validate(NewAccountRequest request) throws BankServiceException {
        validateUuid(request.getUid());
    }

    public static void validate(TransferRequest request) throws BankServiceException {
        validateUuid(request.getUuid());
        validateAccountNumber(request.getSourceAccountNumber());
        validateAccountNumber(request.getTargetAccountNumber());
        validateAmount(request.getSourceAccountNumber(), request.getAmount());
        validateTitle(request.getTitle());
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

    private static void validateUuid(String uuid) throws BankServiceException {
        if (uuid.isEmpty()) throwValidationEx("Session id cannot be empty");
    }

    private static void validateAccountNumber(String accNumber) throws BankServiceException {
        if (accNumber.isEmpty()) throwValidationEx("AccountNumber cannot be empty");
        if (!Utils.checkNRB(accNumber)) throwValidationEx(accNumber + " is not correct PL account number.");
    }

    private static void validateTitle(String title) throws BankServiceException {
        if (title.isEmpty()) throwValidationEx("Title cannot be empty");
    }

    private static void validateAmount(String accNumber, Integer amount) throws BankServiceException {
        if (amount <= 0 ) throwValidationEx("Amount must be greater than 0.");
        AccountMsg msg = AccountDAO.getInstance().get(new AccountMsg(accNumber));
        if (amount > msg.getBalance())
            throwValidationEx("Not enough balance on your account.");
    }

    private static void throwValidationEx(String message) throws BankServiceException {
        throw new BankServiceException(message, BankServiceException.VALIDATION_ERROR);
    }
}
