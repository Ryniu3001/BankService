package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.OperationMsg;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.exception.NoAccountException;
import bsr.bank.service.message.LoginRequest;
import bsr.bank.service.message.TransferRequest;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
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
        String id = sessions.entrySet().stream()
                                        .filter(entry -> entry.getValue().equals(login))
                                        .map(entry -> entry.getKey())
                                        .findFirst()
                                        .get();
        if (id != null)
            return id;
        id = UUID.randomUUID().toString();
        sessions.put(id, login);
        return id;
    }

    public static UserMsg validateCredentials(LoginRequest request){
        UserMsg user = UserDAO.getInstance().get(new UserMsg(request));
        if (user != null && user.getPassword().equals(request.getPassword()))
            return user;
        return null;
    }

    public static String createNewAccountNumber(){
        Integer maxId = AccountDAO.getInstance().getMaxId();
        StringBuffer sb = new StringBuffer(maxId);
        while (sb.length() != 16)
            sb.insert(0, "0");
        String accNumber = App.THIS_BANK + sb;
        accNumber = calculateNRB(accNumber);
        return accNumber;
    }

    public static String getBankId(String accNumber){
        return accNumber.substring(2,10);
    }

    /**
     *
     * @param number 24 znakowy numer, bez kodu kraju i początkowych dwóch cyfr
     * @return
     */
    private static String calculateNRB(String number){
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

    public static void transferMoneyExternal(TransferRequest request) throws NoAccountException {
        OperationMsg operationMsg = new OperationMsg(request.getSourceAccountNumber());
        operationMsg.setTitle(request.getTitle());
        operationMsg.setAmount(request.getAmount());
        operationMsg.setNrb(request.getTargetAccountNumber());
        operationMsg.setType(OperationMsg.typeTransfer);
        OperationDAO.getInstance().transfer(operationMsg);
    }

    public static void transferMoneyInternal(TransferRequest request) throws NoAccountException {
        OperationMsg srcAccOp = new OperationMsg(request.getSourceAccountNumber());
        srcAccOp.setType(OperationMsg.typeTransfer);
        srcAccOp.setAmount(0 - request.getAmount());
        srcAccOp.setTitle(request.getTitle());
        srcAccOp.setDate(System.currentTimeMillis() / 1000L);

        OperationMsg targetAccOp = new OperationMsg(request.getTargetAccountNumber());
        targetAccOp.setType(OperationMsg.typeTransfer);
        targetAccOp.setAmount(request.getAmount());
        targetAccOp.setTitle(request.getTitle());
        targetAccOp.setNrb(request.getSourceAccountNumber());
        targetAccOp.setDate(System.currentTimeMillis() / 1000L);

        OperationDAO.getInstance().transfer(srcAccOp, targetAccOp);
    }
}
