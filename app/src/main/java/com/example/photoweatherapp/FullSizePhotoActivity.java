package com.example.photoweatherapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class FullSizePhotoActivity extends AppCompatActivity {
    ImageView image;
    ArrayList<Word> Images = new ArrayList<Word>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finalphoto);

            loadData();

         image = (ImageView) findViewById(R.id.finalp);

      int p = getIntent().getIntExtra("koko",-1);

        Word x = Images.get(p);
        byte [] byteArrayExtra= x.getImage3();
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayExtra, 0, byteArrayExtra.length);

        image.setImageBitmap(bitmap);
    }

    private void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("koko",null);
        Type type= new TypeToken<ArrayList<Word>>() {}.getType();
        Images = gson.fromJson(json,type);

        if(Images == null){
            Images = new ArrayList<Word>();
        }
    }


}