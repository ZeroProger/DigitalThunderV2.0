package com.example.digitalthunder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle args = getIntent().getExtras();
        TextView UsFirstName = (TextView) findViewById(R.id.user_name);
        TextView UsSecondName = (TextView) findViewById(R.id.user_second_name);

        UsFirstName.setText(args.getCharSequence("User name"));
        UsSecondName.setText(args.getCharSequence("User second name"));
    }
}