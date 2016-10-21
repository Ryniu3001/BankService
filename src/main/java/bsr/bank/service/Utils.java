package bsr.bank.service;

import bsr.bank.dao.UserDAO;
import bsr.bank.dao.message.UserMsg;
import bsr.bank.service.message.LoginRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {

    private static final Map<String, UUID> sessions = new HashMap<>();

    public static boolean isUserLogged(String login) {
        UUID id = sessions.get(login);
        if (id == null)
            return false;
        return true;
    }

    public static void removeUserFromSession(String login) {
        sessions.remove(login);
    }

    public static UUID createUserSession(String login){
        UUID id = sessions.get(login);
        if (id != null)
            return id;
        id = UUID.randomUUID();
        sessions.put(login, id);
        return id;
    }

    public static UserMsg validateCredentials(LoginRequest request){
        UserMsg user = UserDAO.getInstance().get(new UserMsg(request));
        if (user != null && user.getPassword().equals(request.getPassword()))
            return user;
        return null;
    }
}
