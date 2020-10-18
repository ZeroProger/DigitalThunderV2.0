package com.digitalthunder.user;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.digitalthunder.MenuActivity;
import com.digitalthunder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class EntryActivity extends AppCompatActivity {

    private static final String TAG_USER_LOGIN = "User Login in FireBase";

    private FirebaseAuth mAuth;
    private DatabaseReference usersDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        final ProgressBar entryProgressBar = findViewById(R.id.entryProgressBar);
        final Button entryButton = (Button) findViewById(R.id.entryButton);
        Button registrationButton = (Button) findViewById(R.id.registrationButton);
        TextView resetPassword = (TextView) findViewById(R.id.resetPassword);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            if (mAuth.getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(EntryActivity.this, MenuActivity.class));
                finishAffinity();
            }
        }

        entryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText userEntryEMail = findViewById(R.id.entryTextEMail);
                final EditText userEntryPassword = findViewById(R.id.entryTextPassword);

                final String eMail = userEntryEMail.getText().toString();
                String password = userEntryPassword.getText().toString();

                Log.d(TAG_USER_LOGIN, userEntryEMail.getText().toString() + ":" + userEntryPassword.getText().toString());
                Log.d(TAG_USER_LOGIN, eMail + ":" + password);
                String[] splitEMail = new String[0];
                if (eMail.contains("@")) {
                    if (eMail.split("@").length > 1) {
                        splitEMail = eMail.split("@");
                    }
                    else {
                        userEntryEMail.setError("Введите почту в формате: login@example.com");
                    }
                }
                if (eMail.isEmpty()) {
                    userEntryEMail.setError("Введите почту");
                }
                if (!eMail.contains("@") && !eMail.isEmpty() || eMail.charAt(eMail.length() - 1) == '.') {
                    userEntryEMail.setError("Введите почту в формате: login@example.com");
                }
                if (password.isEmpty()) {
                    userEntryPassword.setError("Введите пароль");
                }

                if (!eMail.isEmpty() && !password.isEmpty() && splitEMail.length > 1) {
                    entryProgressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(eMail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser().isEmailVerified()) {

                                } else {
                                    Toast.makeText(EntryActivity.this, "Пожалуйста подтвердите ваш адрес электронной почты", Toast.LENGTH_SHORT).show();
                                }
                                entryProgressBar.setVisibility(View.GONE);
                                Log.d(TAG_USER_LOGIN, "loginUserWithEmailAndPassword:success");
                                startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                                finishAffinity();
                            } else {
                                Log.w(TAG_USER_LOGIN, "loginUserWithEmailAndPassword:failure", task.getException());
                                String expText = task.getException().getMessage();
                                if (expText.equals("The email address is badly formatted.")) {
                                    userEntryEMail.setError("Введиту почту в формате: login@example.com");
                                    entryProgressBar.setVisibility(View.GONE);
                                }
                                if (expText.equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    userEntryEMail.setError("Такой аккаунт не зарегистрирован");
                                    entryProgressBar.setVisibility(View.GONE);
                                }
                                if (expText.equals("The password is invalid or the user does not have a password.")) {
                                    userEntryPassword.setError("Неверный пароль");
                                    entryProgressBar.setVisibility(View.GONE);
                                }
                                if (expText.equals("We have blocked all requests from this device due to unusual activity. Try again later. " +
                                                    "[ Access to this account has been temporarily disabled due to many failed login attempts." +
                                                    "You can immediately restore it by resetting your password or you can try again later. ]")) {
                                    userEntryPassword.setError("Превышено максимальное кол-во неудачных попыток ввода пароля. Пожалуйста, попробуйте позже");
                                    entryProgressBar.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRegistrationActivity = new Intent(EntryActivity.this, RegistrationActivity.class);
                startActivity(intentToRegistrationActivity);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToPasswordResetActivity = new Intent(EntryActivity.this, PasswordResetActivity.class);
                startActivity(intentToPasswordResetActivity);
            }
        });

    }
}