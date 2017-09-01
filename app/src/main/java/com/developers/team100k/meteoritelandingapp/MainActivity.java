package com.developers.team100k.meteoritelandingapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developers.team100k.meteoritelandingapp.Adapter.MyAdapter;
import com.developers.team100k.meteoritelandingapp.Entity.Meteorite;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

  private static String URL = "https://data.nasa.gov/resource/y77d-th95.json?$where=year%20%3E%20%272011-01-01T12:00:00%27&$$app_token=VMuBlcIIY8sM83yXAD2j4KXQV";
  private EventBus eventBus;

  private List<Meteorite> meteorites = new ArrayList<>();

  private ListView mListView;
  private MyAdapter adapter;
  private DataHandler dataHandler;
  private ProgressDialog progress;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Meteorite Landings");
    setSupportActionBar(toolbar);

    progress = new ProgressDialog(this);
    progress.setTitle("Loading data");
    progress.setMessage("Please wait...");
    progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

    adapter = new MyAdapter(MainActivity.this, meteorites);
    mListView = (ListView) findViewById(R.id.list_view);
    mListView.setAdapter(adapter);
    mListView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent mapsIntent = new Intent(MainActivity.this, MapsActivity.class);
        mapsIntent.putExtra("name", meteorites.get(position).getName());
        mapsIntent.putExtra("longitude", meteorites.get(position).getGeolocation().getCoordinates().get(0));
        mapsIntent.putExtra("latitude", meteorites.get(position).getGeolocation().getCoordinates().get(1));
        startActivity(mapsIntent);
      }
    });

    eventBus = EventBus.getDefault();
    eventBus.register(this);

    dataHandler = new DataHandler(this, URL, adapter, mListView, progress);
  }

  @Subscribe
  public void onEvent(String response){
    dataHandler.refresh();
    progress.dismiss();
    Toast.makeText(this, "jej", Toast.LENGTH_LONG).show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.item, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_refresh){
      this.recreate();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStop() {
    super.onStop();
    eventBus.unregister(this);
  }
}
