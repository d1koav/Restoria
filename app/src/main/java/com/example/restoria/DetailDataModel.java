package com.example.restoria;

public class DetailDataModel {
    private String name;
    private  String category;
    private String price;
    private String weight;

    public DetailDataModel( String name, String price, String weight, String category) {
        this.name = name;
        this.price = price;
        this.weight = weight;
        this.category = category;
    }

    public String getName() {
        return name;
    }
    public String getCategory(){return category;}
    public String getPrice() {
        return price;
    }
    public String getWeight() {
        return weight;
    }

    public void setName(String name){
        this.name = name;
    }
    public void setPrice(String price){
        this.price = price;
    }
    public void setWeight(String weight){
        this.weight = weight;
    }
    public void setCategory(String category){
        this.category = category;
    }
}
