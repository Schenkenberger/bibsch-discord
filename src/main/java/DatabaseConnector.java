import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {
    Connection connection;

    DatabaseConnector() {
        connect();
    }

    private void connect() {
        String url = "jdbc:sqlite:C:\\Users\\schen\\Desktop\\sqlite-tools-win32-x86-3380200\\bibsch.db";
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String userId) {

        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO discord(user_id) VALUES('"+ userId +"')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
