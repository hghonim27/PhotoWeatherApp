package com.example.photoweatherapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class CapturedPhotoActivity extends AppCompatActivity {
    ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.capturedphoto);

        //receiving the captured photo from the main activity then converting it to bitmap and //
        // finally setting it in the image view of the captured photo activity//
        imageView = (ImageView) findViewById(R.id.CapturedPhoto);

        byte[] byteArrayExtra = getIntent().getByteArrayExtra("photo");
        final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayExtra, 0, byteArrayExtra.length);

        imageView.setImageBitmap(bitmap);

//setting on click listener for the Attach weather data button and
// passing the captured photo to the MyPhotoWeatherActivity in an intent//

            Button attachweather = (Button) this.findViewById(R.id.AttachWeatherdata);
            attachweather.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] image = stream.toByteArray();


                    Intent intent2 = new Intent(CapturedPhotoActivity.this, MyPhotoWeatherActivity.class);
                    intent2.putExtra("photo",image);
                    startActivity(intent2);
                }

            });

    }
}