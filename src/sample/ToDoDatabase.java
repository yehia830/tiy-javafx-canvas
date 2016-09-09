package sample;

import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Yehia830 on 9/8/16.
 */
public class ToDoDatabase {
    public final static String DB_URL = "jdbc:h2:./main";

    public void init() throws SQLException {

        System.out.println("ToDoDatabase.init()");

        Server.createWebServer().start();

        Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS todos (id IDENTITY, text VARCHAR, is_done BOOLEAN, user_id INT)");
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, username VARCHAR, fullname VARCHAR)");
    }


    public int insertUser(Connection conn, String username, String fullname) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, username);
        stmt.setString(2, fullname);
        stmt.execute();

        stmt = conn.prepareStatement("SELECT * FROM users where username = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        results.next();
        return results.getInt("id");
    }

    public void insertToDo(Connection conn, String text, int userID) throws SQLException {
        System.out.println("insertToDo() with text: " + text +
                            " and userID = " + userID);

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (NULL, 'testing by hand', false, 1)");

//        stmt.setString(1, text);
//        stmt.setInt(2, userID);
        System.out.println(stmt.toString());
        stmt.execute();
    }



    public void deleteUser(Connection conn, String username) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM users where username = ?");
        stmt.setString(1, username);
        stmt.execute();
    }

    public void deleteToDo(Connection conn, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM todos WHERE text = ?");
        stmt.setString(1, text);
        stmt.execute();
    }

    public static ArrayList<ToDoItem> selectToDos(Connection conn) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();

        Statement stmt = conn.createStatement();
        ResultSet results = stmt.executeQuery("SELECT * FROM todos");
        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone));
        }
        return items;
    }

    public void toggleToDo(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("UPDATE todos SET is_done = NOT is_done WHERE id = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

    public Connection getConnection() throws SQLException{
        Connection conn = DriverManager.getConnection(DB_URL);
        return conn = DriverManager.getConnection(DB_URL);
    }


}
