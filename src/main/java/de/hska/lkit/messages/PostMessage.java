package de.hska.lkit.messages;


/**
 * Created by bob on 08.12.2016.
 */


public class PostMessage {

    private String message;
    private String username;

    public PostMessage() {
    }

    public PostMessage(String message, String username) {
        this.message = message;
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String name) {
        this.message = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
