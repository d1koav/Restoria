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
import java.util.Random;
import java.util.concurrent.Semaphore;

public class AdminAdapter extends BaseAdapter {
    Connection connection;
    PreparedStatement statement;
    public interface OnItemClickListener {
        void onButtonClick(int position);
    }
    private Context context;
    private List<AdminDataModel> dataList;
    public void setDataList(List<AdminDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }
    public AdminAdapter(Context context, List<AdminDataModel> dataList) {
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
            convertView = inflater.inflate(R.layout.admin_list_item, parent, false);

            holder = new ViewHolder();
            holder.textNameSurname = convertView.findViewById(R.id.textName_Surname);
            holder.textUserID = convertView.findViewById(R.id.textDish_Name);
            holder.textEnterKey = convertView.findViewById(R.id.text_Count_Dish);
            holder.buttonDelete = convertView.findViewById(R.id.button_Ready);
            holder.buttonEnterKey = convertView.findViewById(R.id.button_NewKey);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AdminDataModel data = dataList.get(position);
        holder.textNameSurname.setText(data.getNameSurname());
        holder.textUserID.setText(data.getUserID());
        holder.textEnterKey.setText(data.getEnter_key());

        holder.buttonEnterKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                String key = generate_key();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connection = DatabaseHelper.getConnection();
                            String updateQuery = "UPDATE Staff SET enter_key = ? Where user_id = ?";
                            PreparedStatement statement = connection.prepareStatement(updateQuery);
                            statement.setString(1, key);
                            statement.setInt(2, Integer.parseInt(data.getUserID()));
                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {

                            }
                            statement.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            DatabaseHelper.closeConnection(connection);
                            semaphore.release();
                        }}});
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
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connection = DatabaseHelper.getConnection();
                            String deleteQuery = "DELETE FROM Staff WHERE user_id = ?";
                            PreparedStatement statement = connection.prepareStatement(deleteQuery);
                            statement.setInt(1, Integer.parseInt(data.getUserID()));
                            int rowsAffected = statement.executeUpdate();
                            if (rowsAffected > 0) {
                                dataList.remove(position);
                            } else {
                                // Удаление не произведено
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
    public String generate_key(){
        String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        int CODE_LENGTH = 16;

        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }
    public void updateData(List<AdminDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textNameSurname;
        TextView textUserID;
        TextView textEnterKey;
        Button buttonDelete;
        Button buttonEnterKey;

    }
}