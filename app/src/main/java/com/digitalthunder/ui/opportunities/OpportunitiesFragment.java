package com.digitalthunder.ui.opportunities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.digitalthunder.R;

public class OpportunitiesFragment extends Fragment {

    private OpportunitiesViewModel opportunitiesViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        opportunitiesViewModel = ViewModelProviders.of(this).get(OpportunitiesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_opportunities, container, false);
        final TextView textView = root.findViewById(R.id.text_opportunities);
        opportunitiesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}