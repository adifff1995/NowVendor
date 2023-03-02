package com.app.radarvendor.Module;

import java.io.Serializable;

public class WorkingDay implements Serializable {
    private String day_name;
    private String day;
    private String from;
    private String to;
    private int selected;

    public WorkingDay(String day,String day_name, int selected) {
        this.day = day;
        this.selected = selected;
        this.day_name = day_name;
    }

    public String getDay_name() {
        return day_name;
    }

    public void setDay_name(String day_name) {
        this.day_name = day_name;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }
}
