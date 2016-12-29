package bsr.bank.service.message;

import java.util.Date;

public class Operation {
    private String title;
    private String sourceNrb;
    private String type;
    private Date date;
    private Integer amount;
    private Integer balance;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSourceNrb() {
        return sourceNrb;
    }

    public void setSourceNrb(String sourceNrb) {
        this.sourceNrb = sourceNrb;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }
}
