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

public class createOrderAdapter extends BaseAdapter {
    Connection connection;
    PreparedStatement statement;
    public interface OnItemClickListener {
        void onButtonClick(int position);
    }
    private Context context;
    private List<createOrderDataModel> dataList;
    public void setDataList(List<createOrderDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }
    public createOrderAdapter(Context context, List<createOrderDataModel> dataList) {
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
            convertView = inflater.inflate(R.layout.create_order_list_item, parent, false);
            holder = new ViewHolder();
            holder.Count = convertView.findViewById(R.id.textView_create_Count);
            holder.Name = convertView.findViewById(R.id.textView_create_Name);
            holder.Price = convertView.findViewById(R.id.textView_create_Price);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        createOrderDataModel data = dataList.get(position);
        holder.Count.setText(data.getDish_count());
        holder.Name.setText(data.getDish_name());
        holder.Price.setText(data.getPrice());

        return convertView;
    }
    public void updateData(List<createOrderDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView Count;
        TextView Name;
        TextView Price;

    }
}