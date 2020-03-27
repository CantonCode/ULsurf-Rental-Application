package com.example.clubapp;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {

        private String sender;
        private String receiver;
        private String message;
        private String Type;
        private Date Time;




    public Message() {
            //empty constructor needed
        }

        public Message(String sender, String receiver, String message, Date Time, String Type ) {
            this.sender = sender;
            this.receiver = receiver;
            this.message = message;
            this.Time = Time;
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

        public String getType() { return Type; }

        public void setType(String type) { Type = type; }

        public Date getTime() { return Time; }

        public void setTime(Date time) { Time = time; }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

