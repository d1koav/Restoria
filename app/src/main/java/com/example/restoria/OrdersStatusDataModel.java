package com.example.restoria;

public class OrdersStatusDataModel {
    private String table;
    private String order;
    private String status;
    private String sum;

    public OrdersStatusDataModel(String order, String table, String status, String sum) {
        this.table = table;
        this.order = order;
        this.status = status;
        this.sum = sum;
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
    public String getSum() {
        return sum;
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
    public void setSum(String sum){
        this.sum = sum;
    }

}
