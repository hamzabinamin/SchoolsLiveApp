package com.hamzabinamin.schoolsliveapp;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Hamza on 10/15/2016.
 */

public class CustomAdapterRanking extends BaseAdapter {

    private final LayoutInflater mInflater;
    Context context;
    public static  List<Rank> list = null;

    public CustomAdapterRanking(Context context, List<Rank> l) {

        this.context = context;
        this.list = l;
        mInflater = LayoutInflater.from(context);
    }

    public void add(Rank rank) {
        list.add(rank);
        notifyDataSetChanged();
    }



    public double getScreenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);

        return screenInches;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            if(getScreenSize() <= 4)
                convertView = mInflater.inflate(R.layout.list_view_item_ranking_small, parent, false);
            else
                convertView = mInflater.inflate(R.layout.list_view_item_ranking, parent, false);
            holder = new ViewHolder();
            holder.rankID = (TextView)convertView.findViewById(R.id.rankTextView);
            holder.name = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.date = (TextView) convertView.findViewById(R.id.dateTextView);
            holder.totalUpdates = (TextView) convertView.findViewById(R.id.totalUpdatesTextView);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }
        holder.rankID.setText("" + (position + 1));
        holder.name.setText(list.get(position).getName());
        holder.date.setText(list.get(position).getDate().replace("/", "\n"));
        holder.totalUpdates.setText("" + list.get(position).getTotalUpdates());


        return convertView;


    }


    private class ViewHolder {
        TextView rankID;
        TextView name;
        TextView date;
        TextView totalUpdates;
    }



}
