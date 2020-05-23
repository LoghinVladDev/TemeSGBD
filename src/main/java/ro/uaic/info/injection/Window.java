package ro.uaic.info.injection;

import ro.uaic.info.sql.DatabaseHandler;

import javax.swing.*;
import javax.swing.border.Border;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class Window extends JFrame {

    private static String[] tokens = {
            "UNION",
            "SELECT",
            "USER_TABLES"
    };

    private JTextField input;
    private JList<String> resultList;
    private DefaultListModel<String> resultModel;
    private JScrollPane scrollPane;

    private List<String> dbResultList;

    private JButton commitButton;

    public Window(){
        super();

        this.setSize(
                new Dimension(
                        1600, 900
                )
        );

        this.setLocationRelativeTo(null);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private void getStudentNames(){
        try {
            if(this.input.getText().isEmpty())
                return;

            for (String s : tokens){
                if(this.input.getText().toUpperCase().contains(s))
                    return;
            }

            String statementString = "SELECT NUME, PRENUME FROM STUDENTI WHERE NUME LIKE " + "'%" + this.input.getText() + "%'";

            System.out.println(statementString);

            PreparedStatement statement = DatabaseHandler
                    .getInstance()
                    .connect()
                    .getConnection()
                    .prepareStatement(statementString);

//            statement.setString(1, "%" + this.input.getText() + "%");

            ResultSet rs = statement.executeQuery();

            this.dbResultList.clear();
            this.resultModel.clear();

            while(rs.next()){
                this.dbResultList.add(rs.getString(1) + ", " + rs.getString(2));
            }

            this.resultModel.addAll(this.dbResultList);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void init(){
        this.input = new JTextField();

//        this.input.setMinimumSize(new Dimension(100,100));
//        this.input.setMaximumSize(new Dimension(100,100));
        this.commitButton = new JButton("search");

        JPanel searchPanel = new JPanel();

        searchPanel.setLayout(new BorderLayout());

        searchPanel.add(this.input, BorderLayout.NORTH);
        searchPanel.add(this.commitButton, BorderLayout.SOUTH);

        this.commitButton.addActionListener(e->getStudentNames());

        searchPanel.setVisible(true);

        this.add(searchPanel, BorderLayout.NORTH);
        this.setVisible( true );

        this.dbResultList = new ArrayList<>();
        this.resultModel = new DefaultListModel<>();
        this.resultList = new JList<>(this.resultModel);

        this.scrollPane = new JScrollPane(this.resultList);

        this.add(this.scrollPane, BorderLayout.CENTER);


    }


}


