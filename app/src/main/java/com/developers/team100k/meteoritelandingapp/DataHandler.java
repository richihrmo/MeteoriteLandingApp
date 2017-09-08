package com.developers.team100k.meteoritelandingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;
import com.developers.team100k.meteoritelandingapp.Adapter.MyAdapter;
import com.developers.team100k.meteoritelandingapp.Entity.Meteorite;
import java.util.List;

/**
 * Data handling class
 * Methods for Activity refresh and Data refresh
 */

public class DataHandler {

  private Context mContext;
  private String URL;
  private MyAdapter adapter;
  private ListView mListView;
  private ProgressDialog progress;

  public DataParser getDataParser() {
    return mDataParser;
  }

  private DataParser mDataParser;

  public DataHandler(Context context, String URL, MyAdapter adapter, ListView mListView, ProgressDialog progress){
    this.mContext = context;
    this.URL = URL;
    this.adapter = adapter;
    this.mListView = mListView;
    this.progress = progress;
    init();
  }

  public void init(){
    mDataParser = new DataParser(mContext, URL);

    String json = mDataParser.getJson();
    if (!json.isEmpty()) mDataParser.jsonToCollection(json);
    downloadRefresh();
    if (json.isEmpty() && !mDataParser.isOnline()) Toast.makeText(mContext, "No data \nConnect to Internet and press refresh", Toast.LENGTH_LONG).show();
  }


  public void listRefresh(){
    List<Meteorite> meteorites = mDataParser.getMeteorites();
    adapter.setList(meteorites);
    mListView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
  }

  public void downloadRefresh(){
    if (mDataParser.isOnline()){
      mDataParser.jsonFromURL();
      progress.show();
    }
  }

}
