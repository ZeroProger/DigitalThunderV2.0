package com.digitalthunder;

import com.digitalthunder.ui.events.EventsFragment;
import com.digitalthunder.ui.opportunities.OpportunitiesFragment;

import java.io.IOException;

public class OlimpiadsThread implements Runnable {
    String subject;
    public OlimpiadsThread(String subject)
    {
        this.subject = subject;
    }
    @Override
    public void run() {
        try {
            EventsFragment.olimpiads = Utils.getOlympiadeData(this.subject);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
