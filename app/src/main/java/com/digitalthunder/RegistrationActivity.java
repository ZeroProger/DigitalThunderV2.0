package com.digitalthunder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG_USER_REGISTER = "User Register in FireBase";

    FloatingActionButton setProfilePicture;
    CircleImageView userProfilePicture;
    Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference usersDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);



        Button createAccountButton = (Button) findViewById(R.id.createAccountButton);
        setProfilePicture = findViewById(R.id.setProfilePicture);
        userProfilePicture = findViewById(R.id.userProfilePicture);

        mAuth = FirebaseAuth.getInstance();
        usersDataBase = FirebaseDatabase.getInstance().getReference("user");

        Thread downloadTask = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DownLoadFile(String.valueOf(R.string.urlToXMLDataBase), "database.xml");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Downloading database", "ERROR");
                }
            }
        });
        downloadTask.start();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser currentUser = mAuth.getCurrentUser();

                final EditText userRegistrationEMail = findViewById(R.id.registrationEmail);
                EditText userRegistrationFirstName = findViewById(R.id.registrationFirstName);
                EditText userRegistrationSecondName = findViewById(R.id.registrationSecondName);
                EditText userRegistrationPassword = findViewById(R.id.registrationPassword);
                EditText userRegistrationRepeatPassword = findViewById(R.id.registrationRepeatPassword);

                String id = usersDataBase.push().getKey();
                String eMail = userRegistrationEMail.getText().toString();
                String firstName = userRegistrationFirstName.getText().toString();
                String secondName = userRegistrationSecondName.getText().toString();
                String password = userRegistrationPassword.getText().toString();
                String repeatPassword = userRegistrationRepeatPassword.getText().toString();

                String[] splitEMail = new String[0];
                if (eMail.contains("@")) {
                    if (eMail.split("@").length > 1) {
                        splitEMail = eMail.split("@");
                    }
                    else {
                        userRegistrationEMail.setError("Пожалуйста, укажите почту в формате: login@example.com");
                    }
                }
                if (eMail.isEmpty()) {
                    userRegistrationEMail.setError("Пожалуйста, укажите вашу почту");
                }
                if (!eMail.contains("@") && !eMail.isEmpty()) {
                    userRegistrationEMail.setError("Пожалуйста, укажите почту в формате: login@example.com");
                }
                if (firstName.isEmpty()) {
                    userRegistrationFirstName.setError("Пожалуйста, укажите ваше имя");
                }
                if (secondName.isEmpty()) {
                    userRegistrationSecondName.setError("Пожалуйста, укажите вашу фамилию");
                }
                if (password.isEmpty() || password.length() < 10 || password.length() > 40) {
                    userRegistrationPassword.setError("Необходимо придумать пароль длиной от 10 до 40 символов");
                }
                if (repeatPassword.isEmpty()) {
                    userRegistrationRepeatPassword.setError("Необходимо повторно ввести пароль");
                    return;
                }
                if (password.equals(repeatPassword)) {
                    userRegistrationRepeatPassword.setText(password);
                } else {
                    userRegistrationRepeatPassword.setError("Подтверждение не совпадает с паролем");
                }

                if (password.equals(repeatPassword) && password.length() >= 10
                        && !eMail.isEmpty() && !firstName.isEmpty() && !secondName.isEmpty()
                        && splitEMail.length > 1) {
                    User newUser = new User(id, eMail, firstName, secondName, password);
                    usersDataBase.push().setValue(newUser);

                    mAuth.createUserWithEmailAndPassword(eMail, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG_USER_REGISTER, "createUserWithEmail:success");
                                        startActivity(new Intent(RegistrationActivity.this, MenuActivity.class));
                                        finishAffinity();

                                    } else {
                                        Log.w(TAG_USER_REGISTER, "createUserWithEmail:failure", task.getException());
                                        String expText = task.getException().getMessage();
                                        if (expText.equals("The email address is badly formatted.")) {
                                            userRegistrationEMail.setError("Пожалуйста, введиту почту в формате: login@example.com");
                                        }
                                        if (expText.equals("The email address is already in use by another account.")) {
                                            userRegistrationEMail.setError("Данная почта уже используется другим пользователем.");
                                        }
                                    }
                                }
                            });
                }
            }
        });

        setProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCrop();
            }
        });
    }

    public void DownLoadFile(String addr, String path) throws IOException {
        URL url = null;
        URLConnection hurl;
        try {
            url = new URL(addr);
            hurl = url.openConnection();
            Log.d(String.valueOf(hurl.getContentLength()), "Downloading file");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = openFileOutput(path, MODE_PRIVATE);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    private void startCrop() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                imageUri = result.getUri();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                userProfilePicture.setImageBitmap(bitmap);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}