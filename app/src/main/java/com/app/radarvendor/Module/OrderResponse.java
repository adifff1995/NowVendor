package com.app.radarvendor.Module;

import java.io.Serializable;
import java.util.List;

public class OrderResponse implements Serializable {
    private int id;
    private int user_id;
    private String location_id;
    private String store_id;
    private String delivery_id;
    private String sub_total;
    private String discount;
    private String delivery_service;
    private String total;
    private String store_status;
    private String store_status_name;
    private String delivery_status;
    private String user_status;
    private String payment_method;
    private String name;
    private String phone;
    private String dtl_other;
    private String delivery_time;
    private String distance;
    private List<OrderItem> order_item;
    private String rating;
    private String rating_text;
    private String order_date;
    private double user_latitude;
    private double user_longitude;
    private double store_latitude;
    private double store_longitude;
    private StoreDate store_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getLocation_id() {
        return location_id;
    }

    public void setLocation_id(String location_id) {
        this.location_id = location_id;
    }

    public String getStore_id() {
        return store_id;
    }

    public void setStore_id(String store_id) {
        this.store_id = store_id;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDelivery_service() {
        return delivery_service;
    }

    public void setDelivery_service(String delivery_service) {
        this.delivery_service = delivery_service;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStore_status() {
        return store_status;
    }

    public void setStore_status(String store_status) {
        this.store_status = store_status;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<OrderItem> getOrder_item() {
        return order_item;
    }

    public void setOrder_item(List<OrderItem> order_item) {
        this.order_item = order_item;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRating_text() {
        return rating_text;
    }

    public void setRating_text(String rating_text) {
        this.rating_text = rating_text;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getDtl_other() {
        return dtl_other;
    }

    public void setDtl_other(String dtl_other) {
        this.dtl_other = dtl_other;
    }

    public double getUser_latitude() {
        return user_latitude;
    }

    public void setUser_latitude(double user_latitude) {
        this.user_latitude = user_latitude;
    }

    public double getUser_longitude() {
        return user_longitude;
    }

    public void setUser_longitude(double user_longitude) {
        this.user_longitude = user_longitude;
    }

    public double getStore_latitude() {
        return store_latitude;
    }

    public void setStore_latitude(double store_latitude) {
        this.store_latitude = store_latitude;
    }

    public double getStore_longitude() {
        return store_longitude;
    }

    public void setStore_longitude(double store_longitude) {
        this.store_longitude = store_longitude;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStore_status_name() {
        return store_status_name;
    }

    public void setStore_status_name(String store_status_name) {
        this.store_status_name = store_status_name;
    }

    public StoreDate getStore_date() {
        return store_date;
    }

    public void setStore_date(StoreDate store_date) {
        this.store_date = store_date;
    }
}
