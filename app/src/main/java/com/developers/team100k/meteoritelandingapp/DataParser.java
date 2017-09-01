package com.developers.team100k.meteoritelandingapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Richard Hrmo.
 */
public class DataParser {

  private Context mContext;
  private String URL;
  private EventBus eventBus;
  private String json;
  private static String filename = "tempData";

  public List<Meteorite> getMeteorites() {
    return meteorites;
  }

  private List<Meteorite> meteorites;

  public DataParser(Context context, String URL){
    this.mContext = context;
    this.URL = URL;
    eventBus = EventBus.getDefault();
    init();
  }

  public void init(){
    json = readFile(mContext, filename);
    if (!json.isEmpty()) jsonToCollection(json);
    if (isOnline()) jsonFromURL();
    if (json.isEmpty() && !isOnline()) Toast.makeText(mContext, "No data \nConnect to Internet and press refresh", Toast.LENGTH_LONG).show();
  }

  /**
   * Convert JSON data to Java Collection using GSON
   * @param json
   */
  public void jsonToCollection(String json){
    Type type = new TypeToken<List<Meteorite>>(){}.getType();
    meteorites = new Gson().fromJson(json, type);
    Collections.sort(meteorites, new CustomComparator());
    eventBus.post(json);
  }

  /**
   * Get JSON data from NASA API URL
   */
  public void jsonFromURL(){
    RequestQueue queue = Volley.newRequestQueue(mContext);
    StringRequest stringRequest = new StringRequest(URL, new Listener<String>() {
      @Override
      public void onResponse(String response) {
        json = response;
        writeToFile(mContext);
        jsonToCollection(json);
        eventBus.post(json);
      }
    }, new ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast.makeText(mContext, "Failed to connect", Toast.LENGTH_SHORT).show();
      }
    });
    queue.add(stringRequest);
  }

  /**
   *  write JSON data to file for offline access to data
   */
  public void writeToFile(Context context){
    FileOutputStream outputStream;
    try {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
      outputStream.write(json.getBytes());
      outputStream.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  /**
   *  read JSON data from file for offline access to data when internet is not available
   */
  public String readFile(Context context, String filename) {
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
        (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo netInfo = cm.getActiveNetworkInfo();
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }

}
