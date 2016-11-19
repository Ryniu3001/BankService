package bsr.bank.dao;

import bsr.bank.dao.message.AccountMsg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO extends BaseDAO {
    private static AccountDAO instance;
    private final static String INSERT_ACCOUNT = "INSERT INTO ACCOUNT (number, balance, userId) " +
            "VALUES (?, ?, ?)";
    private final static String GET_ACCOUNT = "SELECT * FROM ACCOUNT WHERE NUMBER = ?";
    private final static String GET_ACCOUNT_LIST = "SELECT * FROM ACCOUNT WHERE 1=1";
    private final static String DELETE_ACCOUNT = "DELETE FROM ACCOUNT WHERE NUMBER = ?";
    private final static String SELECT_MAX_ID = "SELECT MAX(ID) AS MAXID FROM ACCOUNT";
    private final static String UPDATE_ACCOUNT = "UPDATE ACCOUNT SET balance = ? WHERE number = ?";


    private AccountDAO(){}

    public static AccountDAO getInstance(){
        if (instance != null)
            return instance;
        synchronized (UserDAO.class){
            if (instance == null){
                instance = new AccountDAO();
            }
        }
        return instance;
    }

    public void create(AccountMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.prepareStatement(INSERT_ACCOUNT);
            int idx = 1;
            stmt.setString(idx++, msg.getAccountNumber());
            stmt.setInt(idx++, msg.getBalance());
            stmt.setInt(idx++, msg.getUserId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stmt);
            closeConn(conn);
        }
    }

    public AccountMsg get(AccountMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(GET_ACCOUNT);
            int idx = 1;
            stmt.setString(idx++, msg.getAccountNumber());
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                msg = populateMsg(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeRs(rs);
            closeStmt(stmt);
            closeConn(conn);
        }
        return msg;
    }

    public Integer getMaxId(){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(SELECT_MAX_ID);
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                return rs.getInt("MAXID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeRs(rs);
            closeStmt(stmt);
            closeConn(conn);
        }
        return null;
    }


    public List<AccountMsg> getList(AccountMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        ResultSet rs = null;
        List<AccountMsg> accountList = new ArrayList<>();
        try {
            String query = GET_ACCOUNT_LIST;
            if (msg.getUserId() != null)
                query += " AND userId = " + msg.getUserId().intValue();
            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                accountList.add(populateMsg(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeRs(rs);
            closeStmt(stmt);
            closeConn(conn);
        }
        return accountList;
    }

    public void delete(AccountMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.prepareStatement(DELETE_ACCOUNT);
            int idx = 1;
            stmt.setString(idx++, msg.getAccountNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stmt);
            closeConn(conn);
        }
    }

    public void update(AccountMsg msg, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        boolean connProvided = false;
        if (conn == null)
            conn = getConnection();
        else
            connProvided = true;
        try {
            stmt = conn.prepareStatement(UPDATE_ACCOUNT);
            int idx = 1;
            stmt.setInt(idx++, msg.getBalance());
            stmt.setString(idx++, msg.getAccountNumber());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connProvided)
                throw e;
        }finally {
            closeStmt(stmt);
            if (!connProvided) {
                closeConn(conn);
            }
        }
    }

    private AccountMsg populateMsg(ResultSet rs) throws SQLException {
        AccountMsg msg = new AccountMsg();
        msg.setId(rs.getInt("id"));
        msg.setAccountNumber(rs.getString("number"));
        msg.setBalance(rs.getInt("balance"));
        msg.setUserId(rs.getInt("userId"));
        return msg;
    }
}
