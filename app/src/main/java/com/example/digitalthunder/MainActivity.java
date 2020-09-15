package com.example.digitalthunder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

//Test commit 1
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button Autoris_but;
    TextView textView_name;
    TextView textView_secondName;
    CompoundButton passwdSwitch;
    boolean passwordSwitchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Autoris_but = (Button) findViewById(R.id.autoris_but);//Находим кнопку
        Autoris_but.setOnClickListener(this);
        passwdSwitch = (Switch)findViewById(R.id.switch1);
        passwdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                passwordSwitchStatus = b;
            }
        });

        textView_name = (TextView) findViewById(R.id.editTextTextPersonName);
        textView_secondName = (TextView) findViewById(R.id.editTextTextPersonName2);
        //Тут потом добавить обновление базы по таймеру
        Thread secondThread = new Thread(new Runnable() {
            //@SuppressLint("SdCardPath")
            @Override
            public void run() {
                try {
                    DownLoadFile("https://admtyumen.ru/files/opendata/7202136720-GiftedChildren/7202136720.94.2.xml", "database.xml");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Downloading database", "ERROR");
                }
            }
        });
        secondThread.start();



    }
    @Override
    public void onClick(View view) {
        Intent MainMenu = new Intent(this, MainMenu.class);
        CharSequence usName = textView_name.getText();
        CharSequence usSecondName = textView_secondName.getText();
        try
        {
            if(VerifiDataFromXml(textView_name, textView_secondName).equals("Completed"))
            {
                MainMenu.putExtra("User name", usName);
                MainMenu.putExtra("User second name", usSecondName);
                Log.d("Верификация данных", "Успешно");
                //верификация пароля
                if(passwordSwitchStatus)//Switch is ON
                {
                    if(CheckPasswd(usName.toString()+usSecondName.toString(), "") == PasswordStatus.PASSWORD_NOT_FOUND)
                    {
                        //Пароль не найден
                        //Создать новый пароль
                        Log.d("Пароль", "Необходимо создать пароль");
                        Log.d("Активность", "Создание пароля");

                        //Создать пароль в следующей активности
                        Intent createPasswdActivity = new Intent(this, CreatingPasswordActivity.class);
                        createPasswdActivity.putExtra("username", usName.toString()+usSecondName.toString());
                        startActivity(createPasswdActivity);
                    }
                    else
                    {
                        TextView textStatus = (TextView) findViewById(R.id.textStatus);
                        textStatus.setTextColor(Color.RED);
                        textStatus.setText("У вас существует пароль, введите его");
                        Log.d("Пароль", "Пароль уже существует");
                    }
                }
                else
                {//Пользователь ввёл пароль
                    TextView password = findViewById(R.id.editTextTextPassword);
                    /*
                    switch (checkPasswdReturn){
                        case "Incorrect passwd":
                            Log.d("Пароль", "Пароль неверный");
                            TextView textStatus = (TextView) findViewById(R.id.textStatus);
                            textStatus.setTextColor(Color.RED);
                            textStatus.setText("Ошибка ввода пароля");
                            break;
                        case "Successful":
                            Log.d("Пароль", "Пароль верен");
                            Log.d("Активность", "Старт");
                            startActivity(MainMenu);
                            break;
                    }
                    */
                }
            }
            else
            {
                TextView textStatus = (TextView) findViewById(R.id.textStatus);
                textStatus.setTextColor(Color.RED);
                textStatus.setText("Ошибка в вводе имени");
                Log.d("Верификация данных", "Ошибка");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public PasswordStatus CheckPasswd(String name, final String passwd)
    {
        /*
        Проверить существует ли пароль
        Если не существет, вернуть "Passwd not found"
         */
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(name); // Key
        // Attach listener
        MyRequestToDatabase myrequest = new MyRequestToDatabase(name, passwd);
        ref.addValueEventListener(myrequest);

        return myrequest.getAnswer();
        /*
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pass = (String) snapshot.getValue(String.class);
                if(pass != null)
                {
                    if(pass.equals(passwd))
                    {
                        //out[0] = "Successful";
                        Log.d("Password checking", "Successful");
                    }
                    else
                    {
                        //out[0] = "Incorrect passwd";
                        Log.d("Password checking", "Incorrect password");
                    }
                }
                else
                {
                    //out[0] = "Passwd not found";
                    Log.d("Password checking", "Password not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("База данных", "Некая ошибка");
            }
        });
        */
    }
    public boolean CreatePasswd(final String name, final String passwd)
    {
        final boolean[] out = {false};
        /*
        Сначала проверить существует ли пароль
        Если пароль существует, вернуть false
        Если пароль не существует, создать его и вернуть true
         */
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference(name); // Key
        MyRequestToDatabase myrequest = new MyRequestToDatabase(name, passwd);

        if(myrequest.getAnswer() == PasswordStatus.PASSWORD_NOT_FOUND)
        {
            ref.setValue(passwd);
            return true;
        }
        return false;
    }
    public String VerifiDataFromXml(TextView fName, TextView sName)
    {
        String out = "Human is wrong";
        try {
            DocumentBuilder xml = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = xml.parse(new File("data/data/com.example.digitalthunder/files/database.xml"));
            //Node rootel = doc.getDocumentElement();
            NodeList firstNamesNodes = doc.getElementsByTagName("first_name");//Получение листа нодов по тегу <first_name>
            NodeList secondNamesNodes = doc.getElementsByTagName("second_name");

            for (int i = 0; i < firstNamesNodes.getLength() && i < secondNamesNodes.getLength(); i++)
            {
                if(firstNamesNodes.item(i).getTextContent().equals(fName.getText().toString()) && secondNamesNodes.item(i).getTextContent().equals(sName.getText().toString()))
                {
                    out = "Completed";
                }
            }
        }
        catch (Exception e)
        {
            Log.d("Слышь ты, программюка", "Опять что-то пошло не так!");
            return "Error";
        }
        return out;
    }
    public void DownLoadFile(String addr, String path) throws IOException
    {
        URL url = null;
        URLConnection hurl = null;
        try {
            url = new URL(addr);
            hurl = url.openConnection();
            Log.d(String.valueOf(hurl.getContentLength()), "WORKING!!!");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = openFileOutput(path, MODE_PRIVATE);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

    }

}
