package sample;

/**
 * Created by Dominique on 4/21/2016.
 */
public class ToDoItem {
    public String text;
    public boolean isDone;

    public ToDoItem(int id,String text, boolean isDone) {
        this.text = text;
        this.isDone = false;
        this.isDone = isDone;

    }

    public ToDoItem() {

    }
    public ToDoItem(String text) {
    }

    @Override
    public String toString() {
        if (isDone) {
            return text + " (done)";
        } else {
            return text;
        }
        // A one-line version of the logic above:
        // return text + (isDone ? " (done)" : "");
    }
}
