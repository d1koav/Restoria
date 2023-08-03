package com.example.restoria;

public class createOrderDataModel {
    private String dish_name;
    private String dish_count;
    private String price;

    public createOrderDataModel(String dish_name, String dish_count, String price) {
        this.dish_count = dish_count;
        this.dish_name = dish_name;
        this.price = price;
    }

    public String getDish_name() {
        return dish_name;
    }

    public String getDish_count() {
        return dish_count;
    }

    public String getPrice() {
        return price;
    }

    public void setDish_name(String dish_name){
            this.dish_name = dish_name;
    }
    public void setDish_count(String dish_count){
        this.dish_count = dish_count;
    }
    public void setPrice(String price){
        this.price = price;
    }

}
