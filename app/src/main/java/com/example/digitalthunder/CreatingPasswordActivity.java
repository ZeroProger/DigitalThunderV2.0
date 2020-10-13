package com.example.digitalthunder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class CreatingPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creating_password);
        setTitle("Регистрация");
        Thread downloadTask = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownLoadFile("https://admtyumen.ru/files/opendata/7202136720-GiftedChildren/7202136720.94.2.xml", "database.xml");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.d("Downloading database", "ERROR");
                }
            }
        });
        downloadTask.start();

        Button changeFotoButton = (Button) findViewById(R.id.registrationChangeFotoBut);
        changeFotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Changer foto", "Changer started");
            }
        });
        Button registrationButton = (Button) findViewById(R.id.registrationRegBut);
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView userFirstName = (TextView) findViewById(R.id.registranionFirstName);
                TextView userSecondName = (TextView) findViewById(R.id.registrationSecondName);
                ReaderXML readerXML = new ReaderXML(userFirstName.getText().toString(), userSecondName.getText().toString());
                readerXML.GetNamesData();

                if(readerXML.DatabaseStat == VerifyDatabaseStat.COMPLETED)
                {
                    TextView userLogin = (TextView) findViewById(R.id.registrationLogin);
                    TextView userPasswd = (TextView) findViewById(R.id.registrationPasswd);
                    TextView userRepeatPasswd = (TextView) findViewById(R.id.registrationRepeatPasswd);
                    if(userRepeatPasswd.getText().toString().equals(userPasswd.getText().toString()))
                    {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("users");
                        reference.child(userLogin.getText().toString()).child("passwd").setValue(userPasswd.getText().toString());
                        reference.child(userLogin.getText().toString()).child("name").setValue(userFirstName.getText().toString() + " " + userSecondName.getText().toString());
                        reference.child(userLogin.getText().toString()).child("section").setValue(readerXML.Section);
                        reference.child(userLogin.getText().toString()).child("number").setValue(readerXML.Number);
                        reference.child(userLogin.getText().toString()).child("login").setValue(userLogin.getText().toString());

                        Intent mainMenu = new Intent(CreatingPasswordActivity.this, MainMenu.class);
                        mainMenu.putExtra("User name", userFirstName.getText().toString());
                        mainMenu.putExtra("User second name", userSecondName.getText().toString());
                        startActivity(mainMenu);
                    }
                    else
                    {
                        Log.d("Registration", "Ошибка проверки пароля");
                    }
                }
                else if(readerXML.DatabaseStat == VerifyDatabaseStat.HUMAN_IS_WRONG)
                {
                    Log.d("Registration", "Error of human name");
                }

            }
        });
    }
    public void DownLoadFile(String addr, String path) throws IOException
    {
        URL url = null;
        URLConnection hurl = null;
        try {
            url = new URL(addr);
            hurl = url.openConnection();
            Log.d(String.valueOf(hurl.getContentLength()), "Downloading file");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = openFileOutput(path, MODE_PRIVATE);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

    }
}