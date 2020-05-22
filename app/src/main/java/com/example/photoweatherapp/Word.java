package com.example.photoweatherapp;

public class Word {
    private byte[] image3;
    private String datetime;

    public Word(byte [] d2,String d3) {

        image3 = d2;
        datetime=d3;
    }


    public byte[] getImage3() {
        return image3;
    }
    public String getdatetime() {
        return datetime;
    }

}
