package com.example.restoria;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TileAdapter extends BaseAdapter {
    public interface OnItemClickListener {
        void onItemClick(String category);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    private Context context;
    private List<String> tilesList;

    public TileAdapter(Context context, List<String> tilesList) {
        this.context = context;
        this.tilesList = tilesList;
    }

    @Override
    public int getCount() {
        return tilesList.size();
    }

    @Override
    public Object getItem(int position) {
        return tilesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tile_menu_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.tileText);
        String tileName = tilesList.get(position);
        textView.setText(tileName);
        int tileSize = calculateTileSize();
        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        params.width = tileSize;
        params.height = tileSize;
        convertView.setLayoutParams(params);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(tileName);
                }
            }
        });
        return convertView;
    }

    private int calculateTileSize() {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        int spacing = context.getResources().getDimensionPixelSize(R.dimen.tile_spacing);
        int columnCount = 2;
        int tileSize = (screenWidth - spacing * (columnCount + 1)) / columnCount;
        return tileSize;
    }
}



