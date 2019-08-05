package me.gfred.travelmantics;

import java.io.Serializable;

public class TravelDeal implements Serializable {
    private String id;
    private String title;
    private String description;
    private String price;
    private String imageUrl;
    private String imageName;

    TravelDeal() {
    }

    public TravelDeal(String title, String description, String price, String imageUrl, String imageName) {
        this.setId(id);
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setImageUrl(imageUrl);
        this.setImageName(imageName);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    String getPrice() {
        return price;
    }

    void setPrice(String price) {
        this.price = price;
    }

    String getId() {
        return id;
    }

    void setId(String id) {
        this.id = id;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
