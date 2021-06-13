package com.example.quakereport;

import android.util.Log;

import com.example.quakereport.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private QueryUtils() {
    }

    public static ArrayList<Earthquake> extractEarthquakes(String urlWant) {

        Log.i("QueryUtils", "extractEarthquakes method is called");

        ArrayList<Earthquake> earthquakes = new ArrayList<>();
        String jsonReq = null;
        try {
            jsonReq = makeHttpRequest( createUrl(urlWant) );
        } catch (IOException e) {
            Log.e("Error", "Problem parsing data from server", e);
        }

        try {

            JSONObject all = new JSONObject( jsonReq );
            JSONArray features = all.getJSONArray("features");

            for ( int i = 0; i < features.length(); ++i ){

                JSONObject earthquake = features.getJSONObject(i);
                JSONObject properties = earthquake.getJSONObject("properties");

                long time = Long.parseLong( properties.getString("time") );

                String url = properties.getString("url");

                Earthquake here = new Earthquake( properties.getString("place"),
                        time,
                        Float.parseFloat( properties.getString("mag") ), url );

                earthquakes.add(here);

            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return earthquakes;
    }

    public static URL createUrl(String s){
        URL url = null;
        try {
            url = new URL(s);
        }
        catch (MalformedURLException e){
            Log.e("Error", "Problem forming the URL", e );
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        if ( url == null ){
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if ( urlConnection.getResponseCode() == 200 ){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream( inputStream );
            }
        }
        catch (IOException e){
            Log.e("Error", "Problem making the connection", e);
        }
        finally {
            if ( urlConnection != null ){
                urlConnection.disconnect();
            }

            if ( inputStream != null ){
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    public static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder here = new StringBuilder();
        if ( inputStream != null ){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader br = new BufferedReader(inputStreamReader);
            String line = br.readLine();
            while ( line != null ){
                here.append(line);
                line = br.readLine();
            }
        }
        return here.toString();
    }



}