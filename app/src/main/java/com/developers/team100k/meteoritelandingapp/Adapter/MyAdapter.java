package com.developers.team100k.meteoritelandingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.developers.team100k.meteoritelandingapp.Meteorite;
import com.developers.team100k.meteoritelandingapp.R;
import java.util.List;

/**
 * Created by Richard Hrmo.
 */

public class MyAdapter extends BaseAdapter {

  public Context context;
  public List<Meteorite> mList;

  public MyAdapter(Context context, List<Meteorite> list){
    this.context = context;
    this.mList = list;
  }

  @Override
  public int getCount() {
    return mList.size();
  }

  @Override
  public Meteorite getItem(int position) {
    return mList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.listview_layout, parent, false);
    }

    Meteorite meteorite = getItem(position);
    TextView name = (TextView) convertView.findViewById(R.id.item_name);
    name.setText(meteorite.getName());

    TextView mass = (TextView) convertView.findViewById(R.id.item_mass);
    mass.setText(meteorite.getMass() + "g");

    TextView year = (TextView) convertView.findViewById(R.id.item_year);
    String format = meteorite.getYear().substring(0,4);
    year.setText(format);

    return convertView;
  }

  public void setList(List<Meteorite> list) {
    mList = list;
  }

}
