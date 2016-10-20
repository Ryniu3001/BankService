package bsr.bank.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDAO {

    protected static final String DRIVER = "org.sqlite.JDBC";
    protected static final String DB_URL = "jdbc:sqlite:bank.db";

    protected static Connection conn = null;

    static {
        if (conn == null){
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

        }
    }

    protected static boolean createTables(){
        String createUser = "CREATE TABLE User (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name STRING (30) NOT NULL, surname  STRING (40) NOT NULL, " +
                "login STRING (50) NOT NULL UNIQUE, password STRING (50) NOT NULL);";
        String createAccount = "CREATE TABLE Account (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " number STRING (30) NOT NULL UNIQUE, balance DECIMAL NOT NULL, " +
                " userId INTEGER CONSTRAINT account_user_fk REFERENCES User (id) ON DELETE CASCADE);";
        String createOperation = "CREATE TABLE Operation (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "type INTEGER (1) NOT NULL, amount DECIMAL NOT NULL, sourceIban INTEGER NOT NULL, " +
                "balance DECIMAL NOT NULL, accountId INTEGER NOT NULL CONSTRAINT operation_account_fk REFERENCES Account (id) ON DELETE CASCADE);";
        Statement stat;
        try {
            stat = conn.createStatement();;
            stat.execute(createUser);
            stat.execute(createAccount);
            stat.execute(createOperation);
        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
