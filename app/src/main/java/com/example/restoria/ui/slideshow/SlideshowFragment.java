package com.example.restoria.ui.slideshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.restoria.DatabaseHelper;
import com.example.restoria.OrderStatusAdapter;
import com.example.restoria.OrdersStatusDataModel;
import com.example.restoria.databinding.FragmentSlideshowBinding;
import com.example.restoria.orderDetails;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class SlideshowFragment extends Fragment {
    public interface DataLoadListener {
        void onDataLoaded(List<OrdersStatusDataModel> data);
    }
    private SlideshowFragment.DataLoadListener dataLoadListener;

    public void setOnDataLoadListener(SlideshowFragment.DataLoadListener listener) {
        dataLoadListener = listener;
    }
    private Semaphore semaphore = new Semaphore(0);
    Connection connection;
    PreparedStatement statement;
    ResultSet resultSet, rs;
    String status;
    Integer table_id, order_id, sum, user_id;
    Boolean isPayed;
    private FragmentSlideshowBinding binding;
    private OrderStatusAdapter adapter;
    private SlideshowViewModel viewModel;
    private List<OrdersStatusDataModel> order;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);


        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(SlideshowViewModel.class);
        adapter = new OrderStatusAdapter(requireContext(), order);
        binding.SLV.setAdapter(adapter);
        adapter.setOnItemClickListener(new OrderStatusAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(OrdersStatusDataModel data) {
                Intent intent = new Intent(requireContext(), orderDetails.class);
                intent.putExtra("order_id", data.getOrder());
                startActivity(intent);
            }
        });

        loadData();
        viewModel.getDataList().observe(getViewLifecycleOwner(), newDataList -> {
            adapter.updateData(newDataList);
        });

        return root;
    }

    private void loadData() {
        List<OrdersStatusDataModel> dataFromSource = getDataFromSource();
        if (dataFromSource != null && !dataFromSource.isEmpty()) {
            if (order == null) {
                order = new ArrayList<>();
            } else {
                order.clear();
            }
            order.addAll(dataFromSource);
            adapter.updateData(order);
        }
    }
    public List<OrdersStatusDataModel> getDataFromSource() {
        List<OrdersStatusDataModel> data = new ArrayList<>();
        String enter_key = "aaa";
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connection = DatabaseHelper.getConnection();
                    String user_q = "Select user_id From Staff Where enter_key = ?";
                    PreparedStatement statement1 = connection.prepareStatement(user_q);
                    statement1.setString(1, enter_key);
                    rs = statement1.executeQuery();
                    if (rs.next()){
                        user_id = rs.getInt("user_id");
                    }
                    statement1.close();
                    rs.close();
                    String selectQuery = "SELECT order_id, table_id, sum, isPayed FROM Orders Where user_id = ? and isPayed = False";
                    PreparedStatement statement = connection.prepareStatement(selectQuery);
                    statement.setInt(1, user_id);
                    resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        order_id = resultSet.getInt("order_id");
                        table_id = resultSet.getInt("table_id");
                        isPayed = resultSet.getBoolean("isPayed");
                        sum = resultSet.getInt("sum");
                        if (isPayed){
                            status = "Оплачен";
                        }else{
                            status = "Не оплачен";
                        }
                        data.add(new OrdersStatusDataModel("Заказ №" + String.valueOf(order_id), "Стол №" + String.valueOf(table_id), status,"Сумма заказа: " + String.valueOf(sum) + " рублей"));
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
                    adapter.notifyDataSetChanged();
                    semaphore.release();
                }
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}