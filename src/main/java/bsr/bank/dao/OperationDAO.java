package bsr.bank.dao;

import bsr.bank.dao.message.AccountMsg;
import bsr.bank.dao.message.OperationMsg;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OperationDAO extends BaseDAO{
    private static OperationDAO instance;

    private final static String INSERT_OPERATION = "INSERT INTO OPERATION (title, type, amount, sourceNRB, balance, accountNumber, date) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    private final static String GET_OPERATION = "SELECT * FROM OPERATION WHERE ID = ?";
    private final static String GET_OPERATION_LIST = "SELECT * FROM OPERATION WHERE accountNumber = ?";
    private final static String DELETE_OPERATION = "DELETE FROM OPERATION WHERE ID = ?";

    private final static String UPDATE_ACCOUNT = "UPDATE ACCOUNT SET balance = ? + (select balance from ACCOUNT WHERE number = ?) WHERE number = ?";
    private final static String GET_ACCOUNT = "SELECT * FROM ACCOUNT WHERE NUMBER = ?";

    private OperationDAO(){}

    public static OperationDAO getInstance(){
        if (instance != null)
            return instance;
        synchronized (UserDAO.class){
            if (instance == null){
                instance = new OperationDAO();
            }
        }
        return instance;
    }

    public void create(OperationMsg msg, Connection conn) throws SQLException {
        PreparedStatement stmt = null;
        boolean connProvided = false;
        if (conn == null)
            conn = getConnection();
        else
            connProvided = true;
        try {
            stmt = conn.prepareStatement(INSERT_OPERATION);
            int idx = 1;
            stmt.setString(idx++, msg.getTitle());
            stmt.setInt(idx++, msg.getType());
            stmt.setInt(idx++, msg.getAmount());
            stmt.setString(idx++, msg.getSourceIban());
            stmt.setInt(idx++, msg.getBalance());
            stmt.setString(idx++, msg.getAccountNumber());
            stmt.setLong(idx++, msg.getDate());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            if (connProvided)
                throw e;
        }finally {
            closeStmt(stmt);
            if (!connProvided)
                closeConn(conn);
        }
    }

    public OperationMsg get(OperationMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(GET_OPERATION);
            int idx = 1;
            stmt.setInt(idx++, msg.getId());
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

    public List<OperationMsg> getList(OperationMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        ResultSet rs = null;
        List<OperationMsg> operationList = new ArrayList<>();
        try {
            stmt = conn.prepareStatement(GET_OPERATION_LIST);
            int idx = 1;
            stmt.setString(idx++, msg.getAccountNumber());
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                operationList.add(populateMsg(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeRs(rs);
            closeStmt(stmt);
            closeConn(conn);
        }
        return operationList;
    }

    public void delete(OperationMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.prepareStatement(DELETE_OPERATION);
            int idx = 1;
            stmt.setInt(idx++, msg.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stmt);
            closeConn(conn);
        }
    }

    public synchronized void transfer(OperationMsg srcAccOp, OperationMsg targetAccOp){
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false); //dla spójności

            //Pobranie kont
            AccountMsg srcAcc = new AccountMsg(srcAccOp.getAccountNumber());
            AccountMsg targetAcc = new AccountMsg(targetAccOp.getAccountNumber());
            srcAcc = AccountDAO.getInstance().get(srcAcc);
            targetAcc = AccountDAO.getInstance().get(targetAcc);

            srcAcc.addToBalance(srcAccOp.getAmount());
            targetAcc.addToBalance(targetAccOp.getAmount());

            //Aktualizacja stanu kont
            AccountDAO.getInstance().update(srcAcc, conn);
            AccountDAO.getInstance().update(targetAcc, conn);

            srcAccOp.setBalance(srcAcc.getBalance());
            targetAccOp.setBalance(targetAcc.getBalance());

            this.create(srcAccOp, conn);
            this.create(targetAccOp, conn);

            conn.commit();
        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException e1) { e1.printStackTrace(); }
            e.printStackTrace();
        }finally {
            closeConn(conn);
        }
    }


    private OperationMsg populateMsg(ResultSet rs) throws SQLException {
        OperationMsg msg = new OperationMsg();
        msg.setId(rs.getInt("id"));
        msg.setAccountNumber(rs.getString("number"));
        msg.setBalance(rs.getInt("balance"));
        msg.setAmount(rs.getInt("amount"));
        msg.setTitle(rs.getString("title"));
        msg.setSourceIban(rs.getString("sourceIban"));
        msg.setType(rs.getInt(rs.getInt("type")));
        return msg;
    }
}
