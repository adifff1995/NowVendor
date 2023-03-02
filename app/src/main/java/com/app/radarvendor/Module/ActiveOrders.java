package com.app.radarvendor.Module;

import java.util.List;

public class ActiveOrders {
    private ActiveNotification notification;
    private List<OrderResponse> orders;
    private List<OrderFromUser> delivery_services;

    public ActiveNotification getNotification() {
        return notification;
    }

    public void setNotification(ActiveNotification notification) {
        this.notification = notification;
    }

    public List<OrderResponse> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderResponse> orders) {
        this.orders = orders;
    }

    public List<OrderFromUser> getDelivery_services() {
        return delivery_services;
    }

    public void setDelivery_services(List<OrderFromUser> delivery_services) {
        this.delivery_services = delivery_services;
    }
}
