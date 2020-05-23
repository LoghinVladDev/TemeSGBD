package ro.uaic.info.config;

import ro.uaic.info.catalogue.Catalogue;
import ro.uaic.info.catalogue.CatalogueRow;
import ro.uaic.info.sql.DatabaseHandler;
import ro.uaic.info.window.CatalogueWindow;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PLSQL9 {
    private static List<String> courseTableNames = new ArrayList<>();
    private static Catalogue catalogue = new Catalogue();

    public static void main(String[] args) {
        Connection connection = DatabaseHandler
                .getInstance()
                .connect()
                .getConnection();

        try{

            PreparedStatement statement = connection.prepareStatement("select ID from CURSURI");
            statement.executeQuery();
            ResultSet resultSet = statement.getResultSet();

            while(resultSet.next()){
                try {
                    CallableStatement genCatalogue = connection.prepareCall("{call generateCatalogueForCourseID(?)}");
                    genCatalogue.setInt(1, resultSet.getInt(1));

                    genCatalogue.execute();



                } catch (SQLException e){
                    System.out.println(e.toString() + (e.toString().contains("20005") ? " duplicate table " : ""));
                }

                PreparedStatement getCourseName = connection.prepareStatement("SELECT TITLU_CURS FROM CURSURI WHERE ID = ?");
                getCourseName.setInt(1, resultSet.getInt(1));
                getCourseName.executeQuery();

                getCourseName.getResultSet().next();

                courseTableNames.add(getCourseName.getResultSet().getString(1).replaceAll(" ", "_").toUpperCase());
            }

            statement.close();

        } catch (SQLException e) {
            System.out.println(e.toString());
        }

        try{
            for (String numeMaterie: courseTableNames ) {
                PreparedStatement statement = connection.prepareStatement("SELECT NR_MATRICOL, NUME, PRENUME, VALOARE, DATA_NOTARE FROM " + numeMaterie);

                ResultSet resultSet = statement.executeQuery();

                while(resultSet.next()) {
                    catalogue.addData(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3),
                            numeMaterie.replaceAll("_", " "),
                            resultSet.getDouble(4),
                            courseTableNames
                    );
                }

                statement.close();

            }
        } catch (SQLException exception){
            System.out.println(exception.toString());
        }

//        System.out.println(catalogue.toString());

        CatalogueWindow window = new CatalogueWindow(catalogue);

//        courseTableNames.forEach(e->System.out.print("\"" + e.replaceAll("_", " ") + "\", "));

        window.init();

    }
}
