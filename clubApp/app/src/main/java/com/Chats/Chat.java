package com.Chats;

import java.util.ArrayList;

public class Chat {
    private String chatId;
    private ArrayList<String> users;


    public Chat(){

    }

    public Chat(String chatId, ArrayList<String> users){
        this.chatId = chatId;
        this.users = users;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

}
