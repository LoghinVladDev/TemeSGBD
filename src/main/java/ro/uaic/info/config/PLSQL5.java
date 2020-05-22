package ro.uaic.info.config;

import oracle.ucp.proxy.annotation.Pre;
import ro.uaic.info.sql.DatabaseHandler;

import javax.print.DocFlavor;
import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PLSQL5 {
    public static void main(String[] args) {
        String nume = null, prenume = null;

        Connection connection = DatabaseHandler
                .getInstance()
                .connect()
                .getConnection();

        try{
            PreparedStatement statement = connection.prepareStatement("select NAME, prenume from studenti"); /// invalid identifier, name, not nume
            statement.executeQuery();
        } catch (SQLException e){
            System.out.println("Exceptie Basic SQL : " + e);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("select NUME, PRENUME FROM STUDENTI");
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next()){
                nume = resultSet.getString(1);
                prenume = resultSet.getString(2);

                PreparedStatement getGrade = connection.prepareStatement("select get_grade_median(?,?) from dual");
                getGrade.setString(1, nume);
                getGrade.setString(2, prenume);
                try {
                    getGrade.executeQuery();

                    ResultSet medie = getGrade.getResultSet();

                    medie.next();

                    System.out.println("nume = " + nume + ", prenume = " + prenume + ", medie = " + medie.getDouble(1));
                } catch (SQLException functionException){
                    System.out.println("Exceptie SQL din functie : " + functionException.toString()); //exceptie din functie
                }

                getGrade.close();

            }

        } catch (SQLException e){
            e.printStackTrace();
//            e.printStackTrace();
//            System.out.println(nume + ", " + prenume);
        }

        try{
            PreparedStatement statement = connection.prepareStatement("select get_grade_median('Loghin', 'Vlad') from dual"); /// id inexistent
            statement.executeQuery();
        } catch (SQLException e){
            System.out.println("alta exceptie functie : " + e.toString());
        }
    }
}
