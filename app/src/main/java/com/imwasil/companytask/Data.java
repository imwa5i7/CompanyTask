package com.imwasil.companytask;

public class Data {

    private String comment;
    private String image;
    private String id;

    public Data() {
    }


    public Data(String comment, String image) {
        this.comment = comment;
        this.image = image;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
