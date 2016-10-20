package bsr.bank.dao.message;

public class Account {
    private Integer id;
    private String accountNumber;
    private Integer balance;
    private UserMsg user;

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

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public UserMsg getUser() {
        return user;
    }

    public void setUser(UserMsg user) {
        this.user = user;
    }
}
