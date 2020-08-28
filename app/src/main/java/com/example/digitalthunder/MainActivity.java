package com.example.digitalthunder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.widget.TextView;

import java.sql.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button Autoris_but;
    TextView textView_name;
    TextView textView_secondName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Autoris_but = (Button) findViewById(R.id.autoris_but);//Находим кнопку
        Autoris_but.setOnClickListener(this);

        textView_name = (TextView) findViewById(R.id.editTextTextPersonName);
        textView_secondName = (TextView) findViewById(R.id.editTextTextPersonName2);
        //CharSequence x = textView_name.getText(); //100% работающий вариант
        //textView_secondName.setText(x);
    }
    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, MainMenu.class);
        i.putExtra("User name", textView_name.getText());
        i.putExtra("User second name", textView_secondName.getText());

        startActivity(i);
    }

}