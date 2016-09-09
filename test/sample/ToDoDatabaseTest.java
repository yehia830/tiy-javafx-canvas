package sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Yehia830 on 9/8/16.
 */
public class ToDoDatabaseTest {
    static ToDoDatabase todoDatabase = null;



    @Before
    public void setUp() throws Exception {
        System.out.println("setUp()");
        if(todoDatabase == null) {
            System.out.println("Creating a new instance of the ToDoDatabase");
            todoDatabase = new ToDoDatabase();
            todoDatabase.init();
        }
    }


    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInit() throws Exception {
        // test to make sure we can access the new database
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = todoQuery.executeQuery();
        assertNotNull(results);

    }


    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);

        todoDatabase.insertToDo(conn, todoText, userID);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos where text = ?");
        stmt.setString(1, todoText);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteToDo(conn, todoText);
        // make sure we remove the test user we added earlier
        todoDatabase.deleteUser(conn, username);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testSelectAllToDos() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
        int todosBefore = todos.size();

        //adding a call to insertUser
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);


        todoDatabase.insertToDo(conn, firstToDoText,userID);
        todoDatabase.insertToDo(conn, secondToDoText,userID);

        todos = todoDatabase.selectToDos(conn);
        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() >= todosBefore + 2);

        todoDatabase.deleteToDo(conn, firstToDoText);
        todoDatabase.deleteToDo(conn, secondToDoText);
        todoDatabase.deleteUser(conn,username);
    }

    @Test
    public void testInsertUser() throws Exception{
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        System.out.println("Connection = " + conn);
        String username = "someone@tester.com";
        String fullname = "someone's full name";

        int userID = todoDatabase.insertUser(conn, username,fullname);
        System.out.println("Created user with ID = " + userID);

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users where username = ?");
        stmt.setString(1, username);
//        stmt.setString(2,fullname);

        ResultSet results = stmt.executeQuery();
        assertNotNull(results);


        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

//        todoDatabase.deleteUser(conn, username);

    }











}