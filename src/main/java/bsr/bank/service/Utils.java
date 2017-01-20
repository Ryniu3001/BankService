package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.OperationDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.AccountMsg;
import bsr.bank.dao.message.OperationEnum;
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

    /**
     * Pobiera login uzytkownika na podstawie przekazanego uid
     * @param uuid unikalne id sesji użytkownika
     * @return
     */
    public static String getUserLoginFromSession(String uuid) {
        String login = sessions.get(uuid);
        return login;
    }

    public static void removeSession(String uuid) {
        sessions.remove(uuid);
    }

    /**
     * Generuj id sesji uzytkownika
     * @param login login
     * @return uid
     */
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

    /**
     * Waliduje login i haslo uzytkownika
     * @param request
     * @return
     */
    public static UserMsg validateCredentials(LoginRequest request){
        UserMsg user = UserDAO.getInstance().get(new UserMsg(request));
        if (user.getId() != null && user.getPassword().equals(request.getPassword()))
            return user;
        return null;
    }

    /**
     * Tworzy nowy numer konta
     * @return numer konta
     */
    public static String createNewAccountNumber(){
        Integer maxId = AccountDAO.getInstance().getMaxId();
        StringBuffer sb = new StringBuffer(maxId.toString());
        while (sb.length() != 16)
            sb.insert(0, "0");
        String accNumber = App.THIS_BANK + sb;
        accNumber = calculateNRB(accNumber);
        return accNumber;
    }

    /**
     * Zwraca id banku na podstawie numeru konta
     * @param accNumber numer konta
     * @return id banku
     */
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

    /**
     * Weryfikuje poprawność przekazanego unmeru konta
     * @param number numer konta
     * @return True jeśli numer jes poprawny, False jesli jest inaczej
     */
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

    /**
     * Przelew środkow do innego banku
     * @param request
     * @param bankId
     * @return
     * @throws BankServiceException
     */
    public static TransferResponse transferMoneyExternal(TransferRequest request, String bankId) throws BankServiceException {

        RestClient.invokeTransfer(request, bankId);

        OperationMsg operationMsg = new OperationMsg(request.getSourceAccountNumber());
        operationMsg.setTitle(request.getTitle());
        operationMsg.setAmount(-request.getAmount());
        operationMsg.setNrb(request.getTargetAccountNumber());
        operationMsg.setType(OperationEnum.przelew.getValue());
        operationMsg.setDate(System.currentTimeMillis());
        operationMsg = OperationDAO.getInstance().executeOperation(operationMsg);
        return new TransferResponse(operationMsg.getAccountNumber(), operationMsg.getBalance());
    }

    /**
     * Przelew srodkow w ramach banku
     * @param request
     * @return
     * @throws BankServiceException
     */
    public static TransferResponse transferMoneyInternal(TransferRequest request) throws BankServiceException {

        OperationMsg srcAccOp = new OperationMsg(request.getSourceAccountNumber());
        srcAccOp.setType(OperationEnum.przelew.getValue());
        srcAccOp.setAmount(0 - request.getAmount());
        srcAccOp.setTitle(request.getTitle());
        srcAccOp.setNrb(request.getTargetAccountNumber());
        srcAccOp.setDate(System.currentTimeMillis());

        OperationMsg targetAccOp = new OperationMsg(request.getTargetAccountNumber());
        targetAccOp.setType(OperationEnum.przelew.getValue());
        targetAccOp.setAmount(request.getAmount());
        targetAccOp.setTitle(request.getTitle());
        targetAccOp.setNrb(request.getSourceAccountNumber());
        targetAccOp.setDate(System.currentTimeMillis());

        srcAccOp = OperationDAO.getInstance().executeOperations(srcAccOp, targetAccOp);

        return new TransferResponse(srcAccOp.getAccountNumber(), srcAccOp.getBalance());
    }

    /**
     * Tworzy operacje depozytu i zleca jej wykonanie
     * @param request
     * @return
     * @throws BankServiceException
     */
    public static DepositResponse deposit(DepositMsg request) throws BankServiceException {
        OperationMsg srcAccOp = new OperationMsg(request.getAccountNumber());
        srcAccOp.setType(OperationEnum.wpłata.getValue());
        srcAccOp.setAmount(request.getAmount());
        srcAccOp.setDate(System.currentTimeMillis());
        srcAccOp.setTitle("Wpłata środków");

        srcAccOp = OperationDAO.getInstance().executeOperation(srcAccOp);
        return new DepositResponse(srcAccOp.getAccountNumber(), srcAccOp.getBalance());
    }

    /**
     * Tworzy operacje wypłaty i zleca jej wykonanie
     * @param request
     * @return
     * @throws BankServiceException
     */
    public static WithdrawResponse withdraw(DepositMsg request) throws BankServiceException {
        OperationMsg srcAccOp = new OperationMsg(request.getAccountNumber());
        srcAccOp.setType(OperationEnum.wypłata.getValue());
        srcAccOp.setAmount(-request.getAmount());
        srcAccOp.setDate(System.currentTimeMillis());
        srcAccOp.setTitle("Wypłata środków");

        srcAccOp = OperationDAO.getInstance().executeOperation(srcAccOp);
        return new WithdrawResponse(srcAccOp.getAccountNumber(), srcAccOp.getBalance());
    }

    /**
     * Przekształca obiekt modelu tabeli Account w obiekt odpowiedzi usługi
     * @param msg
     * @return
     */
    public static AccountResponse AccountMsgToResponse(AccountMsg msg){
        return new AccountResponse(msg.getAccountNumber(), msg.getBalance(), msg.getLogin());
    }
}
