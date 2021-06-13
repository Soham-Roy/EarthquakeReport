package com.example.quakereport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<Earthquake>>, onItemClicked{

    private String URL_GET = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&limit=20";
    public static ArrayList<Earthquake> data;

    private static final int LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earthquake);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if ( ni != null && ni.isConnected() ){
            LoaderManager lm = getSupportLoaderManager();
            lm.initLoader(LOADER_ID, null, this);
        }
        else {
            View v = findViewById(R.id.progress_circle);
            v.setVisibility(View.GONE);

            RecyclerView rv = (RecyclerView) findViewById(R.id.list_shown);
            rv.setVisibility(View.GONE);

            TextView tv = (TextView) findViewById(R.id.empty_data_msg);
            tv.setText("No Internet Connection!");
        }

    }

    @Override
    public void onItemClick(int pos) {
        Uri url = Uri.parse( data.get(pos).getUrl() );
        Intent i = new Intent(Intent.ACTION_VIEW, url);
        if (i.resolveActivity(getPackageManager()) != null) {
            startActivity(i);
        }
        else {
            Toast.makeText(this, "You don't have appropriate app to view this page",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int id, Bundle args) {
        Log.i("Method","onCreateLoader is called");
        return new EarthquakeAsyncTask(this, URL_GET);
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        View v = findViewById(R.id.progress_circle);
        v.setVisibility(View.GONE);

        RecyclerView rV = (RecyclerView) findViewById(R.id.list_shown);
        MyAdapter adapter = new MyAdapter((ArrayList<Earthquake>) data, EarthquakeActivity.this);
        rV.setHasFixedSize(true);
        rV.setAdapter(adapter);
        rV.setLayoutManager(new LinearLayoutManager(EarthquakeActivity.this));
        Log.i("Method","onLoadFinished is called");

        if ( data.isEmpty() ){
            rV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.i("Method","onLoaderReset is called");
        data.clear();
    }

    private static class EarthquakeAsyncTask extends AsyncTaskLoader<List<Earthquake>>{

        String dataToFetch = null;

        public EarthquakeAsyncTask(Context context, String url) {
            super(context);
            dataToFetch = url;
        }

        @Override
        public List<Earthquake> loadInBackground() {
            Log.i("Method","loadInBackground is called");
            if ( dataToFetch == null ){
                return null;
            }

            List<Earthquake> here = QueryUtils.extractEarthquakes(dataToFetch);
            data = (ArrayList<Earthquake>) here;
            return here;
        }

        @Override
        protected void onStartLoading() {
            Log.i("Method","onStartLoading is called");
            forceLoad();
        }
    }

}