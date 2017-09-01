package com.developers.team100k.meteoritelandingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ListView;
import android.widget.Toast;
import com.developers.team100k.meteoritelandingapp.Adapter.MyAdapter;
import com.developers.team100k.meteoritelandingapp.Entity.Meteorite;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Richard Hrmo.
 */

public class DataHandler {

  private Context mContext;
  private String URL;
  private EventBus mEventBus;
  private String json;
  private static String filename = "tempData";
  private List<Meteorite> meteorites = new ArrayList<>();
  private MyAdapter adapter;
  private ListView mListView;
  private ProgressDialog progress;

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
    mEventBus = EventBus.getDefault();
    mDataParser = new DataParser(mContext, URL);

    json = mDataParser.readFile(mContext, filename);
    if (!json.isEmpty()) mDataParser.jsonToCollection(json);
    if (mDataParser.isOnline()) {
      mDataParser.jsonFromURL();
      progress.show();
    }
    if (json.isEmpty() && !mDataParser.isOnline()) Toast.makeText(mContext, "No data \nConnect to Internet and press refresh", Toast.LENGTH_LONG).show();
  }


  public void refresh(){
    meteorites = mDataParser.getMeteorites();
    adapter.setList(meteorites);
    mListView.setAdapter(adapter);
    adapter.notifyDataSetChanged();
  }

}