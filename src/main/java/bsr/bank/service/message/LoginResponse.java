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

    public LoginResponse(String uid, List<AccountResponse> accountList) {
        this.uid = uid;
        this.accountList = accountList;
    }

    @XmlElement(required = true)
    private String uid;

    private List<AccountResponse> accountList;

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
}
