package bsr.bank.service;

import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.service.message.*;
import com.sun.xml.ws.developer.SchemaValidation;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import java.util.UUID;

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
        UUID id = Utils.createUserSession(request.getLogin());
        return new LoginResponse(id);
    }

    @WebMethod(operationName = "logOut")
    public void logOut(LogOutRequest request) throws BankServiceException {
        Utils.removeUserFromSession(request.getLogin());
    }

    @WebMethod(operationName = "createAccount")
    public NewAccountResponse createAccout(NewAccountRequest request) throws BankServiceException {
        if (!Utils.isUserLogged(request.getUserLogin()))
            throw new BankServiceException("Session expired.", BankServiceException.USER_LOGGED_OUT);
        return null;
    }
}
