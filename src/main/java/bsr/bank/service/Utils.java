package bsr.bank.service;

import bsr.bank.App;
import bsr.bank.dao.AccountDAO;
import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.service.message.LoginRequest;

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
        String accNumber = "48" + App.THIS_BANK + sb;
        return accNumber;
    }

    public static String getBankId(String accNumber){
        return accNumber.substring(2,10);
    }


}
