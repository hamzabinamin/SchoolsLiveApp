package com.hamzabinamin.schoolsliveapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Hamza on 10/15/2016.
 */

public class CustomAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    Context context;
    public static  List<Game> list = null;

    public CustomAdapter(Context context, List<Game> l) {

        this.context = context;
        this.list = l;
        mInflater = LayoutInflater.from(context);
    }

    public void add(Game game) {
        list.add(game);
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
                convertView = mInflater.inflate(R.layout.list_view_item, parent, false);
            else
                convertView = mInflater.inflate(R.layout.list_view_item, parent, false);
            holder = new ViewHolder();
            holder.gameType = (TextView)convertView.findViewById(R.id.teamNameTextView);
            holder.homeTeamSchoolName = (TextView) convertView.findViewById(R.id.schoolNameTextView);
            holder.homeTeamSchoolType = (TextView) convertView.findViewById(R.id.schoolTypeTextView);
            holder.homeTeamLogo = (ImageView) convertView.findViewById(R.id.school1Logo);
            holder.awayTeamSchoolName = (TextView) convertView.findViewById(R.id.schoolName2TextView);
            holder.awayTeamSchoolType = (TextView) convertView.findViewById(R.id.schoolType2TextView);
            holder.awayTeamLogo = (ImageView) convertView.findViewById(R.id.school2Logo);
            holder.teamScore = (TextView) convertView.findViewById(R.id.scoreTextView);
            holder.lastUpdateBy = (TextView) convertView.findViewById(R.id.lastupdateTextView);
            holder.time = (TextView) convertView.findViewById(R.id.timeTextView);
            holder.updateGame = (TextView) convertView.findViewById(R.id.updateTextView);
            holder.currentOver = (TextView) convertView.findViewById(R.id.currentOverTextView);
            holder.batbowlTextView = (TextView) convertView.findViewById(R.id.batbowlTextView);
            holder.batbowl2TextView = (TextView) convertView.findViewById(R.id.batbowl2TextView);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        if(!list.get(position).getSport().equals("Cricket")) {
            holder.currentOver.setVisibility(View.INVISIBLE);
            holder.batbowlTextView.setVisibility(View.INVISIBLE);
            holder.batbowl2TextView.setVisibility(View.INVISIBLE);
            holder.gameType.setText(list.get(position).getSport() + " " + list.get(position).getAgeGroup() + "/" + list.get(position).getTeam());
            holder.homeTeamSchoolName.setText(list.get(position).getHomeSchoolName());
            String[] splitArray = list.get(position).getSchoolsType().split("/");
            holder.homeTeamSchoolType.setText(splitArray[0]);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(android.R.drawable.arrow_down_float)
                    .showImageOnFail(android.R.drawable.ic_menu_report_image)
                    .showImageOnLoading(android.R.drawable.arrow_up_float).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            ImageLoader imageLoader2 = ImageLoader.getInstance();
            imageLoader.displayImage("http" + list.get(position).getHomeSchoolImageURL(), holder.homeTeamLogo, options);
            imageLoader2.displayImage("http" + list.get(position).getAwaySchoolImageURL(), holder.awayTeamLogo, options);
            holder.awayTeamSchoolName.setText(list.get(position).getAwaySchoolName());
            holder.awayTeamSchoolType.setText(splitArray[1]);
            holder.teamScore.setText(list.get(position).getScore());
            holder.lastUpdateBy.setText(list.get(position).getLastUpdateBy());
            holder.time.setText(list.get(position).getStartTime());
        }
        else {
            holder.currentOver.setVisibility(View.VISIBLE);
            holder.batbowlTextView.setVisibility(View.VISIBLE);
            holder.batbowl2TextView.setVisibility(View.VISIBLE);
            holder.gameType.setText(list.get(position).getSport() + " " + list.get(position).getAgeGroup() + "/" + list.get(position).getTeam());
            holder.homeTeamSchoolName.setText(list.get(position).getHomeSchoolName());
            String[] splitArray = list.get(position).getSchoolsType().split("/");
            holder.homeTeamSchoolType.setText(splitArray[0]);
            holder.awayTeamSchoolName.setText(list.get(position).getAwaySchoolName());
            holder.awayTeamSchoolType.setText(splitArray[1]);
            holder.teamScore.setText(list.get(position).getScore());
            holder.lastUpdateBy.setText(list.get(position).getLastUpdateBy());
            holder.time.setText(list.get(position).getStartTime());
            String[] stringArray = list.get(position).getScore().split("/");
            holder.currentOver.setText(stringArray[4]);
            if(list.get(position).getHomeSchoolName().equals(stringArray[2])) {
                holder.batbowlTextView.setText("Batting");
                holder.batbowl2TextView.setText("Bowling");
            }
            else {
                holder.batbowlTextView.setText("Bowling");
                holder.batbowl2TextView.setText("Batting");
            }
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(android.R.drawable.arrow_down_float)
                    .showImageOnFail(android.R.drawable.ic_menu_report_image)
                    .showImageOnLoading(android.R.drawable.arrow_up_float).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            ImageLoader imageLoader2 = ImageLoader.getInstance();
            imageLoader.displayImage("http" + list.get(position).getHomeSchoolImageURL(), holder.homeTeamLogo, options);
            imageLoader2.displayImage("http" + list.get(position).getAwaySchoolImageURL(), holder.awayTeamLogo, options);
        }
        holder.updateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game game = list.get(position);
                saveGameSharedPreferences(game);
                context.startActivity(new Intent(context, UpdateGameActivity.class));
            }
        });

        return convertView;


    }


    private class ViewHolder {
        TextView gameType;
        TextView homeTeamSchoolName;
        TextView homeTeamSchoolType;
        ImageView homeTeamLogo;
        TextView awayTeamSchoolName;
        TextView awayTeamSchoolType;
        ImageView awayTeamLogo;
        TextView teamScore;
        TextView lastUpdateBy;
        TextView time;
        TextView updateGame;
        TextView currentOver;
        TextView batbowlTextView;
        TextView batbowl2TextView;
    }

    public void saveGameSharedPreferences(Game game) {
        Gson gson = new Gson();
        String runnerString = gson.toJson(game);
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Game", runnerString).commit();
    }
}
