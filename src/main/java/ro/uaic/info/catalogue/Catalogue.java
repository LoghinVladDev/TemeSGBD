package ro.uaic.info.catalogue;

import java.util.ArrayList;
import java.util.List;

public class Catalogue {
    private List<CatalogueRow> rows = new ArrayList<>();

    private static List<String> courseNames = new ArrayList<>();

    public Catalogue(){
        this.rows = new ArrayList<>();
    }

    public Catalogue addRow(CatalogueRow cr){
        if(!this.rows.contains(cr))
            this.rows.add(cr);
        return this;
    }

    public List<CatalogueRow> getRows() {
        return rows;
    }

    public Catalogue addData(String nrMatricol, String nume, String prenume, String numeMaterie, double valoareNota, List<String> courseTableNames){
        if(courseNames.isEmpty()) {
            courseTableNames.forEach(e -> courseNames.add(e.replaceAll("_", " ")));

//            System.out.println(courseNames);
        }
        CatalogueRow row = new CatalogueRow().setNume(nume).setPrenume(prenume).setNrMatricol(nrMatricol);

//        System.out.println(row);

        for (CatalogueRow r : this.rows) {
            if(r.equals(row)){
                r.setNotaMaterie(numeMaterie.replaceAll("_", " "), valoareNota);
                return this;
            }
        }

        this.rows.add(row.initMaterii(courseNames).setNotaMaterie(numeMaterie.replaceAll("_" , " "), valoareNota));

        return this;
    }

    @Override
    public String toString() {
        return "Catalogue{" +
                "rows=" + rows +
                '}';
    }
}
