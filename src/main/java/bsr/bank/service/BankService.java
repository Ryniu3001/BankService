package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.AccountMsg;
import bsr.bank.dao.message.OperationMsg;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.service.message.*;
import bsr.bank.service.message.exception.BankServiceException;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.ws.BindingType;
import java.util.List;
import java.util.stream.Collectors;

import static bsr.bank.service.Utils.transferMoneyExternal;
import static bsr.bank.service.Utils.transferMoneyInternal;

/**
 * Usługa BankService
 */
@WebService(name = "BankPortType", portName = "BankPort", serviceName = "BankService", targetNamespace = "http://bsr.bank.pl")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, parameterStyle = SOAPBinding.ParameterStyle.BARE, use = SOAPBinding.Use.LITERAL)
@BindingType(value = javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING)
//@SchemaValidation
public class BankService {

    @WebMethod(operationName = "register")
    public void register(@WebParam(name = "registerRequest", partName = "payload") RegisterRequest request) throws BankServiceException{
        ServiceValidator.validate(request);
        UserMsg userMsg = UserDAO.getInstance().get(new UserMsg(request));
        if (userMsg.getId() != null)
            throw new BankServiceException("User with specified login already exists.", BankServiceException.BAD_CREDENTIALS);
        UserDAO.getInstance().create(new UserMsg(request));
    }

    @WebMethod(operationName = "logIn")
    public LoginResponse logIn(@WebParam(name = "loginRequest", partName = "payload") LoginRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        UserMsg user = Utils.validateCredentials(request);
        if (user == null)
            throw new BankServiceException("Bad credentials.", BankServiceException.BAD_CREDENTIALS);
        String id = Utils.createUserSession(request.getLogin());
        List<AccountMsg> accList = getAccountList(request.getLogin());
        List<AccountResponse> accResponseList = accList.stream()
                                                        .map(Utils::AccountMsgToResponse)
                                                        .collect(Collectors.toList());
        return new LoginResponse(id, accResponseList, user.getName(), user.getSurname());
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
        AccountMsg msg = new AccountMsg();
        msg.setAccountNumber(response.getAccNumber());
        msg.setLogin(login);
        AccountDAO.getInstance().create(msg);
        return response;
    }

    @WebMethod(operationName = "transfer")
    public TransferResponse transfer(@WebParam(name = "transferRequest", partName = "payload") TransferRequest request) throws BankServiceException {
        String login = getLogin(request.getUuid());
        ServiceValidator.validate(request);
        try {
            String bankId = Utils.getBankId(request.getTargetAccountNumber());
            if (bankId.equals(App.THIS_BANK)) {
                return transferMoneyInternal(request);
            } else {
                return transferMoneyExternal(request, bankId);
            }
        } catch (BankServiceException ex){
            throw ex;
        }
    }

    @WebMethod(operationName = "deposit")
    public DepositResponse deposit(@WebParam(name = "depositRequest", partName = "payload") DepositMsg request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        return Utils.deposit(request);
    }

    @WebMethod(operationName = "withdraw")
    public WithdrawResponse withdraw(@WebParam(name = "withdrawRequest", partName = "payload") WithdrawMsg request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        return Utils.withdraw(request);
    }

    @WebMethod(operationName = "getAccounts")
    public GetAccountsResponse getAccounts(@WebParam(name = "getAccountsRequest", partName = "payload")String uuid) throws BankServiceException {
        ServiceValidator.validateUuid(uuid);
        String login = getLogin(uuid);
        List<AccountMsg> accounts = getAccountList(login);
        List<AccountResponse> accResponseList = accounts.stream()
                .map(Utils::AccountMsgToResponse)
                .collect(Collectors.toList());
        return new GetAccountsResponse(accResponseList);
    }

    @WebMethod(operationName = "getHistory")
    public GetHistoryResponse getHistory(@WebParam(name = "getHistoryRequest", partName = "payload")GetHistoryRequest request) throws BankServiceException {
        ServiceValidator.validate(request);
        String login = getLogin(request.getUid());
        ServiceValidator.validateIfAccountBelongsToLogin(login, request.getAccountNumber());
        OperationMsg msg = new OperationMsg();
        msg.setAccountNumber(request.getAccountNumber());
        List<OperationMsg> operationMsgList = OperationDAO.getInstance().getList(msg);
        List<Operation> operations = operationMsgList.stream().map(operationMsg -> operationMsg.toOperationDTO()).collect(Collectors.toList());

        return new GetHistoryResponse(operations);
    }

    private List<AccountMsg> getAccountList(String login){
        AccountMsg accountMsg = new AccountMsg();
        accountMsg.setLogin(login);
        List<AccountMsg> accList =  AccountDAO.getInstance().getList(accountMsg);
        return accList;
    }
    private String getLogin(String uuid) throws BankServiceException {
        String login = Utils.getUserLoginFromSession(uuid);
        if (login == null)
            throw new BankServiceException("User not logged in or session expired.", BankServiceException.USER_LOGGED_OUT);
        return login;
    }
}
