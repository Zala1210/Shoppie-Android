package com.example.shoppie;

import com.google.firebase.database.Exclude;

public class Shoe {
    private String mName;
    private String mImageUrl;
    private String mPrice;
    private String mKey;

    public Shoe(String name, String imageUrl, String price) {
        if(name.trim().equals("")){
            name = "No name";
        }
        mName = name;
        mImageUrl = imageUrl;
        mPrice = price;
    }

    public Shoe() {
        //Empty constructor
    }

    public String getShoeName() {
        return mName;
    }

    public void setShoeName(String shoeName) {
        mName = shoeName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        mPrice = price;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }
    public void setKey(String key){
        mKey = key;
    }
}
