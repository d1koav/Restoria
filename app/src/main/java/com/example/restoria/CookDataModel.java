package com.example.restoria;

public class CookDataModel {
    private String order_ID;
    private String DishName;
    private String countDish;
    private String status;

    public CookDataModel(String order_ID, String DishName, String countDish, String status) {
        this.order_ID = order_ID;
        this.DishName = DishName;
        this.countDish = countDish;
        this.status = status;
    }

    public String getOrder_ID() {
        return order_ID;
    }

    public String getDishName() {
        return DishName;
    }

    public String getCountDish() {
        return countDish;
    }
    public String getStatus() {
        return status;
    }

    public void setOrder_ID(String order_ID){
        this.order_ID = order_ID;
    }
    public void setDishName(String dishName){
        this.DishName = dishName;
    }
    public void setCountDish(String countDish){
        this.countDish = countDish;
    }
    public void setStatus(String status){
        this.status = status;
    }

}
