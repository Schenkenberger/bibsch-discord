package helper;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseConnector {
    Connection connection;

    private String data;

    public DatabaseConnector() {
        connect();
    }

    private void connect() {



        if ((System.getProperty("user.dir").contains("schen"))) {
            data = "jdbc:sqlite:C:\\Users\\schen\\Desktop\\sqlite-tools-win32-x86-3380200\\bibsch.db";

        } else {
            data = "jdbc:sqlite:/media/nas/bibsch/bibsch.db";
        }

        try {
            connection = DriverManager.getConnection(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertCommand(String command, String url) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO commands(command, url) VALUES('"+ command +"', '"+ url +"')");
    }
    public List<List<String>> getCommand() throws SQLException {
        List<List<String>> list = new ArrayList<>();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM commands;");
        while (resultSet.next()) {
            list.add(Arrays.asList(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3)));
        }

        return list;
    }

    public void remCommand(int id) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM commands WHERE id=" + id +";");
    }
}
