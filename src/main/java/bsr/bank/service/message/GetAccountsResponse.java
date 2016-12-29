package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GetAccountsResponse {
    @XmlElement
    private List<AccountResponse> accountList;

    public GetAccountsResponse() {
    }

    public GetAccountsResponse(List<AccountResponse> accountList) {
        this.accountList = accountList;
    }

    public List<AccountResponse> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<AccountResponse> accountList) {
        this.accountList = accountList;
    }
}
