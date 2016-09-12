package sample;

/**
 * Created by Yehia830 on 9/10/16.
 */
public class User {
    private String userName;

    private String fullName;

    public User(String userID) {
        this.userName = userID;
    }

    public String getUserID() {
        return userName;
    }

    public void setUserID(String userID) {
        this.userName = userID;
    }

//    public boolean isDone() {
//        return isDone;
//    }
//
//    public void setDone(boolean done) {
//        isDone = done;
//    }
}
