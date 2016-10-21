package bsr.bank.dao;

import bsr.bank.dao.message.UserMsg;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends BaseDAO{

    private UserDAO instance;
    private final static String INSERT_USER = "INSERT INTO USER (name, surname, login, password) " +
            "VALUES (?, ?, ?, ?)";
    private final static String GET_USER = "SELECT * FROM USER WHERE login = ?";
    private final static String DELETE_USER = "DELETE FROM USER WHERE LOGIN = ?";
    private final static String UPDATE_USER = "UPDATE USER SET name = ?, surname = ?, password = ?";


    private UserDAO(){}

    public UserDAO getInstance(){
        if (instance != null)
            return instance;
        synchronized (UserDAO.class){
            if (instance == null){
                instance = new UserDAO();
            }
        }
        return instance;
    }

    public void create(UserMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.prepareStatement(INSERT_USER);
            int idx = 1;
            stmt.setString(idx++, msg.getName());
            stmt.setString(idx++, msg.getSurname());
            stmt.setString(idx++, msg.getLogin());
            stmt.setString(idx++, msg.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stmt);
            closeConn(conn);
        }
    }

    public UserMsg get(UserMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(GET_USER);
            int idx = 1;
            stmt.setString(idx++, msg.getLogin());
            rs = stmt.executeQuery();
            while ( rs.next() ) {
                msg.setId(rs.getInt("id"));
                msg.setName(rs.getString("name"));
                msg.setSurname(rs.getString("surname"));
                msg.setPassword(rs.getString("password"));
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

    public void delete(UserMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.prepareStatement(DELETE_USER);
            int idx = 1;
            stmt.setString(idx++, msg.getLogin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stmt);
            closeConn(conn);
        }
    }

    public void update(UserMsg msg){
        PreparedStatement stmt = null;
        Connection conn = getConnection();
        try {
            stmt = conn.prepareStatement(UPDATE_USER);
            int idx = 1;
            stmt.setString(idx++, msg.getName());
            stmt.setString(idx++, msg.getSurname());
            stmt.setString(idx++, msg.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stmt);
            closeConn(conn);
        }
    }
}
