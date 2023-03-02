package com.app.radarvendor.Module;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Reservations implements Serializable {
    private int id;
    private int user_id;
    private int service_id;
    private String service_name;
    private String service_description;
    private String service_image_url;
    @SerializedName("DateAndTime")
    private String dateAndTime;
    private String booking_date;
    private String booking_time;
    private String status_time;
    private String store_status;
    private String store_status_name;
    private Additions additions;
    private double rating_avg;

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

    public int getService_id() {
        return service_id;
    }

    public void setService_id(int service_id) {
        this.service_id = service_id;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public String getService_image_url() {
        return service_image_url;
    }

    public void setService_image_url(String service_image_url) {
        this.service_image_url = service_image_url;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(String booking_date) {
        this.booking_date = booking_date;
    }

    public String getBooking_time() {
        return booking_time;
    }

    public void setBooking_time(String booking_time) {
        this.booking_time = booking_time;
    }

    public String getStatus_time() {
        return status_time;
    }

    public void setStatus_time(String status_time) {
        this.status_time = status_time;
    }

    public Additions getAdditions() {
        return additions;
    }

    public void setAdditions(Additions additions) {
        this.additions = additions;
    }

    public double getRating_avg() {
        return rating_avg;
    }

    public void setRating_avg(double rating_avg) {
        this.rating_avg = rating_avg;
    }

    public String getStore_status() {
        return store_status;
    }

    public void setStore_status(String store_status) {
        this.store_status = store_status;
    }

    public String getStore_status_name() {
        return store_status_name;
    }

    public void setStore_status_name(String store_status_name) {
        this.store_status_name = store_status_name;
    }
}
