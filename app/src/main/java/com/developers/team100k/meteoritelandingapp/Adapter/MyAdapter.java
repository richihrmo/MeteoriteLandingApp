package com.developers.team100k.meteoritelandingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.developers.team100k.meteoritelandingapp.Meteorite;
import com.developers.team100k.meteoritelandingapp.R;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

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
    ViewHolderItem viewHolder;

    if (convertView == null) {
      convertView = LayoutInflater.from(context).inflate(R.layout.listview_layout, parent, false);

      viewHolder = new ViewHolderItem();
      viewHolder.name = (TextView) convertView.findViewById(R.id.item_name);
      viewHolder.mass = (TextView) convertView.findViewById(R.id.item_mass);
      viewHolder.year = (TextView) convertView.findViewById(R.id.item_year);

      convertView.setTag(viewHolder);
    } else {
      viewHolder = (ViewHolderItem) convertView.getTag();
    }

    Meteorite meteorite = getItem(position);
    if (meteorite != null){
      viewHolder.name.setText(meteorite.getName());
      viewHolder.mass.setText(meteorite.getMass() + "g");
      viewHolder.year.setText(meteorite.getYear().substring(0,4));
    }

    return convertView;
  }

  public void setList(List<Meteorite> list) {
    mList = list;
  }

}
