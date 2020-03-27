package com.example.clubapp;

public class Chat {
    private String chatId;
    private String user1;
    private String user2;


    public Chat(){

    }

    public Chat(String chatId, String user1, String user2){
        this.chatId = chatId;
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

}
