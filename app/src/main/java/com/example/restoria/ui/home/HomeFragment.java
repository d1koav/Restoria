package com.example.restoria.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.restoria.DatabaseHelper;
import com.example.restoria.TableAdapter;
import com.example.restoria.TablesDataModel;
import com.example.restoria.create_order;
import com.example.restoria.databinding.FragmentHomeBinding;
import com.example.restoria.orderDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class HomeFragment extends Fragment {
    private final Semaphore semaphore = new Semaphore(0);
    ResultSet resultSet, resultSet2;
    Connection connection = null;
    PreparedStatement statement = null;
    List<TablesDataModel> homeList;
    String order, status;
    Integer table_id;
    Boolean isFree;

    private FragmentHomeBinding binding;
    private TableAdapter adapter;
    private HomeViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        adapter = new TableAdapter(requireContext(), homeList);
        binding.tablesListView.setAdapter(adapter);
        adapter.setOnItemClickListener(data -> {
            Intent intent;
            if (data.getStatus().equals("Свободен")) {
                intent = new Intent(requireContext(), create_order.class);
                intent.putExtra("table_id", data.getTable());
            }else{
                intent = new Intent(requireContext(), orderDetails.class);
                intent.putExtra("order_id", data.getOrder().substring(7));
            }
            startActivity(intent);
            loadData();
        });
        loadData();
        viewModel.getDataList().observe(getViewLifecycleOwner(), newDataList -> adapter.updateData(newDataList));
        return root;
    }
    private void loadData() {
        List<TablesDataModel> dataFromSource = getDataFromSource();
        if (dataFromSource != null && !dataFromSource.isEmpty()) {
            if (homeList == null) {
                homeList = new ArrayList<>();
            } else {
                homeList.clear();
            }
            homeList.addAll(dataFromSource);
            adapter.updateData(homeList);
        }

    }
    public List<TablesDataModel> getDataFromSource() {

        List<TablesDataModel> data = new ArrayList<>();
        Thread thread = new Thread(() -> {
            try {
                connection = DatabaseHelper.getConnection();
                String selectQuery = "SELECT table_id, isFree FROM Tables ORDER BY table_id";
                resultSet = connection.createStatement().executeQuery(selectQuery);

                while (resultSet.next()) {
                    table_id = resultSet.getInt("table_id");
                    isFree = resultSet.getBoolean("isFree");
                    if (!isFree){
                        String secondQuery = "SELECT MAX(order_id) AS last_order_id FROM Orders WHERE table_id = ?";
                        statement = connection.prepareStatement(secondQuery);
                        statement.setInt(1, table_id);
                        resultSet2 = statement.executeQuery();
                        if (resultSet2.next()) {
                            String lastOrderId = String.valueOf(resultSet2.getInt("last_order_id"));
                            order = "Номер заказа: " + lastOrderId;
                            status = "Занят";
                        } else {
                            status = "Занят";
                            order = "Нет текущего заказа";
                        }
                    }else{
                        status = "Свободен";
                        order = "Нет текущего заказа";
                    }
                    data.add(new TablesDataModel("Стол № " + table_id, order,  status));
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
                if (statement != null) {
                    try {
                        statement.close();
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
            adapter.notifyDataSetChanged();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}