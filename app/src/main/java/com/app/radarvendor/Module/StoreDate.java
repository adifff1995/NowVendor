package com.app.radarvendor.Module;

public class StoreDate {
    private int id;
    private String name;
    private String type_service_name;
    private String service_description;
    private String icon_url;
    private int rating_avg;
    private String delivery_time;
    private int delivery_price;
    private int min_order;
    private int radius_work;
    private int status_store;
    private String status_store_name;

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

    public String getType_service_name() {
        return type_service_name;
    }

    public void setType_service_name(String type_service_name) {
        this.type_service_name = type_service_name;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public int getRating_avg() {
        return rating_avg;
    }

    public void setRating_avg(int rating_avg) {
        this.rating_avg = rating_avg;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public int getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(int delivery_price) {
        this.delivery_price = delivery_price;
    }

    public int getMin_order() {
        return min_order;
    }

    public void setMin_order(int min_order) {
        this.min_order = min_order;
    }

    public int getRadius_work() {
        return radius_work;
    }

    public void setRadius_work(int radius_work) {
        this.radius_work = radius_work;
    }

    public int getStatus_store() {
        return status_store;
    }

    public void setStatus_store(int status_store) {
        this.status_store = status_store;
    }

    public String getStatus_store_name() {
        return status_store_name;
    }

    public void setStatus_store_name(String status_store_name) {
        this.status_store_name = status_store_name;
    }
}
