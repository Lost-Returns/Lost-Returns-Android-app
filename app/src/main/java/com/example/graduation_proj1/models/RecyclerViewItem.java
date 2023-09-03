package com.example.graduation_proj1.models;


public class RecyclerViewItem {
    private String contact;
    private String foundDate;
    private String foundLocation;
    private String imageUrl;
    private String itemType;
    private String location;
    private String owner;
    private String title;

    // 생성자, getter, setter 등 추가

    public void setContact(String contact){
        this.contact = contact;
    }

    public void setFoundDate(String foundDate){
        this.foundDate = foundDate;
    }

    public void setFoundLocation(String foundLocation){
        this.foundLocation = foundLocation;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setItemType(String itemType){
        this.itemType = itemType;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setOwner(String owner){
        this.owner = owner;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getContact(){
        return contact;
    }

    public String getFoundDate(){
        return foundDate;
    }

    public String getFoundLocation(){
        return foundLocation;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getItemType(){
        return itemType;
    }

    public String getLocation(){
        return location;
    }

    public String getOwner(){
        return owner;
    }

    public String getTitle(){
        return title;
    }

}
