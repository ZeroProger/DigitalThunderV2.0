package com.digitalthunder.ui.events;

public class Olimpiade {
    public String classes;
    public String title;
    public String subTitle;
    public String description;
    public String link;

    public Olimpiade(String classes, String title, String subTitle, String description, String link)
    {
        this.classes = classes;
        this.title = title;
        this.subTitle = subTitle;
        this.description = description;
        this.link = link;
    }

    public String getClasses()
    {
        return this.classes;
    }

    public String getTitle()
    {
        return this.title;
    }

    public String getSubTitle()
    {
        return this.subTitle;
    }

    public String getDescription()
    {
        return this.description;
    }
}
