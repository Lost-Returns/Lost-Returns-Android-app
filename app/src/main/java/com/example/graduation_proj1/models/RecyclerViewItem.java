package com.example.graduation_proj1.models;
import android.os.Parcel;
import android.os.Parcelable;

public class RecyclerViewItem implements Parcelable {
    private String contact;
    private String foundDate;
    private String foundLocation;
    private String imageUrl;
    private String itemType;
    private String location;
    private String owner;
    private String title;

    public RecyclerViewItem() {
        // 기본 생성자
    }

    public RecyclerViewItem(Parcel in) {
        contact = in.readString();
        foundDate = in.readString();
        foundLocation = in.readString();
        imageUrl = in.readString();
        itemType = in.readString();
        location = in.readString();
        owner = in.readString();
        title = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(contact);
        dest.writeString(foundDate);
        dest.writeString(foundLocation);
        dest.writeString(imageUrl);
        dest.writeString(itemType);
        dest.writeString(location);
        dest.writeString(owner);
        dest.writeString(title);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RecyclerViewItem> CREATOR = new Creator<RecyclerViewItem>() {
        @Override
        public RecyclerViewItem createFromParcel(Parcel in) {
            return new RecyclerViewItem(in);
        }

        @Override
        public RecyclerViewItem[] newArray(int size) {
            return new RecyclerViewItem[size];
        }
    };

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
