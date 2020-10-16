package com.digitalthunder.ui.chats;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatsFragment extends Fragment {

    private ChatsViewModel chatsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        chatsViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_chats, container, false);

        return root;
    }
}