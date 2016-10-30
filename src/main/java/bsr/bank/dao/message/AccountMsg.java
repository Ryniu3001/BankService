package bsr.bank.dao.message;

public class AccountMsg {
    private Integer id;
    private String accountNumber;
    private Integer balance;
    private Integer userId;

    public AccountMsg() {
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

    public void addToBalance(Integer amount){
        this.balance += amount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
