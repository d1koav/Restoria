package com.example.restoria.ui.home;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.restoria.DatabaseHelper;
import com.example.restoria.R;
import com.example.restoria.TablesDataModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class HomeViewModel extends ViewModel {
    private MutableLiveData<List<TablesDataModel>> dataList;

    public HomeViewModel() {
        dataList = new MutableLiveData<>();
    }
    public LiveData<List<TablesDataModel>> getDataList() {
        return dataList;
    }

    public void updateHomeList(List<TablesDataModel> homelist) {dataList.setValue(homelist);}


}
