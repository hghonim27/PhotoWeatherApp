package com.example.photoweatherapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ImageAdapter extends ArrayAdapter {

    public ImageAdapter(Activity context, ArrayList<Word> words) {
        super(context, 0, words);


    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        View listItemView = convertView;

        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.history, parent, false);
        }


       Word currentWord = (Word) getItem(position);


        TextView numberTextView = (TextView) listItemView.findViewById(R.id.datetime);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        numberTextView.setText(currentWord.getdatetime());



        ImageView i = (ImageView) listItemView.findViewById(R.id.photoweatherimage);
        // Get the version number from the current AndroidFlavor object and
        // set this text on the number TextView
        Bitmap bm = BitmapFactory.decodeByteArray(currentWord.getImage3(),0,currentWord.getImage3().length);
        i.setImageBitmap(bm);

        return listItemView;

    }

}

