package com.app.radarvendor.Module;

import java.io.Serializable;
import java.util.List;

public class Vendor implements Serializable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String longitude;
    private String latitude;
    private int type;
    private String service_id;
    private String service_description;
    private List<WorkingDay> work_days;
    private String work_time_from;
    private String work_time_to;
    private boolean verified;
    private String icon_url;
    private String token;
    private String id_no;
    private int city_id;
    private double radius_work;
    private String city_name;
    private String status_store;
    private String status_store_name;
    private List<Tag>tags;

    public Vendor(int id, String name, String email, String phone, String longitude, String latitude, int type, String service_id, String service_description,  List<WorkingDay>  work_days, String work_time_from, String work_time_to, boolean verified, String icon_url) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.longitude = longitude;
        this.latitude = latitude;
        this.type = type;
        this.service_id = service_id;
        this.service_description = service_description;
        this.work_days = work_days;
        this.work_time_from = work_time_from;
        this.work_time_to = work_time_to;
        this.verified = verified;
        this.icon_url = icon_url;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getService_description() {
        return service_description;
    }

    public void setService_description(String service_description) {
        this.service_description = service_description;
    }

    public  List<WorkingDay>  getWork_days() {
        return work_days;
    }

    public void setWork_days( List<WorkingDay>  work_days) {
        this.work_days = work_days;
    }

    public String getWork_time_from() {
        return work_time_from;
    }

    public void setWork_time_from(String work_time_from) {
        this.work_time_from = work_time_from;
    }

    public String getWork_time_to() {
        return work_time_to;
    }

    public void setWork_time_to(String work_time_to) {
        this.work_time_to = work_time_to;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getStatus_store() {
        return status_store;
    }

    public void setStatus_store(String status_store) {
        this.status_store = status_store;
    }

    public String getStatus_store_name() {
        return status_store_name;
    }

    public void setStatus_store_name(String status_store_name) {
        this.status_store_name = status_store_name;
    }

    public double getRadius_work() {
        return radius_work;
    }

    public void setRadius_work(double radius_work) {
        this.radius_work = radius_work;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
