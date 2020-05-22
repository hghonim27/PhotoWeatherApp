package com.example.photoweatherapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    ArrayList<Word> Images = new ArrayList<Word>();

        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            //adding weather photos with the captured date & time to the list in the main activity//

            loadData();
            ImageAdapter adapter = new ImageAdapter(this, Images);

            ListView rootView = (ListView) findViewById(R.id.HistoryList);
            rootView.setAdapter(adapter);

            //generating an On Item click listenr to provide a full size view for each weather photo in the main activity by passing the //
            //position of the Image to be viewd to the FullSizePhoto Activity to be viewd their///
           rootView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
               @Override
               public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                   Intent intent4 = new Intent(getApplicationContext(), FullSizePhotoActivity.class);
                   intent4.putExtra("koko",position);
                   startActivity(intent4);

               }
           });



            ///for allowing permissions///

            int PERMISSION_ALL = 1;
            String[] PERMISSIONS = {
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            };

            if (!hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            }



        //making a click listener for the new Photo button in the main activity to allow it to open the camera to capture a photo//
            Button photoButton = (Button) this.findViewById(R.id.NewPhoto);
            photoButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }

            });


        }

//My shared preferences Data//
    //Was loaded to retrieve all the photo-weather-images with their corresponding date and time to be loaded in the listview in the main activity//
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

//for Permissions//
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

//This is the first method that it enters after it returns from camera capture//
    //i took the captured photo and stored it in a bitmap format then i compresed it to a bytes then i passesd//
    //then i passesd it to the Captured Photo Activity//
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
        {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
            {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] image = stream.toByteArray();


                Intent intent1 = new Intent(this, CapturedPhotoActivity.class);
                intent1.putExtra("photo",image);
                startActivity(intent1);
            }
        }


    }

