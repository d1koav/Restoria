package com.example.restoria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class create_order extends AppCompatActivity {
    private final Semaphore semaphore = new Semaphore(0);
    ResultSet resultSet, resultSet2;
    Connection connection = null;
    Integer dish_id, price, count_dish;
    String order_id, status,  dish_name;
    List<createOrderDataModel> cookList;
    String stringValue;
    private ListView listView;
    TextView tb;
    Button add_dish, btn_create_order;
    private createOrderAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        Intent intent = getIntent();
        stringValue = intent.getStringExtra("table_id").substring(7);
        tb = findViewById(R.id.textView_create_tableID);
        tb.setText(stringValue);
        listView = findViewById(R.id.order_list_view);
        adapter = new createOrderAdapter(this, cookList);
        listView.setAdapter(adapter);
        add_dish = findViewById(R.id.button_add_dish);
        btn_create_order = findViewById(R.id.button_create_order);
        Thread thread = new Thread(() -> {
            try{
                connection = DatabaseHelper.getConnection();
                String insert = "Insert INTO Orders (user_id, table_id, sum, ispayed) VALUES (?, ?, 0, False) Returning order_id;";
                PreparedStatement statement1 = connection.prepareStatement(insert);
                statement1.setInt(1, 1);
                statement1.setInt(2, Integer.parseInt(stringValue));
                ResultSet rs = statement1.executeQuery();
                if (rs.next()) {
                    order_id = String.valueOf(rs.getInt("order_id"));
                }
            }catch (SQLException e) {
                e.printStackTrace();
            }finally {
                DatabaseHelper.closeConnection(connection);
                adapter.notifyDataSetChanged();
                semaphore.release();
            }
        });
        thread.start();
        try{
            semaphore.acquire();
            loadData();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        loadData();
        add_dish.setOnClickListener(v -> {
            Intent intent1 = new Intent(create_order.this, MenuActivity.class);
            startActivityForResult(intent1, 1);
        });
        btn_create_order.setOnClickListener(v -> {
            finish();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String selectedItem = data.getStringExtra("selectedItem");
            Thread thread = new Thread(() -> {
                try{
                    connection = DatabaseHelper.getConnection();
                    String text_sel = "Select dish_id From Menu where name = ?";
                    dish_id = -1;
                    PreparedStatement statement3 = connection.prepareStatement(text_sel);
                    statement3.setString(1,selectedItem);
                    ResultSet rs = statement3.executeQuery();
                    if(rs.next()){
                        dish_id = rs.getInt("dish_id");
                    }
                    String insert = "Insert INTO Orders_dish (order_id, dish_id, status, count_dish) VALUES (?, ?, 'Приготовление', 1);";
                    PreparedStatement statement1 = connection.prepareStatement(insert);
                    statement1.setInt(1, Integer.parseInt(order_id));
                    statement1.setInt(2, dish_id);
                    int s = statement1.executeUpdate();
                }catch (SQLException e) {
                    e.printStackTrace();
                }finally {
                    DatabaseHelper.closeConnection(connection);
                    semaphore.release();
                }
            });
            thread.start();
            try{
                semaphore.acquire();
                loadData();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void loadData() {
        List<createOrderDataModel> dataFromSource = getData();
        if (!dataFromSource.isEmpty()) {
            if (cookList == null) {
                cookList = new ArrayList<>();
            } else {
                cookList.clear();
            }
            cookList.addAll(dataFromSource);
            adapter.updateData(cookList);
        }

    }
    private List<createOrderDataModel> getData() {
        List<createOrderDataModel> data = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                connection = DatabaseHelper.getConnection();
                String selectQuery = "SELECT  order_id, dish_id, status, count_dish FROM Orders_dish Where order_id = (Select max(order_id) from Orders where table_id = ?)";
                PreparedStatement statement1 = connection.prepareStatement(selectQuery);
                statement1.setInt(1, Integer.parseInt(stringValue));
                resultSet = statement1.executeQuery();
                while (resultSet.next()) {
                    order_id = String.valueOf(resultSet.getInt("order_id"));
                    dish_id = resultSet.getInt("dish_id");
                    status = resultSet.getString("status");
                    count_dish = resultSet.getInt("count_dish");
                    String sql2 = "Select name, price from Menu where dish_id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql2);
                    statement.setInt(1,dish_id);
                    resultSet2 = statement.executeQuery();
                    if (resultSet2.next()){
                        dish_name = resultSet2.getString("name");
                        price = resultSet2.getInt("price");
                    }
                    data.add(new createOrderDataModel(dish_name, String.valueOf(count_dish), String.valueOf(price*count_dish)));

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