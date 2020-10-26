package com.digitalthunder.ui.opportunities;

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
import com.digitalthunder.ui.events.Olimpiade;

import java.util.ArrayList;


public class OpportunitiesFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_opportunities, container, false);

        return root;
    }

}