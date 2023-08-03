package com.example.restoria;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class DetailActivity extends AppCompatActivity {
    private Semaphore semaphore = new Semaphore(0);
    ResultSet resultSet;
    PreparedStatement statement;
    Connection connection = null;
    Integer price, weight;
    String name;
    List<DetailDataModel> cookList;
    Button pay;
    private DetailAdapter adapter;
    String stringValue;
    TextView h;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        stringValue = intent.getStringExtra("tileName");
        h = findViewById(R.id.textView_CategoryDish);
        h.setText(stringValue);

        listView = findViewById(R.id.menu_dish_list);
        adapter = new DetailAdapter(this, cookList);
        listView.setAdapter(adapter);
        adapter.setOnItemClickListener(new DetailAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DetailDataModel data) {
                Intent intent = new Intent();
                intent.putExtra("selectedItem",data.getName());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        loadData();

    }
    private void loadData() {
        List<DetailDataModel> dataFromSource = getData();
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
    private List<DetailDataModel> getData() {
        List<DetailDataModel> data = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                connection = DatabaseHelper.getConnection();
                String selectQuery = "SELECT name, price, weight from Menu where category = ?";
                PreparedStatement statement1 = connection.prepareStatement(selectQuery);
                statement1.setString(1, stringValue);
                resultSet = statement1.executeQuery();
                while (resultSet.next()) {
                    name = resultSet.getString("name");
                    price = resultSet.getInt("price");
                    weight = resultSet.getInt("weight");
                    data.add(new DetailDataModel(name, String.valueOf(price), String.valueOf(weight), stringValue));
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
    @Override
    public void onBackPressed() {
        finish();
    }
}