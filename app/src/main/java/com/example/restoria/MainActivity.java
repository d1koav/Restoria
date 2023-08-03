package com.example.restoria;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {
    EditText key;
    Context context;
    Button sign_in;
    Connection connection;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String result = null;
    String role = null;
    private Semaphore semaphore = new Semaphore(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        key = findViewById(R.id.enter_key);
        sign_in = findViewById(R.id.sign_in);
        context = this;
        sign_in.setOnClickListener(v -> {
            Thread thread = new Thread(() -> {
                try {
                    connection = DatabaseHelper.getConnection();
                    String enterKey = String.valueOf(key.getText());
                    String selectQuery = "SELECT enter_key, role FROM Staff WHERE enter_key = ?";
                    statement = connection.prepareStatement(selectQuery);
                    statement.setString(1, enterKey);
                    resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        result = resultSet.getString("enter_key");
                        role = resultSet.getString("role");
                    } else {
                        result = "123456";
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
                    semaphore.release();
                }
            });
            thread.start();
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (result.equals("123456")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Ошибка")
                            .setMessage("Неверный ключ")
                            .setPositiveButton("Попробовать снова", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                }
            else {
                Bundle bundle1 = new Bundle();
                bundle1.putString("enter_key", result);
                if (role.equals("Официант")) {
                    Intent i = new Intent(getApplicationContext(), Tables.class);
                    i.putExtras(bundle1);
                    startActivity(i);
                    finish();
                }
                if (role.equals("Администратор")) {
                    Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                    i.putExtras(bundle1);
                    startActivity(i);
                    finish();
                }
                if (role.equals("Повар")) {
                    Intent i = new Intent(getApplicationContext(), cook.class);
                    i.putExtras(bundle1);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}