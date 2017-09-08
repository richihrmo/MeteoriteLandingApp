package com.developers.team100k.meteoritelandingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.developers.team100k.meteoritelandingapp.Adapter.MyAdapter;
import com.developers.team100k.meteoritelandingapp.Entity.Meteorite;
import com.developers.team100k.meteoritelandingapp.JobServices.MyJobService;
import com.evernote.android.job.JobManager;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {

  private static String URL = "https://data.nasa.gov/resource/y77d-th95.json?$where=year%20%3E%20%272011-01-01T12:00:00%27&$$app_token=VMuBlcIIY8sM83yXAD2j4KXQV";
  private List<Meteorite> meteorites = new ArrayList<>();
  private ListView mListView;
  private MyAdapter adapter;
  private ProgressDialog progress;

  private EventBus eventBus = EventBus.getDefault();
  private DataHandler dataHandler;
  private FirebaseJobDispatcher dispatcher;


  @Override
  protected void onStart() {
    super.onStart();
    eventBus = EventBus.getDefault();
    eventBus.register(this);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fabric.with(this, new Crashlytics());
    setContentView(R.layout.activity_main);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle("Meteorite Landings");
    setSupportActionBar(toolbar);

    progress = new ProgressDialog(this);
    progress.setTitle("Loading data");
    progress.setMessage("Please wait...");
    progress.setCancelable(false);

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

    dataHandler = new DataHandler(this, URL, adapter, mListView, progress);
    meteorites = dataHandler.getDataParser().getMeteorites();

    // job scheduler
    dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(MainActivity.this));
    Job job = dispatcher.newJobBuilder()
        .setService(MyJobService.class)
        .setTag("random_tag")
        .setReplaceCurrent(false)
        .setRecurring(true)
        .setTrigger(Trigger.executionWindow(86400000,86400010))
        .build();
    dispatcher.mustSchedule(job);
  }

  // eventbus onEvent listener
  @Subscribe
  public void onEvent(String response){
    if (response.equals("download")){
      dataHandler.downloadRefresh();
    } else {
      if (response.equals("refresh")){
        dataHandler.listRefresh();
        progress.dismiss();
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.item, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_refresh){
      dataHandler.downloadRefresh();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onStop() {
    super.onStop();
    eventBus.unregister(this);
    dispatcher.cancelAll();
  }
}
