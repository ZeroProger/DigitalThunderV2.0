package com.example.digitalthunder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;


import android.widget.Button;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainMenu extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        try{
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("data/data/com.example.digitalthunder/files/database.xml"));
            Element rootel = doc.getDocumentElement();
            Log.d(doc.getXmlVersion(), "Tag name");
            NodeList List_1 = doc.getElementsByTagName("GiftedChildren");
            Log.d(List_1.toString(), "Nodes");
            FileStatus.setText(doc.getDocumentElement().toString());
        }
        catch(Exception e){
            e.printStackTrace();
            FileStatus.setText("Обновите базы для загрузки файла");
        }
    }
    public void downLoadFile(String addr, String path) throws IOException {
        URL url = null;
        BigInteger sizeOfFile = new BigInteger("1");
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
    public void onClick(View view) {
        Thread second_thread = new Thread(new Runnable() {
            //@SuppressLint("SdCardPath")
            @Override
            public void run() {
                try {
                    downLoadFile("https://admtyumen.ru/files/opendata/7202136720-GiftedChildren/7202136720.94.2.xml", "database.xml");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        second_thread.start();
    }
}