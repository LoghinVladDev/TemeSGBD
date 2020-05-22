package ro.uaic.info.window;

import ro.uaic.info.catalogue.Catalogue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogueWindow extends JFrame {
    private Catalogue catalogue;
    private JTable table;
    private DefaultTableModel model;

    private int pageNo = 0;

    private final Object[] columnNames = {"ID", "NR_MATRICOL", "NUME", "PRENUME", "LOGICA", "MATEMATICA", "INTRODUCERE IN PROGRAMARE",
            "ACSO", "SISTEME DE OPERARE", "POO", "FAI", "PS", "RETELE DE CALCULATOARE", "BAZE DE DATE", "LFAC",
            "ALGORITMICA GRAFURILOR", "TEHNOLOGII WEB", "PROGRAMARE AVANSATA", "INGINERIA PROGRAMARII", "PRACTICA SGBD",
            "INVATARE AUTOMATA", "SI", "IA", "PYTHON", "CALCUL NUMERIC", "GC", "MCE", "RETELE PETRI"
    };

    private Object[][] data = null;

    public CatalogueWindow(Catalogue catalogue){
        super();
        this.setSize(
                new Dimension(1600, 900)
        );
        this.setLocation(new Point(0,0));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        this.catalogue = catalogue;

        this.setVisible(true);
    }

    public void loadPage(int pageNumber){

        AtomicInteger i = new AtomicInteger(0);

        this.catalogue.getRows().forEach(e-> {
                if (i.get() >= pageNumber * 50 && i.get() < (pageNumber+1) * 50 ) {
                    data[i.get() - pageNumber*50][0] = i.get();
                    data[i.get() - pageNumber*50][1] = e.getNrMatricol();
                    data[i.get() - pageNumber*50][2] = e.getNume();
                    data[i.get() - pageNumber*50][3] = e.getPrenume();
                    for (int j = 4; j < columnNames.length; j++) {
                        data[i.get() - pageNumber*50][j] = e.getNoteMaterii().get(columnNames[j].toString());
                    }
                }
                i.incrementAndGet();
//                System.out.println(i.get());
            }
        );

        boolean clearRest = false;

        for(int k = 0; k < 50; k++){
            if(k < 48 && data[k][0] == null || data[k+1][0] == null)
                clearRest = true;
            else if(!clearRest && k < 48 && (int)data[k][0] > (int)data[k+1][0])
                 clearRest = true;
            else
            if(clearRest)
                for(int j = 0; j < columnNames.length; j++)
                    data[k][j] = null;
        }

//        System.out.println(pageNumber);

        if(pageNumber == 20)
            for(int j = 0; j < columnNames.length; j++)
                data[49][j] = null;


        this.model.setDataVector(this.data, this.columnNames);

    }

    public CatalogueWindow init(){
        data = new Object[this.catalogue.getRows().size()][columnNames.length];

        this.model = new DefaultTableModel(data,columnNames);

        this.table = new JTable(this.model);

        this.setLayout(new BorderLayout());

        this.add(this.table.getTableHeader(), BorderLayout.PAGE_START);
        this.add(this.table, BorderLayout.CENTER);

        for(int j = 0 ; j < columnNames.length; j++){
            table.getColumnModel().getColumn(j).setPreferredWidth(50);
        }

        this.loadPage(this.pageNo);

        JPanel buttons = new JPanel();

        JButton next = new JButton("next");
        JButton previous = new JButton("previous");

        buttons.setLayout(new FlowLayout());

        buttons.add(previous);
        buttons.add(next);

        next.addActionListener(e -> loadPage(pageNo < this.catalogue.getRows().size() / 50  ? ++pageNo : pageNo));

        previous.addActionListener(e-> loadPage(pageNo > 0 ? --pageNo : pageNo));

        this.add(buttons, BorderLayout.SOUTH);

        return this;
    }

}
