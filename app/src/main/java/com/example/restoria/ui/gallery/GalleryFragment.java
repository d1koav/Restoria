package com.example.restoria.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.restoria.DatabaseHelper;
import com.example.restoria.DetailActivity;
import com.example.restoria.DetailAdapter;
import com.example.restoria.DetailDataModel;
import com.example.restoria.OrderStatusAdapter;
import com.example.restoria.OrdersStatusDataModel;
import com.example.restoria.TablesDataModel;
import com.example.restoria.TileAdapter;
import com.example.restoria.databinding.FragmentGalleryBinding;
import com.example.restoria.orderDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GalleryFragment extends Fragment {
    private Semaphore semaphore = new Semaphore(0);
    private FragmentGalleryBinding binding;
    private GalleryViewModel viewModel;
    private List<String> tilesList;
    private TileAdapter adapter;
    ResultSet resultSet = null;
    String result = null;
    Connection connection = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        tilesList = new ArrayList<>();
        adapter = new TileAdapter(requireContext(), tilesList);
        binding.gridView.setAdapter(adapter);
        adapter.setOnItemClickListener(new TileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String tileName) {
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("tileName",tileName);
                startActivityForResult(intent, 2);
            }
        });
        loadData();

        return view;
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
        // Здесь вы можете получить данные из вашего источника данных
        // Возвращаем заглушечные данные для примера
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}