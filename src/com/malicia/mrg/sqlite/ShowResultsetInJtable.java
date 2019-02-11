package com.malicia.mrg.sqlite;

import com.malicia.mrg.photo.object.groupphoto.GroupeDePhoto;
import javafx.collections.ObservableList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class ShowResultsetInJtable {
    private final String bigTitle;
    private final String title;
    private SQLiteJDBCDriverConnection sql;

    public ShowResultsetInJtable(SQLiteJDBCDriverConnection sql, String bigTitle, String title) {
        this.sql = sql;
        this.bigTitle = bigTitle;
        this.title = title;
    }

    public void invoke() throws SQLException {
        final JFrame frame = new JFrame(bigTitle);
        JTable table = new JTable(buildTableModel(sql.rs));
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JLabel lblHeading = new JLabel(title);
        lblHeading.setFont(new Font("Arial",Font.TRUETYPE_FONT,24));

        frame.getContentPane().setLayout(new BorderLayout());

        frame.getContentPane().add(lblHeading,BorderLayout.PAGE_START);
        frame.getContentPane().add(scrollPane,BorderLayout.CENTER);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 500);
        frame.setVisible(true);
    }

    private static DefaultTableModel buildTableModel(ResultSet rs)
            throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);

    }
}
