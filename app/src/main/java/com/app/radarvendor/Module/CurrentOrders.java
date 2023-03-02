package com.app.radarvendor.Module;

import java.util.List;

public class CurrentOrders {
    private int id;
    private UserDate user_date;
    private StoreDate store_date;
    private double store_latitude;
    private double store_longitude;
    private double user_latitude;
    private double user_longitude;
    private String dtl_other;
    private int delivery_id;
    private double sub_total;
    private int discount;
    private int delivery_service;
    private double total;
    private String store_status_name;
    private String store_status;
    private String delivery_status_name;
    private String delivery_status;
    private String user_status_name;
    private String user_status;
    private String payment_method;
    private String name;
    private String phone;
    private List<OrderItem> order_item;
    private int rating;
    private String rating_text;
    private String order_date;
    private String notes;
    private String delivery_time;
    private String distance;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDate getUser_date() {
        return user_date;
    }

    public void setUser_date(UserDate user_date) {
        this.user_date = user_date;
    }

    public StoreDate getStore_date() {
        return store_date;
    }

    public void setStore_date(StoreDate store_date) {
        this.store_date = store_date;
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

    public String getDtl_other() {
        return dtl_other;
    }

    public void setDtl_other(String dtl_other) {
        this.dtl_other = dtl_other;
    }

    public int getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(int delivery_id) {
        this.delivery_id = delivery_id;
    }

    public double getSub_total() {
        return sub_total;
    }

    public void setSub_total(double sub_total) {
        this.sub_total = sub_total;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getDelivery_service() {
        return delivery_service;
    }

    public void setDelivery_service(int delivery_service) {
        this.delivery_service = delivery_service;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getStore_status_name() {
        return store_status_name;
    }

    public void setStore_status_name(String store_status_name) {
        this.store_status_name = store_status_name;
    }

    public String getStore_status() {
        return store_status;
    }

    public void setStore_status(String store_status) {
        this.store_status = store_status;
    }

    public String getDelivery_status_name() {
        return delivery_status_name;
    }

    public void setDelivery_status_name(String delivery_status_name) {
        this.delivery_status_name = delivery_status_name;
    }

    public String getDelivery_status() {
        return delivery_status;
    }

    public void setDelivery_status(String delivery_status) {
        this.delivery_status = delivery_status;
    }

    public String getUser_status_name() {
        return user_status_name;
    }

    public void setUser_status_name(String user_status_name) {
        this.user_status_name = user_status_name;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
}
