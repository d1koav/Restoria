package com.example.restoria;

public class TablesDataModel {
    private String table;
    private String order;
    private String status;

    public TablesDataModel(String value, String description, String status) {
        this.table = value;
        this.order = description;
        this.status = status;
    }

    public String getTable() {
        return table;
    }

    public String getOrder() {
        return order;
    }

    public String getStatus() {
        return status;
    }

    public void setTable(String table){
        this.table = table;
    }
    public void setOrder(String order){
        this.order = order;
    }
    public void setStatus(String status){
        this.status = status;
    }

}

