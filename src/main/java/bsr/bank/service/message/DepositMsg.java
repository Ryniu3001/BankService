package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by marcin on 22.11.16.
 */
public class DepositMsg {

    @XmlElement(required = true)
    protected String uid;
    @XmlElement(required = true)
    protected String accountNumber;
    @XmlElement(required = true)
    protected Integer amount;

    public String getUid() {
        return uid;
    }

    public String getAccountNumber() {
        return accountNumber;
    }


    public Integer getAmount() {
        return amount;
    }

}
