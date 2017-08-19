package com.developers.team100k.meteoritelandingapp;

import android.app.DownloadManager.Request;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developers.team100k.meteoritelandingapp.Adapter.MyAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import okhttp3.ResponseBody;
import org.json.JSONObject;
import org.json.JSONTokener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import org.json.*;

public class MainActivity extends AppCompatActivity {

  private static String url = "https://data.nasa.gov/resource/y77d-th95.json?$$app_token=VMuBlcIIY8sM83yXAD2j4KXQV";
  private List<Meteorite> mMeteoriteList;

  private String json;
  private Gson gson = new Gson();
  private List<Meteorite> meteorites = new ArrayList<>();

  private ListView mListView;
  private MyAdapter adapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Meteorite Landings");

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

    RequestQueue queue = Volley.newRequestQueue(this);
    StringRequest stringRequest = new StringRequest(url, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        json = response;
        Jsonrandom(json);
        adapter.setList(meteorites);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(MainActivity.this, "fail", Toast.LENGTH_SHORT).show();
      }
    });
    queue.add(stringRequest);
  }

  public void Jsonrandom(String json){
//    Type type = new TypeToken<List<Meteorite>>(){}.getType();
//    meteorites = gson.fromJson(json, type);
    meteorites = getList(Meteorite[].class, json);
    Collections.sort(meteorites, new CustomComparator());
  }

  public static final <T> List<T> getList(final Class<T[]> clazz, final String json)
  {
    final T[] jsonToObject = new Gson().fromJson(json, clazz);

    return Arrays.asList(jsonToObject);
  }

  public class CustomComparator implements Comparator<Meteorite> {
    @Override
    public int compare(Meteorite o1, Meteorite o2) {
      System.out.println(Integer.valueOf(o2.getMass()).compareTo(Integer.valueOf(o1.getMass())));
      return Integer.valueOf(o2.getMass()).compareTo(Integer.valueOf(o1.getMass()));
    }
  }

}
