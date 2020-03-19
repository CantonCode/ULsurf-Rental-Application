package com.example.clubapp;

public class Equipment {
    private int equipmentId;
    private String equipmentName;
    private String description;
    private String size;
    private boolean rented;
    private String imageUrl;

    public Equipment(){
        //blank constructor
    }

    public Equipment(int equipmentId,String equipmentName, String description, String size, boolean rented, String imageUrl){
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.description = description;
        this.size = size;
        this.rented = rented;
        this.imageUrl = imageUrl;
    }

    public int getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(int equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
