package com.app.radarvendor.Module;

import java.io.Serializable;
import java.util.List;

public class Service implements Serializable {
    private int id;
    private String name;
    private String category_name;
    private String service_time;
    private int category_id;
    private double price;
    private double offer_price;
    private int quantity;
    private String description;
    private String status;
    private String image_url;
    private List<Additions> additions;

    public Service(int id, String name, String category_name, String service_time, int category_id, double price, double offer_price, int quantity, String description, String status, String image_url, List<Additions> additions) {
        this.id = id;
        this.name = name;
        this.category_name = category_name;
        this.service_time = service_time;
        this.category_id = category_id;
        this.price = price;
        this.offer_price = offer_price;
        this.quantity = quantity;
        this.description = description;
        this.status = status;
        this.image_url = image_url;
        this.additions = additions;
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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getService_time() {
        return service_time;
    }

    public void setService_time(String service_time) {
        this.service_time = service_time;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOffer_price() {
        return offer_price;
    }

    public void setOffer_price(double offer_price) {
        this.offer_price = offer_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public List<Additions> getAdditions() {
        return additions;
    }

    public void setAdditions(List<Additions> additions) {
        this.additions = additions;
    }
}
