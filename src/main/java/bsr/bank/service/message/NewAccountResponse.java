package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NewAccountResponse {

    @XmlElement
    private String accNumber;
    @XmlElement
    private Integer balance;
    @XmlElement
    private String userLogin;

    public NewAccountResponse() {
    }

    public NewAccountResponse(String accNumber, Integer balance, String userLogin) {
        this.accNumber = accNumber;
        this.balance = balance;
        this.userLogin = userLogin;
    }

    public String getAccNumber() {
        return accNumber;
    }

    public void setAccNumber(String accNumber) {
        this.accNumber = accNumber;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
