package bsr.bank.dao;

import java.sql.*;

public abstract class BaseDAO {

    protected static final String DRIVER = "org.sqlite.JDBC";
    protected static final String DB_URL = "jdbc:sqlite:bank.db";

    protected  Connection getConnection() {
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

    public boolean createTables(){
        String createUser = "CREATE TABLE IF NOT EXISTS User (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT (30) NOT NULL, surname  TEXT (40) NOT NULL, " +
                "login TEXT (50) NOT NULL UNIQUE, password TEXT (50) NOT NULL);";
        String createAccount = "CREATE TABLE IF NOT EXISTS Account (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " number TEXT (30) NOT NULL UNIQUE, balance DECIMAL NOT NULL, " +
                " login TEXT CONSTRAINT account_user_fk REFERENCES User (login) ON DELETE CASCADE);";
        String createOperation = "CREATE TABLE IF NOT EXISTS Operation (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT (200) NOT NULL, type INTEGER (1) NOT NULL, amount DECIMAL NOT NULL, nrb TEXT, " +
                "balance DECIMAL NOT NULL, accountNumber TEXT(30) NOT NULL CONSTRAINT operation_account_fk REFERENCES Account (number) ON DELETE CASCADE," +
                " DATE INTEGER);";
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

    protected void clearData(){
        Statement stat = null;
        Connection conn = null;
        try {
            conn = getConnection();
            stat = conn.createStatement();
            stat.execute("DELETE FROM USER");
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            closeStmt(stat);
            closeConn(conn);
        }
    }

}
