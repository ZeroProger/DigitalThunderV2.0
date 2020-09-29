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
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
                        Object[] list = td.values().toArray();
                        String[] userName = list[2].toString().split(" ");//получение информации о пользователе из ветки
                        Log.d("section 0", list[0].toString());//хреновый ход, лучше вынести в метод
                        Log.d("passwd 1", list[1].toString());
                        Log.d("section 2", list[2].toString());
                        Log.d("section 3", list[3].toString());
                        Log.d("section 4", list[4].toString());
                        if(userLogin.getText().toString().equals(list[4])) //равенство введённого логина и логина изз базы
                        {
                            Log.d("Авторизация", "Верно");
                            Log.d("Имя", userName[0]);
                            Intent mainMenu = new Intent(MainActivity.this, MainMenu.class);
                            mainMenu.putExtra("User name", userName[0]);
                            mainMenu.putExtra("User second name", userName[1]);
                            startActivity(mainMenu);
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
    public void ReaderDatabase(final String name)
    {
        Exchanger<String> exchanger = new Exchanger<>();
        Thread readerDB = new Thread(new Runnable() {
            //Проверить получение ссылки по имени
            @Override
            public void run() {
                reference = firebaseDatabase.getReference("passwords/"+name);
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String pass =(String) snapshot.getValue(String.class);
                        if(pass != null)Log.d("ReaderDB", pass);
                        else Log.d("ReaderDB", "Password is missing");
                        //написать обмен данными между потоками
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("ReaderDB", "ERROR");
                    }
                });
            }
        });
        readerDB.start();
    }

    public static VerifyDatabaseStat VerifiDataFromXml(String fName, String sName, String section, String number)
    {
        VerifyDatabaseStat out = VerifyDatabaseStat.HUMAN_IS_WRONG;
        try {
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("data/data/com.example.digitalthunder/files/database.xml"));
            //Node rootel = doc.getDocumentElement();
            NodeList firstNamesNodes = doc.getElementsByTagName("first_name");//Получение листа нодов по тегу <first_name>
            NodeList secondNamesNodes = doc.getElementsByTagName("second_name");
            NodeList sections = doc.getElementsByTagName("achive_way");
            NodeList numbers = doc.getElementsByTagName("number");

            for (int i = 0; i < firstNamesNodes.getLength() && i < secondNamesNodes.getLength(); i++)
            {
                if(firstNamesNodes.item(i).getTextContent().equals(fName) && secondNamesNodes.item(i).getTextContent().equals(sName))
                {
                    out = VerifyDatabaseStat.COMPLETED;
                    section = sections.item(i).getTextContent();
                    number = numbers.item(i).getTextContent();
                }
            }
        }
        catch (Exception e)
        {
            Log.d("Слышь ты, программюка", "Опять что-то пошло не так!");
            return VerifyDatabaseStat.ERROR;
        }
        return out;
    }

}
