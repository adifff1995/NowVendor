package com.app.radarvendor.Module;

public class PizzaIcon {
    private int id;
    private String image_url;

    public PizzaIcon(int id, String image_url) {
        this.id = id;
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


}
