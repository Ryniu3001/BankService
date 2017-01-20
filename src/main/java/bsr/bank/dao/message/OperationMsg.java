package bsr.bank.dao.message;


import bsr.bank.service.message.Operation;

import java.util.Date;

/**
 * Model tabeli Operation
 */
public class OperationMsg {

    public static final int typeTransfer = 0;
    public static final int typeDeposit = 1;
    public static final int typeWithdraw = 2;
    public static final int typeBankFee = 3;

    private Integer id;
    private String title;
    private Integer type;
    private Integer amount;
    private String nrb;
    private Integer balance;
    private String accountNumber;
    private Long date;

    public OperationMsg() {
    }

    public OperationMsg(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getNrb() {
        return nrb;
    }

    public void setNrb(String nrb) {
        this.nrb = nrb;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Operation toOperationDTO(){
        Operation operation = new Operation();
        operation.setBalance(this.balance);
        operation.setAmount(this.amount);
        operation.setTitle(this.title);
        operation.setDate(new Date(this.date));
        operation.setSourceNrb(this.nrb);
        operation.setType(OperationEnum.fromValue(this.type.intValue()).name());
        return operation;
    }
}
