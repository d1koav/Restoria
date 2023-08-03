package com.example.restoria;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

public class payment extends AppCompatActivity {
    private Semaphore semaphore = new Semaphore(0);
    private Semaphore semaphore1 = new Semaphore(0);
    private Semaphore semaphore2 = new Semaphore(0);
    Context context;

    ResultSet resultSet, resultSet2;
    PreparedStatement statement;
    Connection connection;
    TextView order_ID, sum_, sale, itog;
    EditText card;
    Integer flag =0;
    Button check, reg, procced;
    String stringValue;
    Integer db_sum = 0, sale_amount= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        stringValue = intent.getStringExtra("order_id");
        setContentView(R.layout.activity_payment);
        order_ID = findViewById(R.id.textView7_orderID);
        order_ID.setText(stringValue.substring(7));
        sum_ = findViewById(R.id.textView10_Sum);
        card = findViewById(R.id.ed2);
        check = findViewById(R.id.button_Check_card);
        reg = findViewById(R.id.button_registration);
        sale = findViewById(R.id.textView_sale_amount);
        itog = findViewById(R.id.textView16_ITOGO);
        procced = findViewById(R.id.button_oplata);
        Thread thread = new Thread(() -> {
            try {
                connection = DatabaseHelper.getConnection();
                String selectQuery = "SELECT sum FROM Orders Where order_id = ?";
                PreparedStatement statement1 = connection.prepareStatement(selectQuery);
                statement1.setInt(1, Integer.parseInt(stringValue.substring(7)));
                resultSet = statement1.executeQuery();
                if (resultSet.next()) {
                    db_sum = resultSet.getInt("sum");
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
                DatabaseHelper.closeConnection(connection);
                semaphore.release();
            }
        });
        thread.start();
        try {
            semaphore.acquire();
            itog.setText(String.valueOf(db_sum*(100-sale_amount)/100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        check.setOnClickListener(v -> {
            String card_ = String.valueOf(card.getText());
            if(card_.isEmpty()){
                AlertDialog.Builder builder2 = new AlertDialog.Builder(payment.this);
                builder2.setTitle("Ошибка")
                        .setMessage("Номер карты не может быть пустым")
                        .setPositiveButton("Введите номер карты", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .show();
            }else{
                Thread thread1 = new Thread(() -> {
                    try {
                        connection = DatabaseHelper.getConnection();
                        String selectQuery = "SELECT sale_amount FROM Client Where card = ?";
                        PreparedStatement statement1 = connection.prepareStatement(selectQuery);
                        statement1.setInt(1, Integer.parseInt(card_));
                        resultSet = statement1.executeQuery();
                        if (resultSet.next()) {
                            sale_amount = resultSet.getInt("sale_amount");
                            flag = 0;
                        }else{
                            sale_amount = 0;
                            flag = 1;
                        }
                        statement1.close();
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
                        DatabaseHelper.closeConnection(connection);
                        semaphore1.release();
                    }
                });
                thread1.start();
                try {
                    semaphore1.acquire();
                    sale.setText(String.valueOf(sale_amount));
                    itog.setText(String.valueOf(db_sum*(100-sale_amount)/100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (flag == 1){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(payment.this);
                    builder1.setTitle("Ошибка")
                            .setMessage("Посетитель не найден.")
                            .setPositiveButton("Зарегистрируйте посетителя", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                }
            }
        });
        sum_.setText(String.valueOf(db_sum));
        sale.setText(String.valueOf(sale_amount));
        reg.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), client_profile.class));
        });
        procced.setOnClickListener(v -> {
            Thread thread2 = new Thread(() -> {
                try {
                    connection = DatabaseHelper.getConnection();
                    String selectQuery = "UPDATE Orders SET isPayed = True where order_id = ?";
                    PreparedStatement statement2 = connection.prepareStatement(selectQuery);
                    statement2.setInt(1, Integer.parseInt(stringValue.substring(7)));
                    int rs = statement2.executeUpdate();
                    statement2.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    DatabaseHelper.closeConnection(connection);
                    semaphore2.release();
                }
            });
            thread2.start();
            try {
                semaphore2.acquire();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(payment.this);  // Используйте payment.this вместо context
                builder1.setTitle("Успешно")
                        .setMessage("Заказ успешно оплачен")
                        .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }
    @Override
    public void onBackPressed() {
        finish();
    }
}