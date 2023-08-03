package com.example.restoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class orderDetailsAdapter extends BaseAdapter {
    Connection connection;
    PreparedStatement statement;
    public interface OnItemClickListener {
        void onButtonClick(int position);
    }
    private Context context;
    private List<CookDataModel> dataList;
    public void setDataList(List<CookDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }
    public orderDetailsAdapter(Context context, List<CookDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    private Semaphore semaphore = new Semaphore(0);
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return dataList != null ? dataList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.order_details_list_item, parent, false);
            holder = new ViewHolder();
            holder.textOrderID = convertView.findViewById(R.id.textOrder_ID_D);
            holder.textDishName = convertView.findViewById(R.id.textDish_Name_D);
            holder.textCountDish = convertView.findViewById(R.id.text_Count_Dish_D);
            holder.textStatus = convertView.findViewById(R.id.textStatus_Dish_D);
            holder.buttonReady = convertView.findViewById(R.id.button_Ready_D);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CookDataModel data = dataList.get(position);
        holder.textOrderID.setText(data.getOrder_ID());
        holder.textDishName.setText(data.getDishName());
        holder.textCountDish.setText(data.getCountDish());
        holder.textStatus.setText(data.getStatus());
        if (data.getStatus().equals("Приготовление")){
            holder.buttonReady.setVisibility(View.GONE);
        }
        if (data.getStatus().equals("Готово")){
            holder.buttonReady.setVisibility(View.VISIBLE);
        }
        if (data.getStatus().equals("Подано")){
            holder.buttonReady.setVisibility(View.GONE);
        }
        holder.buttonReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connection = DatabaseHelper.getConnection();
                            String deleteQuery = "Update Orders_dish Set status = ? WHERE order_id = ? and dish_id = (Select dish_id from menu where name = ?)";
                            PreparedStatement statement = connection.prepareStatement(deleteQuery);
                            statement.setString(1,"Подано");
                            if (data.getOrder_ID().length() > 1){
                                statement.setInt(2, Integer.parseInt(data.getOrder_ID().substring(7)));
                            }else{
                                statement.setInt(2, Integer.parseInt(data.getOrder_ID()));
                            }
                            statement.setString(3, data.getDishName());
                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {

                            }
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            DatabaseHelper.closeConnection(connection);
                        }
                    }});
                thread.start();
                try {
                    semaphore.acquire();
                    notifyDataSetChanged();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (onItemClickListener != null) {
                    onItemClickListener.onButtonClick(position);
                }
            }
        });

        return convertView;
    }
    public void updateData(List<CookDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textOrderID;
        TextView textDishName;
        TextView textCountDish;
        TextView textStatus;
        Button buttonReady;

    }
}