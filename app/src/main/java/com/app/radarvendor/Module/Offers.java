package com.app.radarvendor.Module;

import java.util.List;

public class Offers {
    private int id;
    private String name;
    private int store_id;
    private double old_price;
    private double offer_price;
    private String description;
    private String status;
    private String image_url;
    private List<Product> products;

    public Offers(int id, String name, int store_id, double old_price, double offer_price, String description, String status, String image_url, List<Product> products) {
        this.id = id;
        this.name = name;
        this.store_id = store_id;
        this.old_price = old_price;
        this.offer_price = offer_price;
        this.description = description;
        this.status = status;
        this.image_url = image_url;
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public double getOld_price() {
        return old_price;
    }

    public void setOld_price(double old_price) {
        this.old_price = old_price;
    }

    public double getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(double offer_price) {
        this.offer_price = offer_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
