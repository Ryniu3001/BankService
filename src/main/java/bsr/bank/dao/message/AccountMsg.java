package bsr.bank.dao.message;

import bsr.bank.service.message.exception.BankServiceException;

public class AccountMsg {
    private Integer id;
    private String accountNumber;
    private Integer balance;
    private String login;

    public AccountMsg() {
        this.balance = 0;
    }


    public AccountMsg(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getBalance() {
        return balance;
    }
    public Double getDoubleBalance() {
        return balance/100.0;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public void addToBalance(Integer amount) throws BankServiceException {
        try {
            this.balance = Math.addExact(this.balance, amount);
        } catch (ArithmeticException ex){
            throw new BankServiceException("Too large amount. Target account reach max account balance.", BankServiceException.VALIDATION_ERROR);
        }
        //this.balance += amount;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
