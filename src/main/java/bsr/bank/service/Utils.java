package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.AccountMsg;
import bsr.bank.dao.message.OperationMsg;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.rest.RestClient;
import bsr.bank.service.message.*;
import bsr.bank.service.message.exception.BankServiceException;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class Utils {
    private static final Map<String, String> sessions = new HashMap<>();

    public static String getUserLoginFromSession(String uuid) {
        String login = sessions.get(uuid);
        return login;
    }

    public static void removeSession(String uuid) {
        sessions.remove(uuid);
    }

    public static String createUserSession(String login){
        Optional<String> id = sessions.entrySet().stream()
                                        .filter(entry -> entry.getValue().equals(login))
                                        .map(entry -> entry.getKey())
                                        .findFirst();
        if (id.isPresent())
            return id.get();
        String newId = UUID.randomUUID().toString();
        sessions.put(newId, login);
        return newId;
    }

    public static UserMsg validateCredentials(LoginRequest request){
        UserMsg user = UserDAO.getInstance().get(new UserMsg(request));
        if (user.getId() != null && user.getPassword().equals(request.getPassword()))
            return user;
        return null;
    }

    public static String createNewAccountNumber(){
        Integer maxId = AccountDAO.getInstance().getMaxId();
        StringBuffer sb = new StringBuffer(maxId.toString());
        while (sb.length() != 16)
            sb.insert(0, "0");
        String accNumber = App.THIS_BANK + sb;
        accNumber = calculateNRB(accNumber);
        return accNumber;
    }

    public static String getBankId(String accNumber){
        if (accNumber == null || accNumber.isEmpty()) {
            System.out.println("Niepoprawny numer konta.");
            return null;
        }
        checkNRB(accNumber);
        return accNumber.substring(2,10);
    }

    /**
     *
     * @param number 24 znakowy numer, bez kodu kraju i początkowych dwóch cyfr
     * @return
     */
    public static String calculateNRB(String number){
        if (number.length() != 24)
            throw new IllegalArgumentException("Numer rachunku nieprawidłowy.");
        String nr2 = number + "252100";
        int modulo = 0;
        BigInteger bi =  new BigInteger(nr2);
        modulo = bi.mod(new BigInteger("97")).intValue();
        modulo = 98 - modulo;
        String mod = String.valueOf(modulo);
        if (mod.length() == 1)
            mod = "0" + mod;
        return mod + number;
    }

    public static boolean checkNRB(String number){
        if (number.length() != 26)
            throw new IllegalArgumentException("Niepoprawny numer dla kraju PL. Numer powinien składać się z 26 cyfr.");
        String nr2 = number.substring(2) + "2521" + number.substring(0,2);
        int modulo = 0;
        BigInteger bi =  new BigInteger(nr2);
        modulo = bi.mod(new BigInteger("97")).intValue();
        if (modulo == 1)
            return true;
        return false;
    }

    public static void transferMoneyExternal(TransferRequest request) throws BankServiceException {

        RestClient.invokeTransfer(request);

        OperationMsg operationMsg = new OperationMsg(request.getSourceAccountNumber());
        operationMsg.setTitle(request.getTitle());
        operationMsg.setAmount(-request.getAmount());
        operationMsg.setNrb(request.getTargetAccountNumber());
        operationMsg.setType(OperationMsg.typeTransfer);
        operationMsg.setDate(System.currentTimeMillis() / 1000L);
        OperationDAO.getInstance().executeOperation(operationMsg);
    }

    public static void transferMoneyInternal(TransferRequest request) throws BankServiceException {

        OperationMsg srcAccOp = new OperationMsg(request.getSourceAccountNumber());
        srcAccOp.setType(OperationMsg.typeTransfer);
        srcAccOp.setAmount(0 - request.getAmount());
        srcAccOp.setTitle(request.getTitle());
        srcAccOp.setNrb(request.getTargetAccountNumber());
        srcAccOp.setDate(System.currentTimeMillis() / 1000L);

        OperationMsg targetAccOp = new OperationMsg(request.getTargetAccountNumber());
        targetAccOp.setType(OperationMsg.typeTransfer);
        targetAccOp.setAmount(request.getAmount());
        targetAccOp.setTitle(request.getTitle());
        targetAccOp.setNrb(request.getSourceAccountNumber());
        targetAccOp.setDate(System.currentTimeMillis() / 1000L);

        OperationDAO.getInstance().executeOperations(srcAccOp, targetAccOp);
    }

    public static DepositResponse deposit(DepositMsg request) throws BankServiceException {
        OperationMsg srcAccOp = new OperationMsg(request.getAccountNumber());
        srcAccOp.setType(OperationMsg.typeDeposit);
        srcAccOp.setAmount(request.getAmount());
        srcAccOp.setDate(System.currentTimeMillis() / 1000L);
        srcAccOp.setTitle("Wpłata środków");

        srcAccOp = OperationDAO.getInstance().executeOperation(srcAccOp);
        return new DepositResponse(srcAccOp.getAccountNumber(), srcAccOp.getBalance());
    }

    public static WithdrawResponse withdraw(DepositMsg request) throws BankServiceException {
        OperationMsg srcAccOp = new OperationMsg(request.getAccountNumber());
        srcAccOp.setType(OperationMsg.typeWithdraw);
        srcAccOp.setAmount(-request.getAmount());
        srcAccOp.setDate(System.currentTimeMillis() / 1000L);
        srcAccOp.setTitle("Wypłata środków");

        srcAccOp = OperationDAO.getInstance().executeOperation(srcAccOp);
        return new WithdrawResponse(srcAccOp.getAccountNumber(), srcAccOp.getBalance());
    }

    public static AccountResponse AccountMsgToResponse(AccountMsg msg){
        return new AccountResponse(msg.getAccountNumber(), msg.getBalance(), msg.getLogin());
    }
}
