package ro.uaic.info.sql;

import oracle.jdbc.OracleDatabaseException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseHandler {
    private static DatabaseHandler instance;

    private Connection connection;

    public static DatabaseHandler getInstance() {
        if(DatabaseHandler.instance == null)
            DatabaseHandler.instance = new DatabaseHandler();
        return DatabaseHandler.instance;
    }

    private DatabaseHandler(){
        this.connection = null;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public DatabaseHandler connect() {
        try{
            Class.forName("oracle.jdbc.OracleDriver");

            this.connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:XE",
                    "STUDENT",
                    "STUDENT"
            );

        } catch (ClassNotFoundException | SQLException exception) {
            exception.printStackTrace();
        }
        return this;
    }

    public void disconnect() {
        try{
            this.connection.close();
        } catch ( SQLException exception ){
            exception.printStackTrace();
        }
    }
}
