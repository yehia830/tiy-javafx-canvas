package sample;

import org.h2.tools.Server;

import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Yehia830 on 9/8/16.
 */
public class ToDoDatabase {
    public final static String DB_URL = "jdbc:h2:./main";
    String userName;


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

        stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, username);
        ResultSet results = stmt.executeQuery();
        results.next();
        return results.getInt("id");
    }

    public int insertToDo(Connection conn, String text, int userID) throws SQLException {
        System.out.println("insertToDo() with text: " + text +
                            " and userID = " + userID);

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todos VALUES (NULL, ?, false, ?)");

        stmt.setString(1, text);
        stmt.setInt(2, userID);
        System.out.println(stmt.toString());
        stmt.execute();

        PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM todos WHERE text = ? AND user_id = ?");
        stmt2.setString(1, text);
        stmt2.setInt(2, userID);
        ResultSet results = stmt2.executeQuery();
        results.next();
        int id = results.getInt("id");
        return id;
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

    public ArrayList<ToDoItem> selectToDosForUser(Connection conn, int userID) throws SQLException {
        ArrayList<ToDoItem> items = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos " +
                "INNER JOIN users ON todos.user_id = users.id " +
                "WHERE users.id = ?");
        stmt.setInt(1, userID);
        System.out.println("the id im trying to get is " + userID);
        ResultSet results = stmt.executeQuery();

        while (results.next()) {
            int id = results.getInt("id");
            String text = results.getString("text");
            boolean isDone = results.getBoolean("is_done");
            items.add(new ToDoItem(id, text, isDone));
        }
        return items;
    }
    public User selectUser(String userName, Connection conn) throws SQLException{
        PreparedStatement stmt = conn.prepareStatement("SELECT *  FROM users WHERE username = ?");
        stmt.setString(1, userName);
        ResultSet results = stmt.executeQuery();
        if (results == null) {
            System.out.println("IN SELECT USER METHOD: User does not exist!");
            return null;
        } else {
            results.next();
            String usernameFromDB = results.getString("username");
            String fullnameFromDB = results.getString("fullname");
            int idFromDB = results.getInt("id");
            User myUserFromDB = new User(usernameFromDB, fullnameFromDB, idFromDB);
            return myUserFromDB;
        }



    }
    public int getNumberOfUsers(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        int numUsers = 0;
        while (results.next()) {
            numUsers++;
        }
        return numUsers;
//        return -1;
    }


    public ArrayList<User> getAllUsers(Connection conn) throws SQLException {
        ArrayList<User> allUsers = new ArrayList<User>();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet results = stmt.executeQuery();
        String username;
        String fullName;
        int id;
        User userToAdd;
        while (results.next()) {
            username = results.getString("username");
            fullName = results.getString("fullname");
            id = results.getInt("id");
            userToAdd = new User(username, fullName, id);

            allUsers.add(userToAdd);
        }
        return allUsers;
    }

    public String getUserNameByID(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results != null) {
            results.next();
            String username = results.getString("username");

            return username;
        } else {
            return null;
        }
    }







}
