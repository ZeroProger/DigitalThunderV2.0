package com.digitalthunder.ui.events;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalthunder.OlimpiadesAdapter;
import com.digitalthunder.OlimpiadsThread;
import com.digitalthunder.R;

import java.util.ArrayList;

public class EventsFragment extends Fragment {

    public static ArrayList<Olimpiade> olimpiads = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_events, container, false);


        Button searchButton = (Button) root.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView searchSpace = (TextView) root.findViewById(R.id.searchSpace);
                if(searchSpace != null) {

                    Thread downloading = new Thread(new OlimpiadsThread(searchSpace.getText().toString()));
                    downloading.start();

                    try {
                        while (olimpiads == null) ;
                        if (!olimpiads.toString().equals(""))
                            Log.d("Main threat!!!", olimpiads.toString());
                        else
                            Log.d("Main threat!!!", "null");
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Main threat!!!", "ERROR");
                    }
                    Log.d("Fragment", "All created");
                    RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.rv);
                    recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));

                    OlimpiadesAdapter olimpiadesAdapter = new OlimpiadesAdapter(olimpiads);
                    recyclerView.setAdapter(olimpiadesAdapter);
                }
            }
        });
        return root;
    }

}