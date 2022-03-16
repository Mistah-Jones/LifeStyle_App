package com.example.myapplication.weatherbackend;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Debug;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";
    private static String APPIDQUERY = "&appid=";
    private static final String app_id="dbc96dcde7ca653643a7586eee5ec911";

    private static String BASE_ICON_URL_START = "https://openweathermap.org/img/wn/";
    private static String BASE_ICON_URL_END = "@2x.png";

    public static URL buildURLFromString(String location){
        URL myURL = null;
        try{
            myURL = new URL(BASE_URL + location + APPIDQUERY + app_id);
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return myURL;
    }

    public static String getDataFromURL(URL url) throws IOException{
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            //The scanner trick: search for the next "beginning" of the input stream
            //No need to user BufferedReader
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if(hasInput){
                return scanner.next();
            }
            else{
                return null;
            }

        } catch (Error e) {
            String message = e.getMessage();
            e.printStackTrace();
        }
        // Should never run
        return null;
    }

    // Method edited from Stack Overflow:
    // https://stackoverflow.com/questions/2471935/how-to-load-an-imageview-by-url-in-android
    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = BASE_ICON_URL_START + urls[0] + BASE_ICON_URL_END;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}