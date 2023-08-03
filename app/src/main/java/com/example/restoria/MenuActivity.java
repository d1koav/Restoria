package com.example.restoria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.restoria.databinding.FragmentGalleryBinding;
import com.example.restoria.ui.gallery.GalleryViewModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MenuActivity extends AppCompatActivity {
    private Semaphore semaphore = new Semaphore(0);
    private FragmentGalleryBinding binding;
    private GalleryViewModel viewModel;
    private List<String> tilesList;
    private TileAdapter adapter;
    ResultSet resultSet = null;
    String result = null;
    Connection connection = null;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        gridView = findViewById(R.id.gridView2);
        tilesList = new ArrayList<>();
        adapter = new TileAdapter(this, tilesList);
        gridView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tileName) {
                Intent intent = new Intent(MenuActivity.this, DetailActivity.class);
                intent.putExtra("tileName",tileName);
                startActivityForResult(intent, 2);
            }
        });
        loadData();
    }

    // В MenuActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            String selectedItem = data.getStringExtra("selectedItem");
            Intent intent = new Intent();
            intent.putExtra("selectedItem", selectedItem);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
    private void loadData() {
        List<String> dataFromSource = getDataFromSource();
        if (dataFromSource != null && !dataFromSource.isEmpty()) {
            if (tilesList == null) {
                tilesList = new ArrayList<>();
            } else {
                tilesList.clear();
            }
            tilesList.addAll(dataFromSource);
            adapter.notifyDataSetChanged();
        }
    }

    private List<String> getDataFromSource() {
        List<String> valuesList = new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = DatabaseHelper.getConnection();
                    String selectQuery = "SELECT DISTINCT category FROM Menu";
                    resultSet = connection.createStatement().executeQuery(selectQuery);
                    while (resultSet.next()) {
                        result = resultSet.getString("category");
                        valuesList.add(result);
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
                    adapter.notifyDataSetChanged();
                    semaphore.release();
                }
            }
        });
        thread.start();
        try{
            semaphore.acquire();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return valuesList;
    }

}