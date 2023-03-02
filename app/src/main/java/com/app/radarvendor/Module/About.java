package com.app.radarvendor.Module;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class About {
    private String about_us;
    private String privacy_policy;
    @SerializedName("pizza_extras")
    private List<PizzaIcon> pizzaIconList;

    public About(String about_us, String privacy_policy) {
        this.about_us = about_us;
        this.privacy_policy = privacy_policy;
    }

    public String getAbout_us() {
        return about_us;
    }

    public void setAbout_us(String about_us) {
        this.about_us = about_us;
    }

    public String getPrivacy_policy() {
        return privacy_policy;
    }

    public void setPrivacy_policy(String privacy_policy) {
        this.privacy_policy = privacy_policy;
    }

    public List<PizzaIcon> getPizzaIconList() {
        return pizzaIconList;
    }

    public void setPizzaIconList(List<PizzaIcon> pizzaIconList) {
        this.pizzaIconList = pizzaIconList;
    }
}
