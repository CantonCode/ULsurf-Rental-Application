package com.Chats;

import java.io.Serializable;
import java.util.Date;

public class Message {

    private String sender;
    private String receiver;
    private String message;
    private Date Time;
    private boolean seen;




    public Message() {
        //empty constructor needed
    }

    public Message(String sender, String receiver, String message, Date Time) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.Time = Time;
        this.seen = seen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public Date getTime() { return Time; }

    public void setTime(Date time) { Time = time; }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSeen() { return seen; }

    public void setSeen(boolean seen) { this.seen = seen; }
}