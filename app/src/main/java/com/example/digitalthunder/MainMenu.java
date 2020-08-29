package com.example.digitalthunder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class MainMenu extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Bundle args = getIntent().getExtras();
        TextView UsFirstName = (TextView) findViewById(R.id.user_name);
        TextView UsSecondName = (TextView) findViewById(R.id.user_second_name);
        TextView FileStatus = (TextView) findViewById(R.id.databaseStatus);

        UsFirstName.setText(args.getCharSequence("User name"));
        UsSecondName.setText(args.getCharSequence("User second name"));

        try{
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("/sdcard/DigitalThunderData/database.xml"));
           // System.out.println(doc.getDocumentElement());
            Element rootel = doc.getDocumentElement();
            FileStatus.setText(doc.getDocumentElement().toString());
        }
        catch(Exception e){
            e.printStackTrace();
            FileStatus.setText("Not working");
        }
    }

}