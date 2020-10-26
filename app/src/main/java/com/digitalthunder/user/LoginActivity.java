package com.digitalthunder.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.digitalthunder.MenuActivity;
import com.digitalthunder.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG_USER_LOGIN = "User Login in FireBase";

    private FirebaseAuth fAuth;
    private DatabaseReference fDatabaseReference;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressBar entryProgressBar = findViewById(R.id.entryProgressBar);
        final Button entryButton = (Button) findViewById(R.id.entryButton);
        Button registrationButton = (Button) findViewById(R.id.registrationButton);
        TextView resetPassword = (TextView) findViewById(R.id.resetPassword);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null) {
            if (fAuth.getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finishAffinity();
            }
        }

        entryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText userEntryEMail = findViewById(R.id.entryTextEMail);
                final EditText userEntryPassword = findViewById(R.id.entryTextPassword);

                String eMail = userEntryEMail.getText().toString();
                String password = userEntryPassword.getText().toString();

                if (eMail.length() > 0 && password.length() > 0) {
                    String[] splitEMail = new String[0];
                    if (eMail.contains("@")) {
                        if (eMail.split("@").length > 1) {
                            splitEMail = eMail.split("@");
                        }
                        else {
                            userEntryEMail.setError("Введите почту в формате: login@example.com");
                        }
                    }
                    if (!eMail.contains("@") || eMail.charAt(eMail.length() - 1) == '.') {
                        userEntryEMail.setError("Введите почту в формате: login@example.com");
                    }

                    if (splitEMail.length > 1) {
                        entryProgressBar.setVisibility(View.VISIBLE);
                        fAuth.signInWithEmailAndPassword(eMail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if (fAuth.getCurrentUser().isEmailVerified()) {
                                        entryProgressBar.setVisibility(View.GONE);
                                        Log.d(TAG_USER_LOGIN, "loginUserWithEmailAndPassword:success");
                                        startActivity(new Intent(getApplicationContext(), MenuActivity.class));
                                        finishAffinity();
                                    } else {
                                        entryProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Пожалуйста подтвердите ваш адрес электронной почты", Toast.LENGTH_LONG).show();
                                    }
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

                } else {
                    if (eMail.isEmpty()) {
                        userEntryEMail.setError("Введите почту");
                    }
                    if (password.isEmpty()) {
                        userEntryPassword.setError("Введите пароль");
                    }
                }
            }
        });


        final boolean[] isShowPassword = {false};
        final EditText userEntryPassword = findViewById(R.id.entryTextPassword);
        userEntryPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int cursorPosition = userEntryPassword.getSelectionStart();
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= userEntryPassword.getRight() - userEntryPassword.getTotalPaddingRight()) {
                        if (!isShowPassword[0]) {
                            isShowPassword[0] = true;
                            userEntryPassword.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_password, null), null,
                                    getResources().getDrawable(R.drawable.ic_hide_pass, null), null);
                            userEntryPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            userEntryPassword.setSelection(cursorPosition);
                            return true;
                        } else {
                            isShowPassword[0] = false;
                            userEntryPassword.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_password, null), null,
                                    getResources().getDrawable(R.drawable.ic_show_pass, null), null);
                            userEntryPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            userEntryPassword.setSelection(cursorPosition);
                        }
                    }
                }
                return false;
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToRegistrationActivity = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intentToRegistrationActivity);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowRecoverPasswordDialog();
            }
        });

    }

    private void ShowRecoverPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Восстановление пароля");

        LinearLayout linearLayout = new LinearLayout(this);

        final EditText recoverPasswordEMail = new EditText(this);
        recoverPasswordEMail.setHint("Введите почту");
        recoverPasswordEMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        recoverPasswordEMail.setMinEms(10);
        recoverPasswordEMail.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        recoverPasswordEMail.setTextSize(16);

        linearLayout.addView(recoverPasswordEMail);
        linearLayout.setPadding(40,40,40,40);

        builder.setView(linearLayout);

        builder.setPositiveButton("Восстановить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String eMail = recoverPasswordEMail.getText().toString().trim();
                Log.d("RECOVER_PASS_EMAIL", eMail);
                if (!eMail.equals("")) {
                    BeginRecoveryPassword(eMail);
                }
            }
        });

        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void BeginRecoveryPassword(String eMail) {
        final ProgressDialog progressDialog =  new ProgressDialog(this);
        progressDialog.setMessage("Отправка письма...");
        progressDialog.show();
        fAuth.sendPasswordResetEmail(eMail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Письмо для восстановления пароля отправлено", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    String expText = task.getException().getMessage();
                    Log.d("RECOVER_PASS", expText);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.d("RECOVER_PASS", e.getMessage());
                if (e.getMessage().equals("The email address is badly formatted.")) {
                    Toast.makeText(LoginActivity.this, "Ошибка. Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                }
                if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                    Toast.makeText(LoginActivity.this, "Такой аккаунт не зарегистрирован", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}