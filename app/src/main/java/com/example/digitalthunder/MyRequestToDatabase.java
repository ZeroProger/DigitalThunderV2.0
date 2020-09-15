package com.example.digitalthunder;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyRequestToDatabase implements ValueEventListener
{
    protected String name;
    protected String passwd;
    FirebaseDatabase db = null;
    DatabaseReference ref = null;
    public PasswordStatus out;

    MyRequestToDatabase(String name, String passwd)
    {
        this.name = name;
        this.passwd = passwd;
        db = FirebaseDatabase.getInstance();
        ref = db.getReference(name); // Key
        Log.d("Constructor", "Created");
    }
    public PasswordStatus getAnswer()
    {
        if(out == null) Log.d("OUT", "PIZDEC");
        else Log.d("OUT", "NE PIZDEC");
        switch (out)
        {
            case PASSWORD_NOT_FOUND:
                Log.d("Fucking test", "PASS NOT FOUND");
            case SUCCESSFUL:
                Log.d("Fucking test", "SUCCESSFUL");
            case INCORRECT_PASSWORD:
                Log.d("Fucking test", "INCORRECT PASS");
            default:
                Log.d("Fucking test", "DEFAULT");
        }
        return out;
    }
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        String pass = snapshot.getValue(String.class);

        if(pass != null)
        {
            Log.d("TESTTESTTEST", pass);
            //ref.setValue(passwd);
            //need to return true
            if(pass.equals(passwd))
            {
                this.out = PasswordStatus.SUCCESSFUL;
                Log.d("Password status", "SUCCESSFUL");
            }
            else
            {
                this.out = PasswordStatus.INCORRECT_PASSWORD;
                Log.d("Password status", "INCORRECT_PASSWORD");
            }
        }
        else
        {
            this.out = PasswordStatus.PASSWORD_NOT_FOUND;
            Log.d("Password status", "PASSWORD_NOT_FOUND");
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Log.d("База данных", "Некая ошибка доступа к базе");
    }
}