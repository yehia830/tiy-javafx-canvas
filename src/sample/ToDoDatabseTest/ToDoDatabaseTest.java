package sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class ToDoDatabaseTest {

    ToDoDatabase todoDatabase = null;

    @Before
    public void setUp() throws Exception {
//        System.out.println("setUp() is running!");
        if (todoDatabase == null) {
//            System.out.println("Initializing the database this time around (should only see this once!)");
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
    public void testInsertUser() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String userName = "UnitTest-UserInsertUserName";
        String fullName = "UnitTest-UserInsertFullName";


        todoDatabase.insertUser(conn, userName, fullName);

        // make sure we can retrieve the user we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, userName);

        ResultSet results = stmt.executeQuery();
        assertNotNull(results);

        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(5, numResults);

        todoDatabase.deleteUser(conn, userName);

        // make sure there are no more records for our test user
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

//    @Test
//    public void testInsertToDo() throws Exception {
//        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
//        String todoText = "UnitTest-ToDo";
//        todoDatabase.insertToDo(conn, todoText);
//        // make sure we can retrieve the todo we just created
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos where text = ?");
//        stmt.setString(1, todoText);
//        ResultSet results = stmt.executeQuery();
//        assertNotNull(results);
//        // count the records in results to make sure we get what we expected
//        int numResults = 0;
//        while (results.next()) {
//            numResults++;
//        }
//        assertEquals(1, numResults);
//    }

//    @Test
//    public void testInsertToDo() throws Exception {
//        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
//        String todoText = "UnitTest-ToDo";
//
//        todoDatabase.insertToDo(conn, todoText);
//
//        // make sure we can retrieve the todo we just created
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos WHERE text = ?");
//        stmt.setString(1, todoText);
//        ResultSet results = stmt.executeQuery();
//        assertNotNull(results);
//        // count the records in results to make sure we get what we expected
//        int numResults = 0;
//        while (results.next()) {
//            numResults++;
//        }
//
//        assertEquals(1, numResults);
//
//        todoDatabase.deleteToDo(conn, todoText);
//
//        // make sure there are no more records for our test todo
//        results = stmt.executeQuery();
//        numResults = 0;
//        while (results.next()) {
//            numResults++;
//        }
//        assertEquals(0, numResults);
//    }

    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
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
//        String secondToDoText2 = "UnitTest-ToDo2.1";

        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
//        int todosBefore = todos.size();

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userId = todoDatabase.insertUser(conn, username, fullName);

        todoDatabase.insertToDo(conn, firstToDoText, userId);
        todoDatabase.insertToDo(conn, secondToDoText, userId);

        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " + todos.size() + ")", todos.size() > 1);
//                todos.size() + ")", todos.size() >= todosBefore + 2);

        todoDatabase.deleteToDo(conn, firstToDoText);
        todoDatabase.deleteToDo(conn, secondToDoText);
    }

    @Test
    public void testInsertToDoForUser() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String todoText = "UnitTest-ToDo";
        String todoText2 = "UnitTest-ToDo2";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);

        String username2 = "unitester2@tiy.com";
        String fullName2 = "Unit Tester 2";
        int userID2 = todoDatabase.insertUser(conn, username2, fullName2);

        todoDatabase.insertToDo(conn, todoText, userID);
        todoDatabase.insertToDo(conn, todoText2, userID2);

        // make sure each user only has one todo item
        ArrayList<ToDoItem> todosUser1 = todoDatabase.selectToDosForUser(conn, userID);
        ArrayList<ToDoItem> todosUser2 = todoDatabase.selectToDosForUser(conn, userID2);

        assertEquals(1, todosUser1.size());
        assertEquals(0, todosUser2.size());

        // make sure each todo item matches
        ToDoItem todoUser1 = todosUser1.get(0);
        assertEquals(todoText, todoUser1.text);
        ToDoItem todoUser2 = todosUser2.get(0);
        assertEquals(todoText2, todoUser2.text);

        todoDatabase.deleteToDo(conn, todoText);
        todoDatabase.deleteToDo(conn, todoText2);
        // make sure we remove the test user we added earlier
        todoDatabase.deleteUser(conn, username);
        todoDatabase.deleteUser(conn, username2);

    }
}