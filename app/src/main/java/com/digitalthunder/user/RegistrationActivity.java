package com.digitalthunder.user;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.digitalthunder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    //log
    private static final String TAG_USER_REGISTER = "User Register in FireBase";

    //FireBase
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private FirebaseDatabase fDatabase;
    private FirebaseStorage fStorage;
    private DatabaseReference fDatabaseReference;
    private StorageReference fStorageReference;
    private String userID;

    //activity_registration.xml
    Button createAccountButton;
    FloatingActionButton setProfilePicture;
    CircleImageView registrationUserProfilePicture;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final ProgressBar registrationProgressBar = findViewById(R.id.registrationProgressBar);
        createAccountButton = findViewById(R.id.createAccountButton);
        setProfilePicture = findViewById(R.id.setProfilePicture);
        registrationUserProfilePicture = findViewById(R.id.registrationUserProfilePicture);

        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        userID = fUser.getUid();
        fDatabase = FirebaseDatabase.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fStorageReference = FirebaseStorage.getInstance().getReference().child("image_profile_picture");
        fDatabaseReference = fDatabase.getReference("user");

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
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View view) {
                final EditText userRegistrationEMail = findViewById(R.id.registrationEmail);
                final EditText userRegistrationFirstName = findViewById(R.id.registrationFirstName);
                final EditText userRegistrationSecondName = findViewById(R.id.registrationSecondName);
                final EditText userRegistrationPassword = findViewById(R.id.registrationPassword);
                final EditText userRegistrationRepeatPassword = findViewById(R.id.registrationRepeatPassword);

                final String eMail = userRegistrationEMail.getText().toString();
                final String firstName = userRegistrationFirstName.getText().toString();
                final String secondName = userRegistrationSecondName.getText().toString();
                final String password = userRegistrationPassword.getText().toString();
                final String repeatPassword = userRegistrationRepeatPassword.getText().toString();
                Log.d(TAG_USER_REGISTER, eMail + ":" + password);

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
                        && !eMail.isEmpty() && !firstName.isEmpty() && !secondName.isEmpty()) {
                    registrationProgressBar.setVisibility(View.VISIBLE);
                    fAuth.createUserWithEmailAndPassword(eMail, password)
                            .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                                @SuppressLint("LongLogTag")
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> taskSendEMail) {
                                                if (taskSendEMail.isSuccessful()) {
                                                    UploadInfoInDataBase(eMail, firstName, secondName, password);
                                                    VerifyEMailToast();

                                                    registrationProgressBar.setVisibility(View.GONE);
                                                    startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                                    finishAffinity();
                                                } else {
                                                    String expText = taskSendEMail.getException().getMessage();
                                                    Log.d(TAG_USER_REGISTER, "Fail email send: " + taskSendEMail.getException());
                                                    if (expText.equals("The email corresponding to this action failed to send as the provided recipient email address is invalid. [ Missing recipients ]")) {
                                                        userRegistrationEMail.setError("Ошибка отправки письма подтверждения. Пожалуйста, введите корректную почту.");
                                                    }
                                                    registrationProgressBar.setVisibility(View.GONE);
                                                }
                                            }
                                        });
                                    } else {
                                        Log.w(TAG_USER_REGISTER, "createUserWithEmail:failure", task.getException());
                                        String expText = task.getException().getMessage();
                                        if (expText.equals("The email address is badly formatted.")) {
                                            userRegistrationEMail.setError("Пожалуйста, введиту почту в формате: login@example.com");
                                        }
                                        if (expText.equals("The email address is already in use by another account.")) {
                                            userRegistrationEMail.setError("Данная почта уже используется другим пользователем.");
                                        }
                                        registrationProgressBar.setVisibility(View.GONE);
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

    private void VerifyEMailToast() {
        Toast.makeText(RegistrationActivity.this, "Для завершения регистрации пройдите по ссылке в письме, " +
                "отправленном вам на электронную почту.", Toast.LENGTH_LONG).show();
    }

    private void UploadInfoInDataBase(final String eMail, final String firstName, final String secondName, final String password) {
        final StorageReference imageName = fStorageReference.child("image_" + userID);
        imageName.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("USER_PROFILE_PICTURE", "Success");
                imageName.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String imageURL = String.valueOf(uri);
                        User newUser = new User(userID, eMail, firstName, secondName, password, imageURL);
                        fDatabaseReference.child(userID).setValue(newUser);
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

    private void DownLoadFile(String addr, String path) throws IOException {
        URL url = null;
        URLConnection hurl;
        try {
            url = new URL(addr);
            hurl = url.openConnection();
            Log.d(String.valueOf(hurl.getContentLength()), "Downloading file");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(Objects.requireNonNull(url).openStream());
        FileOutputStream fos = openFileOutput(path, MODE_PRIVATE);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                if (result != null) {
                    imageUri = result.getUri();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    registrationUserProfilePicture.setImageBitmap(bitmap);
               }
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