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

    private TextView UsFirstName = null;
    private TextView UsSecondName = null;
    private TextView FileStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle args = getIntent().getExtras();
        UsFirstName = (TextView) findViewById(R.id.user_name);
        UsSecondName = (TextView) findViewById(R.id.user_second_name);
        FileStatus = (TextView) findViewById(R.id.databaseStatus);
        Button updates = findViewById(R.id.updates);

        UsFirstName.setText(args.getCharSequence("User name"));
        UsSecondName.setText(args.getCharSequence("User second name"));
        updates.setOnClickListener(this);
        //Запуск потока на чтение базы данных
        /*
        Thread readDataBaseThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //ParsingXml();
                if(VerifiDataFromXml(UsFirstName, UsSecondName).equals("Completed"))
                {
                    //FileStatus.setText("Пользователь подтверждён");
                    Log.d("Статус базы", "Пользователь подтверждён");
                }
                else
                {
                    //FileStatus.setText("Пользователь не подтверждён");
                    Log.d("Статус базы", "Пользователь не подтверждён");
                }
            }
        });
        readDataBaseThread.start();
        */
    }
    //метод для парсинга xml
    public void ParsingXml()
    {
        try
        {
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("data/data/com.example.digitalthunder/files/database.xml"));
            Node rootel = doc.getDocumentElement();
            //Log.d(doc.getXmlVersion(), "Tag name");
            //NodeList List_1 = doc.getElementsByTagName("GiftedChildren");
            NodeList listOne = rootel.getChildNodes();
            for(int i = 0; i < listOne.getLength(); i++)
            {
                Node record = listOne.item(i);
                if(record.getNodeType() != Node.TEXT_NODE)
                {
                    NodeList listTwo = record.getChildNodes();
                    for(int j = 0; j < listTwo.getLength(); j++)
                    {
                        Node recordsTwo = listTwo.item(j);
                        if(recordsTwo.getNodeType() != Node.TEXT_NODE)
                        {
                            Log.d("Вывод:", recordsTwo.getNodeName() + ":" + recordsTwo.getTextContent());
                        }
                    }
                }
            }
           // Log.d(List_1.toString(), "Nodes");
           // FileStatus.setText(doc.getDocumentElement().toString());
        }
        catch(Exception e){
            e.printStackTrace();
           // FileStatus.setText("Обновите базы для загрузки файла");
            Log.d("Что-то пошло не так!!!", "Ошибка при парсинге");
        }
    }
    public String VerifiDataFromXml(TextView fName, TextView sName)
    {
        String out = "Human is wrong";
        try {
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("data/data/com.example.digitalthunder/files/database.xml"));
            //Node rootel = doc.getDocumentElement();
            NodeList firstNamesNodes = doc.getElementsByTagName("first_name");//Получение листа нодов по тегу <first_name>
            NodeList secondNamesNodes = doc.getElementsByTagName("second_name");

            for (int i = 0; i < firstNamesNodes.getLength() && i < secondNamesNodes.getLength(); i++)
            {
                if(firstNamesNodes.item(i).getTextContent().equals(fName.getText().toString()) && secondNamesNodes.item(i).getTextContent().equals(sName.getText().toString()))
                {
                    out = "Completed";
                }
                //Log.d("Names: ", firstNames.item(i).getTextContent());
            }
        }
        catch (Exception e)
        {
            Log.d("Слышь ты, программюка", "Опять что-то пошло не так!");
            return "Error";
        }
        return out;
    }
    public void DownLoadFile(String addr, String path) throws IOException
    {
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