package com.digitalthunder;

import android.util.Log;

import com.digitalthunder.ui.events.Olimpiade;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class Utils {
    public static ArrayList<Olimpiade> getOlympiadeData(String type) throws IOException {
        final String addr = "https://olimpiada.ru/search?q=";
        ArrayList<Olimpiade> olimpiades = new ArrayList<Olimpiade>();
        Document html;
        try
        {
            html = Jsoup.connect(addr + type).get();

            Elements table = html.getElementsByAttributeValueContaining("style", "vertical-align:top;");
            Elements classes = html.getElementsByClass("classes_dop");
            for (int tableNum = 0; tableNum < table.size(); tableNum++)
            {
                Olimpiade olimpiade = new Olimpiade("", "", "", "Описание отсутствует", "");

                olimpiade.classes = classes.get(tableNum).text();
                String description = table.get(tableNum).getElementsByClass("none_a black olimp_desc").first().text();
                String link = table.get(tableNum).getElementsByClass("none_a black olimp_desc").first().attributes().get("href");
                if(link != null)
                    olimpiade.link = link;

                if(description != null && description != "")
                    olimpiade.description = description;

                Elements titles = table.get(tableNum).getElementsByClass("headline");
                for (int titleNum = 0; titleNum < titles.size(); titleNum++)
                {
                    if(titles.get(titleNum).className().equals("headline")) {
                        olimpiade.title = titles.get(titleNum).text();
                    }
                    else
                        olimpiade.subTitle = titles.get(titleNum).text();
                }
                olimpiades.add(new Olimpiade(olimpiade.classes, olimpiade.title, olimpiade.subTitle, olimpiade.description, olimpiade.link));
            }

        }
        catch (Exception e)
        {
            Log.d("Olympiades", "ERROR");
        }
        return olimpiades;
    }
}
