package com.example.restoria;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class cook extends AppCompatActivity {
    private Semaphore semaphore = new Semaphore(0);
    ResultSet resultSet, resultSet2;
    PreparedStatement statement;
    Connection connection = null;
    Integer dish_id;
    String order_id, status, count_dish, dish_name;
    List<CookDataModel> cookList;

    private ListView listView;
    private CookAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cook);
        listView = findViewById(R.id.ListView_cook);
        adapter = new CookAdapter(this, cookList);
        listView.setAdapter(adapter);
        loadData();
    }

    private void loadData() {
        List<CookDataModel> dataFromSource = getData();
        if (dataFromSource != null && !dataFromSource.isEmpty()) {
            if (cookList == null) {
                cookList = new ArrayList<>();
            } else {
                cookList.clear();
            }
            cookList.addAll(dataFromSource);
            adapter.updateData(cookList);
        }

    }
    private List<CookDataModel> getData() {
        List<CookDataModel> data = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                connection = DatabaseHelper.getConnection();
                String selectQuery = "SELECT  order_id, dish_id, status, count_dish FROM Orders_dish ";
                resultSet = connection.createStatement().executeQuery(selectQuery);
                while (resultSet.next()) {
                    order_id = String.valueOf(resultSet.getInt("order_id"));
                    dish_id = resultSet.getInt("dish_id");
                    status = resultSet.getString("status");
                    count_dish = String.valueOf(resultSet.getInt("count_dish"));
                    String sql2 = "Select name from Menu where dish_id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql2);
                    statement.setInt(1,dish_id);
                    resultSet2 = statement.executeQuery();
                    if (resultSet2.next()){
                        dish_name = resultSet2.getString("name");
                    }
                    if (!status.equals("Подано")){
                        data.add(new CookDataModel("Заказ №" + order_id, dish_name, "Количество блюд: " + count_dish, status));
                    }



                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (resultSet != null) {
                    try {
                        resultSet.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                DatabaseHelper.closeConnection(connection);
                semaphore.release();
            }
        });
        thread.start();
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }
}