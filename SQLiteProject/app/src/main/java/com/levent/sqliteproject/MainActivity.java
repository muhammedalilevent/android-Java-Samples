package com.levent.sqliteproject;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SQLiteDatabase database = this.openOrCreateDatabase("Musicians",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE if not exists musicians(id INTEGER PRIMARY KEY,name VARCHAR,age INTEGER)");

            //EKLEME
            //database.execSQL("INSERT INTO musicians (name,age) VALUES('James',50)");
            //database.execSQL("INSERT INTO musicians (name,age) VALUES('Lauren',30)");
            //database.execSQL("INSERT INTO musicians (name,age) VALUES('Kirk',35)");

            //GÜNCELLEME
            //database.execSQL("UPDATE musicians  SET age = 61 WHERE name = 'Lauren'");
            //Okumak için Cursor'a ihtiyacımız var

            //SİLME
            database.execSQL("DELETE FROM musicians WHERE id = 2");

            Cursor cursor = database.rawQuery("SELECT * from musicians",null);

            int nameIX = cursor.getColumnIndex("name");
            int ageIX = cursor.getColumnIndex("age");
            int idIX = cursor.getColumnIndex("id");

            while(cursor.moveToNext()){

                System.out.println("Name : " + cursor.getString(nameIX) );
                System.out.println("Age : " + cursor.getString(ageIX) );
                System.out.println("id : " + cursor.getInt(idIX) );

            }
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}