package com.app.radarvendor.Module;

public class NavItems {
    private String id;
    private String title;
    private int icon;

    public NavItems(String id, String title, int icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

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

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
