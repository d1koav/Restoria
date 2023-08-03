package com.example.restoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TableAdapter extends BaseAdapter {

    private Context context;
    private List<TablesDataModel> dataList;

    public TableAdapter(Context context, List<TablesDataModel> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    public interface OnItemClickListener {
        void onItemClick(TablesDataModel data);
    }
    private TableAdapter.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(TableAdapter.OnItemClickListener listener) {
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
            convertView = inflater.inflate(R.layout.table_custom_item, parent, false);

            holder = new ViewHolder();
            holder.textTable = convertView.findViewById(R.id.textTable);
            holder.textOrder = convertView.findViewById(R.id.textOrder);
            holder.textStatus = convertView.findViewById(R.id.textStatus);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TablesDataModel data = dataList.get(position);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(data);
                }
            }
        });
        holder.textTable.setText(data.getTable());
        holder.textOrder.setText(data.getOrder());
        holder.textStatus.setText(data.getStatus());

        return convertView;
    }

    public void updateData(List<TablesDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView textTable;
        TextView textOrder;
        TextView textStatus;
    }
}
