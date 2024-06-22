package com.y_social_media_app;

public class ModalPost {
    public ModalPost(){
    }

    public  ModalPost(String title, String description, String timestamp){
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    String uid;
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    String postImage;

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }
}
