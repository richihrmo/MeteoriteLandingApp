package com.developers.team100k.meteoritelandingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private static String URL = "https://data.nasa.gov/resource/y77d-th95.json?$where=year%20%3E%20%272011-01-01T12:00:00%27&$$app_token=VMuBlcIIY8sM83yXAD2j4KXQV";

  private String filename = "tempData";

  private String json;
  private List<Meteorite> meteorites = new ArrayList<>();

  private ListView mListView;
  private MyAdapter adapter;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Meteorite Landings");

//    Toast.makeText(this, String.format("%.2f", "15960.12243453"), Toast.LENGTH_LONG);

    json = read_file(this, filename);
    if (json.isEmpty()){
      if (!isOnline()) Toast.makeText(this, "Please connect to the Internet", Toast.LENGTH_LONG).show();
    } else JsonToCollection(json);

    if (isOnline()){
      JsonFromURL();
    }

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
  }

  /**
   * Convert JSON data to Java Collection using GSON
   * @param json
   */
  public void JsonToCollection(String json){
    Type type = new TypeToken<List<Meteorite>>(){}.getType();
    meteorites = new Gson().fromJson(json, type);
//    meteorites = getList(Meteorite[].class, json);
    Collections.sort(meteorites, new CustomComparator());
  }

  public static final <T> List<T> getList(final Class<T[]> clazz, final String json)
  {
    final T[] jsonToObject = new Gson().fromJson(json, clazz);

    return Arrays.asList(jsonToObject);
  }

  /**
   * Get JSON data from NASA API URL
   */
  public void JsonFromURL(){
    RequestQueue queue = Volley.newRequestQueue(this);
    StringRequest stringRequest = new StringRequest(URL, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        json = response;
        writeToFile();
        JsonToCollection(json);
        adapter.setList(meteorites);
        mListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT).show();
      }
    });
    queue.add(stringRequest);
  }

  /**
   *  write JSON data to file for offline access to data
   */
  public void writeToFile(){
    FileOutputStream outputStream;
    try {
      outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
      outputStream.write(json.getBytes());
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   *  read JSON data from file for offline access to data when internet is not available
   */
  public String read_file(Context context, String filename) {
    try {
      FileInputStream fis = context.openFileInput(filename);
      InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
      BufferedReader bufferedReader = new BufferedReader(isr);
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        sb.append(line).append("\n");
      }
      return sb.toString();
    } catch (FileNotFoundException e) {
      return "";
    } catch (UnsupportedEncodingException e) {
      return "";
    } catch (IOException e) {
      return "";
    }
  }

  public class CustomComparator implements Comparator<Meteorite> {
    @Override
    public int compare(Meteorite o1, Meteorite o2) {
      if (o1.getMass() == null && o2.getMass() == null)
        return 0;
      if (o1.getMass() == null && o2.getMass() != null)
        return 1;
      if (o1.getMass() != null && o2.getMass() == null)
        return -1;
      return Float.valueOf(o2.getMass()).compareTo(Float.valueOf(o1.getMass()));
    }
  }

  /**
   * check whether there is Internet connection or not
   * @return boolean value
   */
  public boolean isOnline() {
    ConnectivityManager cm =
        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }

}
