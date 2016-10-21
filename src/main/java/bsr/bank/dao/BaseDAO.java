package bsr.bank.dao;

import java.sql.*;

public class BaseDAO {

    protected static final String DRIVER = "org.sqlite.JDBC";
    protected static final String DB_URL = "jdbc:sqlite:bank.db";

    protected Connection getConnection(){
        Connection conn = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException e) {
            System.out.println("Brak sterownika JDBC");
            e.printStackTrace();
            System.exit(0);
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return conn;
    }

    protected void closeConn(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeStmt(Statement stmt){
        if (stmt != null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected void closeRs(ResultSet rs){
        if (rs != null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean createTables(){
        String createUser = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name STRING (30) NOT NULL, surname  STRING (40) NOT NULL, " +
                "login STRING (50) NOT NULL UNIQUE, password STRING (50) NOT NULL);";
        String createAccount = "CREATE TABLE Account (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " number STRING (30) NOT NULL UNIQUE, balance DECIMAL NOT NULL, " +
                " userId INTEGER CONSTRAINT account_user_fk REFERENCES User (id) ON DELETE CASCADE);";
        String createOperation = "CREATE TABLE Operation (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type INTEGER (1) NOT NULL, amount DECIMAL NOT NULL, sourceIban INTEGER NOT NULL, " +
                "balance DECIMAL NOT NULL, accountId INTEGER NOT NULL CONSTRAINT operation_account_fk REFERENCES Account (id) ON DELETE CASCADE);";
        Statement stat = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stat = conn.createStatement();
            stat.execute(createUser);
            stat.execute(createAccount);
            stat.execute(createOperation);
        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            return false;
        }finally {
            closeStmt(stat);
            closeConn(conn);
        }
        return true;
    }

}
