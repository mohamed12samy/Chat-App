package com.example.chatapp;

public class User {

    private String Id;
    private String name;
    private String urlPhoto;
    private String email;

    public User(String id, String name, String urlPhoto, String email) {
        Id = id;
        this.name = name;
        this.urlPhoto = urlPhoto;
        this.email = email;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlPhoto() {
        return urlPhoto;
    }

    public void setUrlPhoto(String urlPhoto) {
        this.urlPhoto = urlPhoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
