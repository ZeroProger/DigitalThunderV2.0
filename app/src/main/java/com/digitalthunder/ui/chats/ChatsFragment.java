package com.digitalthunder.ui.chats;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import com.digitalthunder.R;
import com.digitalthunder.ui.profile.ProfileFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ChatsFragment extends Fragment {

    public interface ChatsEventListener {
        void someEvent(String s);
    }

    ChatsEventListener chatsEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            chatsEventListener = (ChatsEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    EditText messageField;
    ImageButton messageSendButton;

    public ChatsFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_chats, container, false);
        setRetainInstance(true);

        messageField = root.findViewById(R.id.messageField);
        messageSendButton = root.findViewById(R.id.messageSendButton);

        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageField.setText("");
                chatsEventListener.someEvent("MESSAGE_SEND");
            }
        });

        return root;
    }
}