package com.example.digitalthunder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

//Test commit 1
public class MainActivity extends AppCompatActivity{
    Button Autoris_but;
    TextView textView_name;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference reference = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Autoris_but = (Button) findViewById(R.id.autoris_but);//Находим кнопку входа
        Button regButton = (Button) findViewById(R.id.registrationButton);
        setTitle("Вход в систему");
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registrationActivity = new Intent(MainActivity.this, CreatingPasswordActivity.class);
                startActivity(registrationActivity);
            }
        });
        Autoris_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                TextView userLogin = (TextView) findViewById(R.id.editLogin);
                final DatabaseReference reference = database.getReference("users/" + userLogin.getText().toString());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //int value = (int)snapshot.getChildrenCount();

                        TextView userLogin = (TextView) findViewById(R.id.editLogin);
                        TextView userPassword = (TextView) findViewById(R.id.editTextTextPassword);

                        Map<String, Object> td = (HashMap<String,Object>) snapshot.getValue();
                        if(td != null) {
                            Object[] list = td.values().toArray();
                            String[] userName = list[2].toString().split(" ");//получение информации о пользователе из ветки
                            Log.d("section 0", list[0].toString());//хреновый ход, лучше вынести в метод
                            Log.d("passwd 1", list[1].toString());
                            Log.d("section 2", list[2].toString());
                            Log.d("section 3", list[3].toString());
                            Log.d("section 4", list[4].toString());
                            if (userLogin.getText().toString().equals(list[4])) //равенство введённого логина и логина изз базы
                            {
                                Log.d("Авторизация", "Верно");
                                Log.d("Имя", userName[0]);
                                Intent mainMenu = new Intent(MainActivity.this, MainMenu.class);
                                mainMenu.putExtra("User name", userName[0]);
                                mainMenu.putExtra("User second name", userName[1]);
                                startActivity(mainMenu);
                            }
                        }
                        else
                        {
                            Log.d("База данных", "td is null");
                        }
                        reference.removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Чтение firebase", error.getMessage());
                        reference.removeEventListener(this);
                    }
                });
            }
        });
        textView_name = (TextView) findViewById(R.id.editLogin);
        //Тут потом добавить обновление базы по таймера
    }
}
