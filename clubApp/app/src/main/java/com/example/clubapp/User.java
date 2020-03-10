package com.example.clubapp;

public class User {

    private int userId;
    private String userName;
    private String studentNumber;
    private String photoUrl;
    private boolean admin;


    public User() {
        //empty constructor needed
    }

    public User(String userName, int userId, String studentNumber, String photoUrl, boolean admin){
        this.userName = userName;
        this.userId = userId;
        this.studentNumber = studentNumber;
        this.photoUrl = photoUrl;
        this.admin = admin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}