package com.app.radarvendor.Module;

public class MessageEvent {
    private int flag;
    private String title;
    private OrderResponse orderResponse;

    public MessageEvent(int flag) {
        this.flag = flag;
    }

    public MessageEvent(int flag, OrderResponse orderResponse) {
        this.flag = flag;
        this.orderResponse = orderResponse;
    }

    public MessageEvent(String title) {
        this.title = title;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public OrderResponse getOrderResponse() {
        return orderResponse;
    }

    public void setOrderResponse(OrderResponse orderResponse) {
        this.orderResponse = orderResponse;
    }
}
