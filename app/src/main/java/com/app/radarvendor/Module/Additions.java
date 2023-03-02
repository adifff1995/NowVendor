package com.app.radarvendor.Module;

import java.io.Serializable;

public class Additions implements Serializable {
    private String name;
    private String price;
    private String pizza_id;

    public Additions() {
    }

    public Additions(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPizza_id() {
        return pizza_id;
    }

    public void setPizza_id(String pizza_id) {
        this.pizza_id = pizza_id;
    }
}
