package com.example.restoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OrderStatusAdapter extends BaseAdapter {

    private Context context;
    private List<OrdersStatusDataModel> dataList;

    public OrderStatusAdapter(Context context, List<OrdersStatusDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
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
    public interface OnItemClickListener {
        void onItemClick(OrdersStatusDataModel data);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.order_item, parent, false);

            holder = new ViewHolder();
            holder.textTable_item = convertView.findViewById(R.id.textTable_item);
            holder.textOrder_item = convertView.findViewById(R.id.textOrder_item);
            holder.textStatus_item = convertView.findViewById(R.id.textStatus_item);
            holder.textSum_item = convertView.findViewById(R.id.textSum_item);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        OrdersStatusDataModel data = dataList.get(position);
        holder.textTable_item.setText(data.getTable());
        holder.textOrder_item.setText(data.getOrder());
        holder.textStatus_item.setText(data.getStatus());
        holder.textSum_item.setText(data.getSum());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(data);
                }
            }
        });
        return convertView;
    }

    public void updateData(List<OrdersStatusDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textTable_item;
        TextView textOrder_item;
        TextView textStatus_item;
        TextView textSum_item;
    }
}
