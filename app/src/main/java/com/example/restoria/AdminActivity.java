package com.example.restoria;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AdminActivity extends AppCompatActivity {
    private Semaphore semaphore = new Semaphore(0);
    ResultSet resultSet;
    Connection connection = null;
    String user_id, name, surname, enter_key;
    List<AdminDataModel> adminList;

    private ListView listView;
    private AdminAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        listView = findViewById(R.id.ListView_admin);
        adapter = new AdminAdapter(this, adminList);
        listView.setAdapter(adapter);
        loadData();
    }
    private void loadData() {
        List<AdminDataModel> dataFromSource = getData();
        if (dataFromSource != null && !dataFromSource.isEmpty()) {
            if (adminList == null) {
                adminList = new ArrayList<>();
            } else {
                adminList.clear();
            }
            adminList.addAll(dataFromSource);
            adapter.updateData(adminList);
        }

    }
    private List<AdminDataModel> getData() {
        List<AdminDataModel> data = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                connection = DatabaseHelper.getConnection();
                String selectQuery = "SELECT user_id, name, surname, enter_key FROM Staff";
                resultSet = connection.createStatement().executeQuery(selectQuery);
                while (resultSet.next()) {
                    user_id = String.valueOf(resultSet.getInt("user_id"));
                    name = resultSet.getString("name");
                    surname = resultSet.getString("surname");
                    enter_key = resultSet.getString("enter_key");
                    data.add(new AdminDataModel(name+" "+surname, user_id, enter_key));
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
