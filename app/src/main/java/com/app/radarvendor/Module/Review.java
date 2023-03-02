package com.app.radarvendor.Module;

public class Review {
    private String user_name;
    private float rating;
    private String rating_text;

    public Review(String user_name, float rating, String rating_text) {
        this.user_name = user_name;
        this.rating = rating;
        this.rating_text = rating_text;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getRating_text() {
        return rating_text;
    }

    public void setRating_text(String rating_text) {
        this.rating_text = rating_text;
    }
}
