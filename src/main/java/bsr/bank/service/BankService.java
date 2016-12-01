package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.AccountMsg;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.service.message.*;
import bsr.bank.service.message.exception.BankServiceException;
import com.sun.xml.ws.developer.SchemaValidation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import java.util.List;
import java.util.stream.Collectors;

import static bsr.bank.service.Utils.transferMoneyExternal;
import static bsr.bank.service.Utils.transferMoneyInternal;

@WebService(name = "BankPortType", portName = "BankPort", serviceName = "BankService", targetNamespace = "http://bsr.bank.pl")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.LITERAL)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
@SchemaValidation
public class BankService {

    @WebMethod(operationName = "register")
    public void register(@WebParam(name = "registerRequest", partName = "payload") RegisterRequest request) throws BankServiceException{
        ServiceValidator.validate(request);
        UserMsg userMsg = UserDAO.getInstance().get(new UserMsg(request));
        if (userMsg.getId() != null)
            throw new BankServiceException("User with specified login already exists.");
        UserDAO.getInstance().create(new UserMsg(request));
    }

    @WebMethod(operationName = "logIn")
    public LoginResponse logIn(@WebParam(name = "loginRequest", partName = "payload") LoginRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        UserMsg user = Utils.validateCredentials(request);
        if (user == null)
            throw new BankServiceException("Bad credentials.", BankServiceException.BAD_CREDENTIALS);
        String id = Utils.createUserSession(request.getLogin());
        AccountMsg accountMsg = new AccountMsg();
        accountMsg.setLogin(user.getLogin());
        List<AccountMsg> accList =  AccountDAO.getInstance().getList(accountMsg);
        List<AccountResponse> accResponseList = accList.stream()
                                                        .map(Utils::AccountMsgToResponse)
                                                        .collect(Collectors.toList());
        return new LoginResponse(id, accResponseList);
    }

    @WebMethod(operationName = "logOut")
    public void logOut(@WebParam(name = "logOutRequest", partName = "payload") LogOutRequest request) throws BankServiceException {
        Utils.removeSession(request.getUid());
    }

    @WebMethod(operationName = "createAccount")
    public AccountResponse createAccout(@WebParam(name = "newAccountRequest", partName = "payload") NewAccountRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        AccountResponse response = new AccountResponse(Utils.createNewAccountNumber(), 0, login);
        return response;
    }

    @WebMethod(operationName = "transfer")
    public void transfer(@WebParam(name = "transferRequest", partName = "payload") TransferRequest request) throws BankServiceException {
        String login = getLogin(request.getUuid());
        ServiceValidator.validate(request);
        try {
            if (Utils.getBankId(request.getTargetAccountNumber()).equals(App.THIS_BANK)) {
                transferMoneyInternal(request);
            } else {
                transferMoneyExternal(request);
            }
        } catch (BankServiceException ex){
            throw ex;
        }
    }

    @WebMethod(operationName = "deposit")
    public void deposit(@WebParam(name = "depositRequest", partName = "payload") DepositMsg request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        Utils.deposit(request);
    }

    @WebMethod(operationName = "withdraw")
    public void deposit(@WebParam(name = "withdrawRequest", partName = "payload") WithdrawMsg request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        Utils.withdraw(request);
    }

    private String getLogin(String uuid) throws BankServiceException {
        String login = Utils.getUserLoginFromSession(uuid);
        if (login == null)
            throw new BankServiceException("User not logged in or session expired.", BankServiceException.USER_LOGGED_OUT);
        return login;
    }
}
