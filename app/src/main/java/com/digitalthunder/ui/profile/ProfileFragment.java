package com.digitalthunder.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.digitalthunder.MenuActivity;
import com.digitalthunder.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.digitalthunder.MenuActivity.*;

public class ProfileFragment extends Fragment {

    public interface ProfileEventListener {
        void someEvent(String s);
    }

    ProfileEventListener profileEventListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            profileEventListener = (ProfileEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onSomeEventListener");
        }
    }

    public ProfileFragment() {

    }

    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseDatabase fDatabase;
    private FirebaseStorage fStorage;
    private DatabaseReference fDatabaseReference;
    private StorageReference fStorageReference;
    private String userID;

    CircleImageView userProfilePicture;
    TextView userFullName;
    ProgressBar profileLoadProgressBar;
    FloatingActionButton floatingActionButton;

    Uri imageUri;

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        final String[] fullName = new String[1];
        final String[] imageURL = new String[1];

        profileLoadProgressBar = root.findViewById(R.id.profileLoadProgressBar);
        userFullName = root.findViewById(R.id.tUserFullName);
        userProfilePicture = root.findViewById(R.id.userProfilePicture);
        floatingActionButton = root.findViewById(R.id.changeProfilePicture);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        userID = fUser.getUid();
        fDatabase = FirebaseDatabase.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStorageReference = FirebaseStorage.getInstance().getReference().child("image_profile_picture");
        fDatabaseReference = fDatabase.getReference("user");

        Query query = fDatabaseReference.orderByChild("email").equalTo(fUser.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    fullName[0] = "" + dataSnapshot.child("firstName").getValue()
                            + " " + dataSnapshot.child("secondName").getValue();

                    userFullName.setText(fullName[0]);
                    imageURL[0] = dataSnapshot.child("imageURL").getValue().toString();
                    Picasso.get().load(imageURL[0]).into(userProfilePicture);
                    profileLoadProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileEventListener.someEvent("CHANGE_AVATAR_BUTTON_PRESSED");
            }
        });

        return root;
    }

}