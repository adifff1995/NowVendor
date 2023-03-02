package com.app.radarvendor.Module;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    private int id;
    private String name;
    private String category_name;
    private String service_time;
    private int category_id;
    private double price;
    private double unitPrice;
    private double offer_price;
    private int quantity;
    private String description;
    private String status;
    private boolean has_details;
    private boolean isChecked;
    private List<Additions> additions;
    private List<Additions> unique_additions;
    private List<ProductImages> images;

    public Product() {
    }

    public Product(int id, String name, String category_name, int category_id, double price, double offer_price, int quantity, String description, String status, List<Additions> additions, List<ProductImages> images) {
        this.id = id;
        this.name = name;
        this.category_name = category_name;
        this.category_id = category_id;
        this.price = price;
        this.offer_price = offer_price;
        this.quantity = quantity;
        this.description = description;
        this.status = status;
        this.additions = additions;
        this.images = images;
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

    public List<Additions> getAdditions() {
        return additions;
    }

    public void setAdditions(List<Additions> additions) {
        this.additions = additions;
    }

    public List<ProductImages> getImages() {
        return images;
    }

    public void setImages(List<ProductImages> images) {
        this.images = images;
    }

    public String getCategory_name() {
        return category_name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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

    public boolean isHas_details() {
        return has_details;
    }

    public void setHas_details(boolean has_details) {
        this.has_details = has_details;
    }

    public List<Additions> getUnique_additions() {
        return unique_additions;
    }

    public void setUnique_additions(List<Additions> unique_additions) {
        this.unique_additions = unique_additions;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
