package bsr.bank.services.message;

import javax.xml.ws.WebFault;

@WebFault(messageName = "BankException", faultBean = "bsr.bank.services.message.FaultBean")
public class BankServiceException extends Exception {

    private FaultBean faultBean;

    public BankServiceException() {
        super();
    }

    public BankServiceException(String message) {
        super(message);
    }

    public BankServiceException(String message, FaultBean faultBean, Throwable cause) {
        super(message, cause);
        this.faultBean = faultBean;
    }

    public BankServiceException(String message, FaultBean faultBean) {
        super(message);
        this.faultBean = faultBean;
    }

    public FaultBean getFaultInfo() {
        return faultBean;
    }
}
