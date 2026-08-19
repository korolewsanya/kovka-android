package com.example.kovka;

public class Product {
    private String tags;
    private String path;
    private String image;
    private String id;
    private String category;

    public Product(String image, String tags, String path, String id, String category) {
        this.image = image;
        this.tags = tags;
        this.path = path;
        this.id = id;
        this.category = category;
    }
    public String getImage() {
        return image;
    }
    public String getTags() {
        return tags;
    }
    public String getPath() {
        return path;
    }
    public String getId() { return id; }
    public String getCategory() { return category; }
}
