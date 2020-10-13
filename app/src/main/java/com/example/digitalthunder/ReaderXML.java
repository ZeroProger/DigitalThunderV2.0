package com.example.digitalthunder;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ReaderXML
{
    VerifyDatabaseStat DatabaseStat;
    String Section;
    String Number;
    String FirstName;
    String SecondName;

    ReaderXML(String fName, String sName)
    {
        this.FirstName = fName;
        this.SecondName = sName;
    }
    void GetNamesData()
    {
        VerifyDatabaseStat out = VerifyDatabaseStat.HUMAN_IS_WRONG;
        try {
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("data/data/com.example.digitalthunder/files/database.xml"));
            NodeList firstNamesNodes = doc.getElementsByTagName("first_name");//Получение листа нодов по тегу <first_name>
            NodeList secondNamesNodes = doc.getElementsByTagName("second_name");
            NodeList sections = doc.getElementsByTagName("achive_way");
            NodeList numbers = doc.getElementsByTagName("number");

            for (int i = 0; i < firstNamesNodes.getLength() && i < secondNamesNodes.getLength(); i++)
            {
                if(firstNamesNodes.item(i).getTextContent().equals(FirstName) && secondNamesNodes.item(i).getTextContent().equals(SecondName))
                {
                    out = VerifyDatabaseStat.COMPLETED;
                    this.Section = sections.item(i).getTextContent();
                    this.Number = numbers.item(i).getTextContent();
                }
            }
        }
        catch (Exception e)
        {
            Log.d("Слышь ты, программюка", "Опять что-то пошло не так!");
            DatabaseStat = VerifyDatabaseStat.ERROR;
        }
        DatabaseStat = out;
    }
}
