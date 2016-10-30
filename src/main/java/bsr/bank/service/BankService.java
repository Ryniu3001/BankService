package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.OperationMsg;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.service.message.*;
import bsr.bank.service.message.exception.BankServiceException;
import com.sun.xml.ws.developer.SchemaValidation;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import java.util.Calendar;

@WebService(name = "BankPortType", portName = "BankPort", serviceName = "BankService", targetNamespace = "http://bsr.bank.pl")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.LITERAL)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@SchemaValidation
public class BankService {

    @WebMethod(operationName = "register")
    public void register(RegisterRequest request) throws BankServiceException{
        ServiceValidator.validate(request);
        UserMsg userMsg = UserDAO.getInstance().get(new UserMsg(request));
        if (userMsg.getId() != null)
            throw new BankServiceException("User with specified login already exists.");
        UserDAO.getInstance().create(new UserMsg(request));
    }

    @WebMethod(operationName = "logIn")
    public LoginResponse logIn(LoginRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        UserMsg user = Utils.validateCredentials(request);
        if (user == null)
            throw new BankServiceException("Bad credentials.", BankServiceException.BAD_CREDENTIALS);
        String id = Utils.createUserSession(request.getLogin());
        return new LoginResponse(id);
    }

    @WebMethod(operationName = "logOut")
    public void logOut(LogOutRequest request) throws BankServiceException {
        Utils.removeSession(request.getUid());
    }

    @WebMethod(operationName = "createAccount")
    public NewAccountResponse createAccout(NewAccountRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        NewAccountResponse response = new NewAccountResponse(Utils.createNewAccountNumber(), 0, login);
        return response;
    }

    @WebMethod(operationName = "transfer")
    public void transfer(TransferRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUuid());
        if (Utils.getBankId(request.getTargetAccountNumber()).equals(App.THIS_BANK)){

        }else{
            //przelew zew
        }

    }

    private String getLogin(String uuid) throws BankServiceException {
        String login = Utils.getUserLoginFromSession(uuid);
        if (login == null)
            throw new BankServiceException("User not logged in or session expired.", BankServiceException.USER_LOGGED_OUT);
        return login;
    }


    public static void transferMoney(TransferRequest request){
        OperationMsg srcAccOp = new OperationMsg(request.getSourceAccountNumber());
        srcAccOp.setType(OperationMsg.typeTransfer);
        srcAccOp.setAmount(0 - request.getAmount());
        srcAccOp.setTitle(request.getTitle());
        srcAccOp.setDate(System.currentTimeMillis() / 1000L);

        OperationMsg targetAccOp = new OperationMsg(request.getTargetAccountNumber());
        targetAccOp.setType(OperationMsg.typeTransfer);
        targetAccOp.setAmount(request.getAmount());
        targetAccOp.setTitle(request.getTitle());
        targetAccOp.setSourceIban(request.getSourceAccountNumber());
        targetAccOp.setDate(System.currentTimeMillis() / 1000L);

        OperationDAO.getInstance().transfer(srcAccOp, targetAccOp);
    }
}
