package com.Rental;

public class Rentals {
    private String imageUrl;
    private String boardName;
    private String date;

    public Rentals(String imageResource, String text1, String text2) {
        imageUrl = imageResource;
        boardName = text1;
        date = text2;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBoardName() {
        return boardName;
    }

    public String getDate() {
        return date;
    }
}
