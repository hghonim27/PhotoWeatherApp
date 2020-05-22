package com.example.photoweatherapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//our class implements the LocationListener to receive notifications from the LocationManager when the location change//

public class MyPhotoWeatherActivity extends AppCompatActivity implements LocationListener {

    ImageView imageView;
    TextView temp, condition, mylocation;
    String CITYNAME;

    ArrayList<Word> w = new ArrayList<Word>();

    private LocationManager locationManager;
    private String provider;


    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myweatherphoto);

      //receiving the captured photo from the Captured photo activity due to clicking on Attach Weather Button//
        imageView = (ImageView) findViewById(R.id.cwphoto);

        byte[] byteArrayExtra = getIntent().getByteArrayExtra("photo");
        final Bitmap bitmap = BitmapFactory.decodeByteArray(byteArrayExtra, 0, byteArrayExtra.length);

        imageView.setImageBitmap(bitmap);


//Sharing photo to facebook twitter and other apps on the mobile due to clicking on the button Share//
        final Button sharebutton = (Button) findViewById(R.id.share);
        sharebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(shareIntent.EXTRA_TEXT, "Share Image via");
                startActivity(Intent.createChooser(shareIntent, "Share Image Via"));

            }

        });
//end Sharing///


        //making an onclick listener for the Save button //
        //defining a local-date time to add it as a text beside the image//
        //i converted the MyPhotoweather Activity view after putting the weather data into it to a bitmap image//
       final Button savebutton = (Button) findViewById(R.id.save);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss -- dd/MM/yyyy");
        final String date = dtf.format(LocalDateTime.now());

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setting the buttons (save and share) visibilities to be GONE so that when i convert the layout view
                // after adding the weather condition  the buttons will not appear in the photo//
                savebutton.setVisibility(View.GONE);
                sharebutton.setVisibility(View.GONE);
                //i converted the MyPhotoweatherActivity view after putting the weather data into it to a bitmap image//
                Bitmap bm = getBitmapFromView((RelativeLayout) findViewById(R.id.relativeLayout3));
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                byte[] image = stream1.toByteArray();

                //retrieving all the data I have then i add to it my current photo and datetime//
                loadData();
                w.add(new Word(image,date));
                saveData();

//passing my photo-weather image to the main activity to display it in the list after saving it in the database //
                Intent intent3 = new Intent(MyPhotoWeatherActivity.this, MainActivity.class);
                startActivity(intent3);

            }
        });
        //making the buttons visible again//
        savebutton.setVisibility(View.VISIBLE);
        sharebutton.setVisibility(View.VISIBLE);


        //end Saving///

        temp = (TextView) findViewById(R.id.Temp);
        condition = (TextView) findViewById(R.id.condition);
        mylocation = (TextView) findViewById(R.id.Location);


//start location//

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
//

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            Settings.Secure.setLocationProviderEnabled(getContentResolver(), LocationManager.GPS_PROVIDER, true);
            //locationManager.requestLocationUpdates(provider, 400, 1, this);
            mylocation.setText("Location not available");
        }
//end location//

//start Weather//
        String content;
        Weather weather = new Weather();
        try {
            content = weather.execute("https://api.openweathermap.org/data/2.5/weather?q=" + CITYNAME + "&appid=c8b62752f4e95e383b9399950b96f07c&units=metric").get();

            //JSON
            JSONObject jsonObject = new JSONObject(content);
            String weatherData = jsonObject.getString("weather");
            String tempData = jsonObject.getString("main");


            JSONArray array = new JSONArray(weatherData);
            String description = "";

            for (int i = 0; i < array.length(); i++) {
                JSONObject ww = array.getJSONObject(i);
                description = ww.getString("description");
            }
            JSONObject allMain = new JSONObject(tempData);
            String Temperature = allMain.getString("temp");
            temp.setText(Temperature + " °C");
            condition.setText(description);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

//method that converts a view to a bitmap//
    public static Bitmap getBitmapFromView(View view) {

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();

        if (bgDrawable!=null) {
            bgDrawable.draw(canvas);
        }
        else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    //a method that saves a Word=(image+datatime)
    private void saveData(){
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(w);
        editor.putString("koko",json);
        editor.apply();

    }
//a method that retrieves all the Words//
    private void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("koko",null);
        Type type= new TypeToken<ArrayList<Word>>() {}.getType();
        w = gson.fromJson(json,type);

        if(w == null){
            w = new ArrayList<Word>();
        }
    }

// making a class weather extending the AsyncTask which is a class that extends the Object class
// to allow operations to run asynchronously in the background.
    class Weather extends AsyncTask<String, Void, String> {

        //With doInBackground AsyncTask can run tasks asynchronously on new threads.:))
        @Override
        protected String doInBackground(String... address) {

            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                int data = isr.read();
                String content = "";
                char ch;
                while (data != -1) {
                    ch= (char) data;
                    content = content + ch;
                    data=isr.read();
                }
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //end Weather//
    //start location//
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    //getting my latitude and longitude and then using the geocoder to get my city,country and place names//
    @Override
    public void onLocationChanged(Location location) {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geoCoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityname = addresses.get(0).getAdminArea();
        String countryName = addresses.get(0).getCountryName();
        String placename = addresses.get(0).getLocality();

            String [] arr = cityname.split(" ");
            CITYNAME=arr[0];
            mylocation.setText(placename+","+CITYNAME+","+countryName); //This will display the final address.


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }
    //end location//

//happy coding :))


}










