package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement (name = "logInResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class LoginResponse {

    public LoginResponse(){}
    public LoginResponse(String id){
        this.uid = id;
    }

    public LoginResponse(String uid, List<AccountResponse> accountList, String name, String surname) {
        this.uid = uid;
        this.accountList = accountList;
        this.name = name;
        this.surname = surname;
    }

    @XmlElement(required = true)
    private String uid;

    @XmlElement(required = false)
    private List<AccountResponse> accountList;

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private String surname;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public List<AccountResponse> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountResponse> accountList) {
        this.accountList = accountList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
