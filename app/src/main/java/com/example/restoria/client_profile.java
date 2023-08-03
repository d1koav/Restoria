package com.example.restoria;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class client_profile extends AppCompatActivity {
    EditText Cname, Csurname, Cemail, Cphone;
    Button reg;
    Connection connection;
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String result = null;
    private Semaphore semaphore = new Semaphore(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_profile);
        Cname = findViewById(R.id.editTextClientName);
        Csurname = findViewById(R.id.editTextClientSurname);
        Cemail = findViewById(R.id.editTextClientEmail);
        Cphone = findViewById(R.id.editTextClientPhone);
        reg = findViewById(R.id.client_registration);
        reg.setOnClickListener(v-> {
            Thread thread = new Thread(()->{
                try {
                    connection = DatabaseHelper.getConnection();
                    String insert = "Insert INTO Client (name, surname, tel_number, email, card, order_amount, sale_amount) VALUES (?, ?, ?, ?, ?, 0, 0);";
                    PreparedStatement statement1 = connection.prepareStatement(insert);
                    statement1.setString(1, String.valueOf(Cname.getText()));
                    statement1.setString(2, String.valueOf(Csurname.getText()));
                    statement1.setString(3, String.valueOf(Cphone.getText()));
                    statement1.setString(4, String.valueOf(Cemail.getText()));
                    statement1.setLong(5,Long.parseLong(String.valueOf(Cphone.getText())));
                    int s = statement1.executeUpdate();
                }catch (SQLException e) {
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
        });
        finish();
    }
}