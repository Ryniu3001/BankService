package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
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
