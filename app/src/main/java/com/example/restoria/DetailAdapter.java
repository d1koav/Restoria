package com.example.restoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DetailAdapter extends BaseAdapter {

    private Context context;
    private List<DetailDataModel> dataList;

    public DetailAdapter(Context context, List<DetailDataModel> dataList) {
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
        void onItemClick(DetailDataModel data);
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
            convertView = inflater.inflate(R.layout.detail_list_item, parent, false);

            holder = new ViewHolder();
            holder.Name = convertView.findViewById(R.id.textView_Dish_N);
            holder.Price = convertView.findViewById(R.id.textView_Dish_P);
            holder.Weight = convertView.findViewById(R.id.textView_Dish_W);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Получение данных для текущей позиции
        DetailDataModel data = dataList.get(position);
        holder.Name.setText(data.getName());
        holder.Price.setText(data.getPrice());
        holder.Weight.setText(data.getWeight());
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

    public void updateData(List<DetailDataModel> newDataList) {
        dataList = newDataList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView Name;
        TextView Price;
        TextView Weight;
    }
}
