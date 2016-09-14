package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable  {
    @FXML
    ListView todoList;

    @FXML
    TextField todoText;

    ObservableList<ToDoItem> todoItems = FXCollections.observableArrayList();
    ArrayList<ToDoItem> savableList = new ArrayList<ToDoItem>();
    String fileName = "todos.json";

    public String username;
    public String fullname;
    ToDoDatabase toDoDatabase = new ToDoDatabase();
    Connection conn;
    int id;
    User currentUser;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Scanner inputScanner = new Scanner(System.in);
      try{

          conn = DriverManager.getConnection(ToDoDatabase.DB_URL);

          toDoDatabase.init();

          boolean keepGoing = true;

          if(toDoDatabase.getNumberOfUsers(conn) == 0){
              currentUser = createNewUser(inputScanner);

          }else{
              while(keepGoing){
                  System.out.println("Please enter your userID, or enter \"0\" to create a new user.");
                  System.out.println("USER ID\t\tUSERNAME\t\t\t\tFULL NAME");
                  ArrayList<Integer> userIdHolder = new ArrayList<Integer>();
                  for (User user : toDoDatabase.getAllUsers(conn)) {
                      userIdHolder.add(user.id);
                      System.out.println("   " + user.id + "\t\t" + user.userName + "\t\t\t" + user.fullname);
                  }
                  System.out.println("   0" + "New User");
                  int userSelection = inputScanner.nextInt();
                  inputScanner.nextLine();

                 if (userSelection == 0){
                     currentUser = createNewUser(inputScanner);
                     keepGoing = false;
                 }else{
                     boolean userIsPresent = false;
                     for(int id : userIdHolder){
                         userIsPresent = true;
                     }

                     if(userIsPresent){
                         username = toDoDatabase.getUserNameByID(conn, userSelection);
                         currentUser = toDoDatabase.selectUser(username, conn);
                         keepGoing = false;
                     }else{
                         System.out.println("That user is not in the system!");
                     }

                 }




              }
          }

          ArrayList<ToDoItem> toDoItemsFromDB = toDoDatabase.selectToDosForUser(conn, currentUser.id);
          for (ToDoItem item : toDoItemsFromDB) {
//                System.out.println(item.toString());
              todoItems.add(item);
          }

          todoList.setItems(todoItems);


      }catch (SQLException ex){

      }
////
//        if (username != null && !username.isEmpty()) {
//            fileName = username + ".json";
//        }
//
//        System.out.println("Checking existing list ...");
//        ToDoItemList retrievedList = retrieveList();
//        if (retrievedList != null) {
//            for (ToDoItem item : retrievedList.todoItems) {
//                todoItems.add(item);
//            }
//        }


    }

    public void saveToDoList() {
//        if (todoItems != null && todoItems.size() > 0) {
//            System.out.println("Saving " + todoItems.size() + " items in the list");
//            savableList = new ArrayList<ToDoItem>(todoItems);
//            System.out.println("There are " + savableList.size() + " items in my savable list");
//            saveList();
//        } else {
//            System.out.println("No items in the ToDo List");
//        }
    }

    public void addItem() {
        try {
            System.out.println("Adding item ...");
//            todoItems.add(new ToDoItem(todoText.getText()));

            int id = toDoDatabase.insertToDo(conn, todoText.getText(), currentUser.id);

            ToDoItem newToDoItem = new ToDoItem(id, todoText.getText());
            todoItems.add(newToDoItem);

            todoText.setText("");

//            todoList.setItems(todoItems);
        } catch (SQLException ex) {
            System.out.println("Exception caught inserting toDo");
            ex.printStackTrace();
        }

    }

    public void removeItem() {
        ToDoItem todoItem = (ToDoItem)todoList.getSelectionModel().getSelectedItem();
        System.out.println("Removing " + todoItem.text + " ...");
        todoItems.remove(todoItem);
    }

    public void toggleItem() {
        System.out.println("Toggling item ...");
        try{
             conn = DriverManager.getConnection(ToDoDatabase.DB_URL);


            toDoDatabase.toggleToDo(conn,id);
        }catch(Exception e){

        }


        ToDoItem todoItem = (ToDoItem)todoList.getSelectionModel().getSelectedItem();
        if (todoItem != null) {
            todoItem.isDone = !todoItem.isDone;
            todoList.setItems(null);
            todoList.setItems(todoItems);
        }
    }

    public void saveList() {
        try {

            // write JSON
            JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
            String jsonString = jsonSerializer.serialize(new ToDoItemList(todoItems));

            System.out.println("JSON = ");
            System.out.println(jsonString);

            File sampleFile = new File(fileName);
            FileWriter jsonWriter = new FileWriter(sampleFile);
            jsonWriter.write(jsonString);
            jsonWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public ToDoItemList retrieveList() {
//        try {

//            Scanner fileScanner = new Scanner(new File(fileName));
//            fileScanner.useDelimiter("\\Z"); // read the input until the "end of the input" delimiter
//            String fileContents = fileScanner.next();
//            JsonParser ToDoItemParser = new JsonParser();
//
//            ToDoItemList theListContainer = ToDoItemParser.parse(fileContents, ToDoItemList.class);
//            System.out.println("==============================================");
//            System.out.println("        Restored previous ToDoItem");
//            System.out.println("==============================================");
//            return theListContainer;
//        } catch (IOException ioException) {
//            // if we can't find the file or run into an issue restoring the object
//            // from the file, just return null, so the caller knows to create an object from scratch
            return null;
//        }
    }
    public User createNewUser(Scanner scanner) throws SQLException {
        System.out.println("Hello new user! PLease enter your email!");System.out.print("New user! What is your email? ");
        String username = scanner.nextLine();
        System.out.print("What is your full name? ");
        String fullname = scanner.nextLine();


        int userId = toDoDatabase.insertUser(conn, username, fullname);

        currentUser = new User(username, fullname, userId);
        return currentUser;
    }
}
