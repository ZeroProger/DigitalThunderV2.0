package com.digitalthunder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.digitalthunder.ui.chats.ChatsFragment;
import com.digitalthunder.ui.events.EventsFragment;
import com.digitalthunder.ui.opportunities.OpportunitiesFragment;
import com.digitalthunder.ui.profile.ProfileFragment;
import com.digitalthunder.ui.profile.ProfileFragment.ProfileEventListener;
import com.digitalthunder.ui.settings.SettingsFragment;
import com.digitalthunder.user.LoginActivity;
import com.digitalthunder.user.RegistrationActivity;
import com.digitalthunder.user.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity implements ProfileEventListener, ChatsFragment.ChatsEventListener {

    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseDatabase fDatabase;
    private FirebaseStorage fStorage;
    private DatabaseReference fDatabaseReference;
    private StorageReference fStorageReference;
    private String userID;
    private String imageURL;
    private String fullName;

    Uri imageUri;
    ActionBar actionBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        actionBar = getSupportActionBar();

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        userID = fUser.getUid();
        fDatabase = FirebaseDatabase.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStorageReference = FirebaseStorage.getInstance().getReference().child("image_profile_picture");
        fDatabaseReference = fDatabase.getReference("user");


        BottomNavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);
        navigationView.setSelectedItemId(R.id.navigation_events);

        actionBar.setTitle(R.string.title_events);
        EventsFragment fragmentEvents = new EventsFragment();
        FragmentTransaction ftEvents = getSupportFragmentManager().beginTransaction();
        ftEvents.replace(R.id.nav_host_fragment, fragmentEvents, "");
        ftEvents.commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("ResourceType")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_profile:
                            actionBar.setTitle(R.string.title_profile);
                            ProfileFragment fragmentProfile = new ProfileFragment();
                            FragmentTransaction ftProfile = getSupportFragmentManager().beginTransaction();
                            ftProfile.replace(R.id.nav_host_fragment, fragmentProfile, "");
                            ftProfile.commit();
                            return true;
                        case R.id.navigation_opportunities:
                            actionBar.setTitle(R.string.title_opportunities);
                            OpportunitiesFragment fragmentOpportunities = new OpportunitiesFragment();
                            FragmentTransaction ftOpportunities = getSupportFragmentManager().beginTransaction();
                            ftOpportunities.replace(R.id.nav_host_fragment, fragmentOpportunities, "");
                            ftOpportunities.commit();
                            return true;
                        case R.id.navigation_events:
                            actionBar.setTitle(R.string.title_events);
                            EventsFragment fragmentEvents = new EventsFragment();
                            FragmentTransaction ftEvents = getSupportFragmentManager().beginTransaction();
                            ftEvents.replace(R.id.nav_host_fragment, fragmentEvents, "");
                            ftEvents.commit();
                            return true;
                        case R.id.navigation_chats:
                            actionBar.setTitle(R.string.title_chats);
                            ChatsFragment fragmentChats = new ChatsFragment();
                            FragmentTransaction ftChats = getSupportFragmentManager().beginTransaction();
                            ftChats.replace(R.id.nav_host_fragment, fragmentChats, "");
                            ftChats.commit();
                            return true;
                        case R.id.navigation_settings:
                            actionBar.setTitle(R.string.title_settings);
                            SettingsFragment fragmentSettings = new SettingsFragment();
                            FragmentTransaction ftSettings = getSupportFragmentManager().beginTransaction();
                            ftSettings.replace(R.id.nav_host_fragment, fragmentSettings, "");
                            ftSettings.commit();
                            return true;
                    }
                    return false;
                }
            };

    private void startCrop() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                imageUri = result.getUri();
                ChangeProfilePictureInDataBase();
            }
        }
    }

    private void ChangeProfilePictureInDataBase() {
        final StorageReference imageName = fStorageReference.child("image_" + userID);
        imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("USER_PROFILE_PICTURE", "Success");
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imageURL = String.valueOf(uri);
                        fDatabaseReference.child(userID).child("imageURL").setValue(imageURL);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("USER_PROFILE_PICTURE", "Failure");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            fAuth.signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void someEvent(String s) {
        if (s.equals("CHANGE_AVATAR_BUTTON_PRESSED")) {
            startCrop();
        }
        if (s.equals("MESSAGE_SEND")) {
            Toast.makeText(MenuActivity.this, "Сообщение отправлено", Toast.LENGTH_SHORT).show();
        }
    }

    public static class ImageTransfer {
        public static Bitmap photoBitmap = null;
    }
}