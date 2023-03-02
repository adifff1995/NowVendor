package com.app.radarvendor.Module;

public class NotificationItem {
    private int id;
    private String title;
    private String body;
    private String click_action;
    private boolean is_read;
    private String time;
    private int type;
    private NotificationData data;

    public NotificationItem(int id, String title, String body, String click_action, boolean is_read, String time, int type, NotificationData data) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.click_action = click_action;
        this.is_read = is_read;
        this.time = time;
        this.type = type;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }
}
