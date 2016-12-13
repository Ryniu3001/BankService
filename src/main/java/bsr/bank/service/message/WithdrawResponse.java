package bsr.bank.service.message;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class WithdrawResponse extends DepositResponse {
    public WithdrawResponse() {
    }

    public WithdrawResponse(String accNumber, Integer balance) {
        super(accNumber, balance);
    }

}
