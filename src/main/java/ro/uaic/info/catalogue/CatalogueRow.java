package ro.uaic.info.catalogue;

import com.sun.source.tree.UsesTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CatalogueRow {
    private String nrMatricol;
    private String nume;
    private String prenume;
    private Map<String, Double> noteMaterii;

    public CatalogueRow(){
        this.noteMaterii = new HashMap<>();
    }

    public CatalogueRow initMaterii(List<String> numeMaterii){
        numeMaterii.forEach(e->{noteMaterii.put(e, 0.0);});
        return this;
    }

    public Map<String, Double> getNoteMaterii() {
        return noteMaterii;
    }

    public String getNrMatricol() {
        return nrMatricol;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public CatalogueRow setNotaMaterie(String materie, double valoare) {
        this.noteMaterii.put(materie, valoare);
        return this;
    }

    public CatalogueRow setNrMatricol(String nrMatricol) {
        this.nrMatricol = nrMatricol;
        return this;
    }

    public CatalogueRow setNume(String nume) {
        this.nume = nume;
        return this;
    }

    public CatalogueRow setPrenume(String prenume) {
        this.prenume = prenume;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogueRow that = (CatalogueRow) o;
        return Objects.equals(nrMatricol, that.nrMatricol) &&
                Objects.equals(nume, that.nume) &&
                Objects.equals(prenume, that.prenume);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nrMatricol, nume, prenume);
    }

    @Override
    public String toString() {
        return "CatalogueRow{" +
                "nrMatricol='" + nrMatricol + '\'' +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", noteMaterii=" + noteMaterii +
                "}\n";
    }
}

