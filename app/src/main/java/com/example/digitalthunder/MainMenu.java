package com.example.digitalthunder;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import android.widget.Button;

public class MainMenu extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle args = getIntent().getExtras();
        TextView UsFirstName = (TextView) findViewById(R.id.user_name);
        TextView UsSecondName = (TextView) findViewById(R.id.user_second_name);
        TextView FileStatus = (TextView) findViewById(R.id.databaseStatus);
        Button updates = findViewById(R.id.updates);

        UsFirstName.setText(args.getCharSequence("User name"));
        UsSecondName.setText(args.getCharSequence("User second name"));
        updates.setOnClickListener(this);
    }

    public void DownLoadFile(String addr, String path) throws IOException
    {
        URL url = null;
        URLConnection hurl = null;
        try {
             url = new URL(addr);
             hurl = url.openConnection();
             Log.d(String.valueOf(hurl.getContentLength()), "WORKING!!!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = openFileOutput(path, MODE_PRIVATE);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    @Override
    public void onClick(View view)
    {
        Thread secondThread = new Thread(new Runnable() {
            //@SuppressLint("SdCardPath")
            @Override
            public void run() {
                try {
                    DownLoadFile("https://admtyumen.ru/files/opendata/7202136720-GiftedChildren/7202136720.94.2.xml", "database.xml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        secondThread.start();
    }
}