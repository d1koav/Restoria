package com.example.restoria;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.restoria.databinding.ActivityTablesBinding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Tables extends AppCompatActivity {
    private Semaphore semaphore = new Semaphore(0);
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityTablesBinding binding;
    Context context;
    String value, userId, name, surname, role;
    Connection connection;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTablesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.appBarTables.toolbar.setTitle("Столы");
        setSupportActionBar(binding.appBarTables.toolbar);
        Intent intent = getIntent();
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        context = this;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tables);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            connection = DatabaseHelper.getConnection();
                            String enterKey = extras.getString("enter_key");
                            String selectQuery = "SELECT user_id, name, surname, role FROM Staff WHERE enter_key = ?";
                            statement = connection.prepareStatement(selectQuery);
                            statement.setString(1, enterKey);
                            resultSet = statement.executeQuery();
                            if (resultSet.next()) {
                                userId = resultSet.getString("user_id");
                                name = resultSet.getString("name");
                                surname = resultSet.getString("surname");
                                role = resultSet.getString("role");
                                TextView navInfo = (TextView) headerView.findViewById(R.id.info);
                                navInfo.setText(role);
                                TextView navUsername = (TextView) headerView.findViewById(R.id.userName);
                                if (surname != null){
                                    navUsername.setText(name + " " + surname);
                                }
                                else{
                                    navUsername.setText(name);
                                }
                            } else {
                            }} catch (SQLException e) {
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
                    }
                });
                thread.start();
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tables, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_tables);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}