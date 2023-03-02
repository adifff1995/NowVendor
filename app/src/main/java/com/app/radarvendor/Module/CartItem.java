package com.app.radarvendor.Module;

import java.util.List;

public class CartItem {
    private String id;
    private String name;
    private String price;
    private String amount;
    private String img;
    private List<Additions>cartItemAdditionsList;

    public CartItem(String id, String name, String price, String amount, String img, List<Additions> cartItemAdditionsList) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.img = img;
        this.cartItemAdditionsList = cartItemAdditionsList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<Additions> getCartItemAdditionsList() {
        return cartItemAdditionsList;
    }

    public void setCartItemAdditionsList(List<Additions> cartItemAdditionsList) {
        this.cartItemAdditionsList = cartItemAdditionsList;
    }
}
