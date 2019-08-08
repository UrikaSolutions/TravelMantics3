package com.urikadev.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {

    private String id;
    private String title;
    private String descprtion;
    private String price;
    private String imageUrl;
    private String ImageName;

    public TravelDeal()
    {}


    public TravelDeal(String title, String descprtion, String price, String imageUrl,String ImageName) {
        this.setId(id);
        this.setTitle(title);
        this.setDescprtion(descprtion);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
        this.setImageName(ImageName);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescprtion() {
        return descprtion;
    }

    public void setDescprtion(String descprtion) {
        this.descprtion = descprtion;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }
}
