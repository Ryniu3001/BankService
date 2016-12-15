package bsr.bank.service.message.exception;

import javax.xml.ws.WebFault;

@WebFault(messageName = "BankException", faultBean = "bsr.bank.service.message.exception.FaultBean")
public class BankServiceException extends Exception {

    public static final int USER_LOGGED_OUT = 1;
    public static final int BAD_CREDENTIALS = 2;
    public static final int VALIDATION_ERROR = 3;
    public static final int NO_ACCOUNT = 4;
    public static final int REST_SERVICE_ERROR = 5;
    public static final int UNEXPECTED = 10;

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

    public BankServiceException(FaultBean faultBean) {
        super();
        this.faultBean = faultBean;
    }

    public BankServiceException(String faultBeanMessage, Integer faultCode) {
        super(faultBeanMessage);
        this.faultBean = new FaultBean();
        this.faultBean.setDetails(faultBeanMessage);
        this.faultBean.setCode(faultCode);
    }

    public FaultBean getFaultInfo() {
        return faultBean;
    }
}
