package com.app.radarvendor.Module;

import java.io.Serializable;

public class ProductImages implements Serializable {
    private int id;
    private int store_id;
    private String product_id;
    private String image_url;
    private int status;

    public ProductImages(int id, int store_id, String product_id, String image_url, int status) {
        this.id = id;
        this.store_id = store_id;
        this.product_id = product_id;
        this.image_url = image_url;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStore_id() {
        return store_id;
    }

    public void setStore_id(int store_id) {
        this.store_id = store_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
