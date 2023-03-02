package com.app.radarvendor.Module;

import com.google.gson.annotations.SerializedName;

public class ActiveNotification {
    private String id;
    private String title;
    private String body;
    private String click_action;
    private boolean is_read;
    private int type;
    private int date_id;


    @SerializedName("data")
    private Object orders;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDate_id() {
        return date_id;
    }

    public void setDate_id(int date_id) {
        this.date_id = date_id;
    }

    public Object getOrders() {
        return orders;
    }

    public void setOrders(Object orders) {
        this.orders = orders;
    }
}
