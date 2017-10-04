package com.hamzabinamin.schoolsliveapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Hamza on 10/15/2016.
 */

public class CustomAdapterChat extends BaseAdapter {

    private final LayoutInflater mInflater;
    Context context;
    public static  List<Chat> list = null;

    public CustomAdapterChat(Context context, List<Chat> l) {

        this.context = context;
        this.list = l;
        mInflater = LayoutInflater.from(context);
    }

    public void add(Chat chat) {
        list.add(chat);
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
                convertView = mInflater.inflate(R.layout.chat_list_view_item_small, parent, false);
            else
                convertView = mInflater.inflate(R.layout.chat_list_view_item, parent, false);
            holder = new ViewHolder();
            holder.messageTextView = (TextView)convertView.findViewById(R.id.messageTextView);
            holder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
            holder.timeAndDateTextView = (TextView) convertView.findViewById(R.id.timeAndDateTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.nameTextView.setText(list.get(position).getName());
        holder.messageTextView.setText(list.get(position).getMessage());
        holder.timeAndDateTextView.setText(list.get(position).getTime());

        return convertView;
    }


    private class ViewHolder {
        TextView messageTextView;
        TextView nameTextView;
        TextView timeAndDateTextView;
    }

    public void saveGameSharedPreferences(Game game) {
        Gson gson = new Gson();
        String runnerString = gson.toJson(game);
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Game", runnerString).commit();
    }
}
