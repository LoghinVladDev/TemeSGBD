package ro.uaic.info.catalogue;

import ro.uaic.info.sql.DatabaseHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LoaderThread extends Thread {

    private Catalogue catalogue;
    private List<String> courseTableNames;

    private int refreshTime;

    private boolean loading;

    public LoaderThread(Catalogue catalogue, List<String> courseTableNames, int refreshTimeSeconds){
        this.catalogue = catalogue;
        this.courseTableNames = courseTableNames;
        this.refreshTime = refreshTimeSeconds;
    }

    public boolean isLoading() {
        return loading;
    }

    public void run(){

        while(true){
            try{

                Thread.sleep(this.refreshTime * 1000);
                try{
                    System.out.println("LOADING");
                    this.loading = true;

                    DatabaseHandler.getInstance().connect();
                    for (String numeMaterie: courseTableNames ) {
                        PreparedStatement statement =
                            DatabaseHandler.getInstance().getConnection().prepareStatement("SELECT NR_MATRICOL, NUME, PRENUME, VALOARE, DATA_NOTARE FROM " + numeMaterie);

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
                    DatabaseHandler.getInstance().disconnect();

                    this.loading = false;

                    System.out.println("LOADED");
                } catch (SQLException exception){
                    System.out.println(exception.toString());
                }

            } catch (InterruptedException ingored){

            }
        }

    }
}
