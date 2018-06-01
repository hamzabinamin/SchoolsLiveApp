package com.hamzabinamin.schoolsliveapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.widget.PopupMenu;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Hamza on 10/15/2016.
 */

public class CustomAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    Context context;
    public static  List<Game> list = null;
    List<Game> notificationGameList = new ArrayList<Game>();
    ProgressDialog progressDialog;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";

    public CustomAdapter(Context context, List<Game> l) {

        this.context = context;
        this.list = l;
        mInflater = LayoutInflater.from(context);
       // progressDialog = new ProgressDialog(context);
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
    public int getViewTypeCount() { return 2; }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        holder = new ViewHolder();
        if(list.get(position).getSport().equals("Cricket")) {
            if (convertView == null) {
                if(getScreenSize() <= 4) {
                    System.out.println("Got inside <=4");
                    convertView = mInflater.inflate(R.layout.list_view_cricket_item_small, parent, false);
                }
                else {
                    System.out.println("Got inside >4");
                    convertView = mInflater.inflate(R.layout.list_view_cricket_item, parent, false);
                }
                holder.gameType = (TextView)convertView.findViewById(R.id.teamNameTextView);
                holder.homeTeamSchoolName = (TextView) convertView.findViewById(R.id.schoolNameTextView);
                holder.homeTeamSchoolType = (TextView) convertView.findViewById(R.id.schoolTypeTextView);
                holder.homeTeamLogo = (ImageView) convertView.findViewById(R.id.school1Logo);
                holder.awayTeamSchoolName = (TextView) convertView.findViewById(R.id.schoolName2TextView);
                holder.awayTeamSchoolType = (TextView) convertView.findViewById(R.id.schoolType2TextView);
                holder.awayTeamLogo = (ImageView) convertView.findViewById(R.id.school2Logo);
                holder.homeTeamScore = (TextView) convertView.findViewById(R.id.homeScoreTextView);
                holder.awayTeamScore = (TextView) convertView.findViewById(R.id.awayScoreTextView);
                holder.lastUpdateBy = (TextView) convertView.findViewById(R.id.lastupdateTextView);
                holder.time = (TextView) convertView.findViewById(R.id.timeTextView);
                holder.updateGame = (TextView) convertView.findViewById(R.id.updateTextView);
                holder.homeTeamOver = (TextView) convertView.findViewById(R.id.homeOverTextView);
                holder.awayTeamOver = (TextView) convertView.findViewById(R.id.awayOverTextView);
                holder.batbowlTextView = (TextView) convertView.findViewById(R.id.batbowlTextView);
                holder.batbowl2TextView = (TextView) convertView.findViewById(R.id.batbowl2TextView);
                holder.halfTextView = (TextView) convertView.findViewById(R.id.verifiedImageView);
                holder.ageGroupTeamHomeTextView = (TextView) convertView.findViewById(R.id.ageGroupTeamHomeTextView);
                holder.ageGroupTeamAwayTextView = (TextView) convertView.findViewById(R.id.ageGroupTeamAwayTextView);
                holder.addtoNotificationImageView = (ImageView) convertView.findViewById(R.id.addtoNotificationImageView);
                holder.chatImageView = (ImageView) convertView.findViewById(R.id.chatImageView);
                holder.selectRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.selectRelativeLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
        }
        else {
            if (convertView == null) {
                if(getScreenSize() <= 4)
                    convertView = mInflater.inflate(R.layout.list_view_item_small, parent, false);
                else
                    convertView = mInflater.inflate(R.layout.list_view_item, parent, false);
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
                holder.halfTextView = (TextView) convertView.findViewById(R.id.verifiedImageView);
                holder.ageGroupTeamHomeTextView = (TextView) convertView.findViewById(R.id.ageGroupTeamHomeTextView);
                holder.ageGroupTeamAwayTextView = (TextView) convertView.findViewById(R.id.ageGroupTeamAwayTextView);
                holder.addtoNotificationImageView = (ImageView) convertView.findViewById(R.id.addtoNotificationImageView);
                holder.chatImageView = (ImageView) convertView.findViewById(R.id.chatImageView);
                holder.selectRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.selectRelativeLayout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
        }


        if(list.get(position).getStatus().equals("NOT STARTED")) {
            holder.chatImageView.setVisibility(View.INVISIBLE);
        }
        else if(list.get(position).getStatus().equals("FULL TIME")) {
           // holder.updateGame.setVisibility(View.INVISIBLE);
            holder.updateGame.setText("Game Details");
            holder.addtoNotificationImageView.setVisibility(View.INVISIBLE);
        }

        if(list.get(position).getChat().equals("Yes")) {
            holder.chatImageView.setVisibility(View.VISIBLE);
        }
        else {
            holder.chatImageView.setVisibility(View.INVISIBLE);
        }

        if(list.get(position).getAgeGroup().contains("-") && list.get(position).getTeam().contains("-")) {
            Game game = list.get(position);
            String[] ageGroupArray = game.getAgeGroup().split("-");
            String[] teamArray = game.getTeam().split("-");
            holder.ageGroupTeamHomeTextView.setText(ageGroupArray[0] + "/" + teamArray[0]);
            holder.ageGroupTeamAwayTextView.setText(ageGroupArray[1] + "/" + teamArray[1]);
        }
        else {
            Game game = list.get(position);
            holder.ageGroupTeamHomeTextView.setText(game.getAgeGroup() + "/" + game.getTeam());
            holder.ageGroupTeamAwayTextView.setText(game.getAgeGroup() + "/" + game.getTeam());
        }

        if(getGameListSharedPreferences()) {
            if(notificationGameList.contains(list.get(position))) {
                holder.addtoNotificationImageView.setImageResource(R.drawable.alarmclockselected);
            }
        }

        if(!list.get(position).getSport().equals("Cricket")) {
            System.out.println("Not Cricket: " + list.get(position).getSport());
//            holder.currentOver.setVisibility(View.INVISIBLE);
            holder.batbowlTextView.setVisibility(View.INVISIBLE);
            holder.batbowl2TextView.setVisibility(View.INVISIBLE);

            String category = "";
            if(list.get(position).getCategory().trim().equals("Boys")) {
                category = "(B)";
            }
            else if(list.get(position).getCategory().equals("Girls")) {
                category = "(G)";
            }

            holder.gameType.setText(category + " " + list.get(position).getSport());
            holder.homeTeamSchoolName.setText(list.get(position).getHomeSchoolName());
            holder.halfTextView.setText(list.get(position).getStatus());
            String[] splitArray = list.get(position).getSchoolsType().split("/");
            holder.homeTeamSchoolType.setText(splitArray[0]);

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            ImageLoader imageLoader2 = ImageLoader.getInstance();
            imageLoader.displayImage("http" + list.get(position).getHomeSchoolImageURL(), holder.homeTeamLogo, options);
            imageLoader2.displayImage("http" + list.get(position).getAwaySchoolImageURL(), holder.awayTeamLogo, options);
            holder.awayTeamSchoolName.setText(list.get(position).getAwaySchoolName());
            holder.awayTeamSchoolType.setText(splitArray[1]);
            if(list.get(position).getScore() != null) {
                holder.teamScore.setText(list.get(position).getScore());
            }
            holder.lastUpdateBy.setText(list.get(position).getLastUpdateBy());
            holder.time.setText(convertUTCTimeInToLocal(list.get(position).getStartTime()));
        }
        else {
            holder.batbowlTextView.setVisibility(View.VISIBLE);
            holder.batbowl2TextView.setVisibility(View.VISIBLE);

            String category = "";
            if(list.get(position).getCategory().trim().equals("Boys")) {
                category = "(B)";
            }
            else if(list.get(position).getCategory().trim().equals("Girls")) {
                category = "(G)";
            }

            holder.gameType.setText(category + " " + list.get(position).getSport());
            holder.homeTeamSchoolName.setText(list.get(position).getHomeSchoolName());
            String[] splitArray = list.get(position).getSchoolsType().split("/");
            holder.homeTeamSchoolType.setText(splitArray[0]);
            holder.awayTeamSchoolName.setText(list.get(position).getAwaySchoolName());
            holder.awayTeamSchoolType.setText(splitArray[1]);
            holder.lastUpdateBy.setText(list.get(position).getLastUpdateBy());
            holder.time.setText(convertUTCTimeInToLocal(list.get(position).getStartTime()));
            holder.halfTextView.setText(list.get(position).getStatus());
            String[] stringArray = list.get(position).getScore().split("/");
            if(list.get(position).getScore().contains("*") && list.get(position).getSport().equals("Cricket")) {
                Game game = list.get(position);
                String homeScore = game.getScore().split("\\*")[0];
                String awayScore = game.getScore().split("\\*")[1];
                String homeScore2[] = homeScore.split("/");
                String awayScore2[] = awayScore.split("/");

                System.out.println("Home Score: " + homeScore);
                System.out.println("Away Score: " + awayScore);

                if(holder.homeTeamOver != null) {
                    holder.homeTeamOver.setText("Over: " + homeScore2[4]);
                    holder.homeTeamScore.setText(homeScore2[0].trim() + "/" + homeScore2[1].trim());
                    holder.awayTeamOver.setText("Over: " + awayScore2[4]);
                    holder.awayTeamScore.setText(awayScore2[0].trim() + "/" + awayScore2[1].trim());
                }
                if (list.get(position).getHomeSchoolName().equals(homeScore2[2])) {
                    holder.batbowl2TextView.setText("(Bat)");
                    holder.batbowlTextView.setText("(Bowl)");
                } else {
                    holder.batbowl2TextView.setText("(Bowl)");
                    holder.batbowlTextView.setText("(Bat)");
                }

                if(list.get(position).getStatus().equals("FULL TIME") && list.get(position).getSport().equals("Cricket")) {
                    String store = list.get(position).getWhoWon().trim();
                    holder.batbowlTextView.setVisibility(View.VISIBLE);
                    holder.batbowl2TextView.setVisibility(View.VISIBLE);

                    if(store.equals(holder.homeTeamSchoolName.getText().toString().trim())) {
                        System.out.println("Got in If");
                        holder.batbowl2TextView.setText("WON");
                        holder.batbowlTextView.setText("LOST");
                    }
                    else if(store.equals(holder.awayTeamSchoolName.getText().toString().trim())) {
                        System.out.println("Got in Else");
                        holder.batbowlTextView.setText("WON");
                        holder.batbowl2TextView.setText("LOST");
                    }
                    else if(store.equals("Draw")) {
                        holder.batbowlTextView.setText("Draw");
                        holder.batbowl2TextView.setText("Draw");
                    }
                    else if(store.equals("Tie")) {
                        holder.batbowlTextView.setText("Tie");
                        holder.batbowl2TextView.setText("Tie");
                    }
                }
                else {
                    holder.batbowlTextView.setVisibility(View.INVISIBLE);
                    holder.batbowl2TextView.setVisibility(View.INVISIBLE);

                }
            }
            else {
                holder.homeTeamScore.setText("0/0");
                holder.awayTeamScore.setText("0/0");
                holder.batbowlTextView.setVisibility(View.INVISIBLE);
                holder.batbowl2TextView.setVisibility(View.INVISIBLE);
                holder.homeTeamOver.setText("Over: 0");
                holder.awayTeamOver.setText("Over: 0");

                if(list.get(position).getStatus().equals("FULL TIME") && list.get(position).getSport().equals("Cricket")) {
                    String store = list.get(position).getWhoWon().trim();
                    holder.batbowlTextView.setVisibility(View.VISIBLE);
                    holder.batbowl2TextView.setVisibility(View.VISIBLE);

                    if(store.equals(holder.homeTeamSchoolName.getText().toString().trim())) {
                        System.out.println("Got in If");
                        holder.batbowl2TextView.setText("WON");
                        holder.batbowlTextView.setText("LOST");
                    }
                    else if(store.equals(holder.awayTeamSchoolName.getText().toString().trim())) {
                        System.out.println("Got in Else");
                        holder.batbowlTextView.setText("WON");
                        holder.batbowl2TextView.setText("LOST");
                    }
                    else if(store.equals("Draw")) {
                        holder.batbowlTextView.setText("Draw");
                        holder.batbowl2TextView.setText("Draw");
                    }
                    else if(store.equals("Tie")) {
                        holder.batbowlTextView.setText("Tie");
                        holder.batbowl2TextView.setText("Tie");
                    }
                }
                else {
                    holder.batbowlTextView.setVisibility(View.INVISIBLE);
                    holder.batbowl2TextView.setVisibility(View.INVISIBLE);

                }
            }

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            ImageLoader imageLoader2 = ImageLoader.getInstance();
            imageLoader2.init(ImageLoaderConfiguration.createDefault(context));
            imageLoader.displayImage("http" + list.get(position).getHomeSchoolImageURL(), holder.homeTeamLogo, options);
            imageLoader2.displayImage("http" + list.get(position).getAwaySchoolImageURL(), holder.awayTeamLogo, options);
        }
        holder.updateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Game game = list.get(position);
                saveGameSharedPreferences(game);
                if(!game.getSport().equals("Cricket")) {
                    Intent intent = new Intent(context, UpdateGameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else {
                    Intent intent = new Intent(context, UpdateCricketGameActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    System.out.println("Opening Cricket Update");
                    context.startActivity(intent);
                }

            }
        });

        final ViewHolder finalHolder = holder;
        holder.addtoNotificationImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Game game = list.get(position);

                if (getGameListSharedPreferences()) {
                    if(notificationGameList.indexOf(game) != -1) {
                        finalHolder.addtoNotificationImageView.setImageResource(R.drawable.alarmclock);
                        int index = notificationGameList.indexOf(game);
                        notificationGameList.remove(index);
                        saveGameListSharedPreferences(notificationGameList);
                        Toast.makeText(context, "Game Removed from Notifications", Toast.LENGTH_SHORT).show();

                        for(int i=0; i<notificationGameList.size(); i++) {
                            System.out.println("Game ID: " + notificationGameList.get(i).getGameID());
                            System.out.println("Game Type: " + notificationGameList.get(i).getSport());
                            System.out.println("Game Score: " + notificationGameList.get(i).getScore());
                        }
                    }
                    else {
                        finalHolder.addtoNotificationImageView.setImageResource(R.drawable.alarmclockselected);
                        game.setSelectedForNotification(true);
                        notificationGameList.add(game);
                        saveGameListSharedPreferences(notificationGameList);
                        Toast.makeText(context, "Game Added to Notifications", Toast.LENGTH_SHORT).show();

                        for(int i=0; i<notificationGameList.size(); i++) {
                            System.out.println("Game ID: " + notificationGameList.get(i).getGameID());
                            System.out.println("Game Type: " + notificationGameList.get(i).getSport());
                            System.out.println("Game Score: " + notificationGameList.get(i).getScore());
                        }
                    }
                } else {
                    finalHolder.addtoNotificationImageView.setImageResource(R.drawable.alarmclockselected);
                    game.setSelectedForNotification(true);
                    notificationGameList.add(game);
                    saveGameListSharedPreferences(notificationGameList);
                    Toast.makeText(context, "Game Added to Notifications", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final View finalConvertView = convertView;
        final ViewHolder finalHolder1 = holder;
        holder.selectRelativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               PopupMenu popup = new PopupMenu(finalConvertView.getContext(), finalHolder1.time);
               popup.getMenuInflater()
                       .inflate(R.menu.deletegame, popup.getMenu());

               popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                   public boolean onMenuItemClick(MenuItem item) {
                       if(item.getTitle().toString().equals("DELETE")) {
                           Game game = list.get(position);
                           progressDialog = new ProgressDialog(finalConvertView.getContext());
                           progressDialog.setMessage("Please Wait");
                           progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                           progressDialog.setIndeterminate(true);
                           progressDialog.setCancelable(false);
                           progressDialog.setCanceledOnTouchOutside(false);
                           progressDialog.show();

                           String url = String.format("http://schools-live.com/deleteGame.php?id=%s", game.getGameID());
                           try {
                               sendGETDeleteGame(url, position);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }

                       }


                       return true;
                   }
               });

               popup.show();

               return false;
           }
       });
        return convertView;
   }

    public String convertUTCTimeInToLocal(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("d-M-yyyy / hh:mm a");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        String formattedDate = null;
        try {
            date = df.parse(dateString);
            df.setTimeZone(TimeZone.getDefault());
            formattedDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
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
        TextView homeTeamScore;
        TextView awayTeamScore;
        TextView lastUpdateBy;
        TextView time;
        TextView updateGame;
        TextView currentOver;
        TextView homeTeamOver;
        TextView awayTeamOver;
        TextView batbowlTextView;
        TextView batbowl2TextView;
        TextView halfTextView;
        TextView ageGroupTeamHomeTextView;
        TextView ageGroupTeamAwayTextView;
        ImageView addtoNotificationImageView;
        ImageView chatImageView;
        RelativeLayout selectRelativeLayout;
    }

    public void saveGameSharedPreferences(Game game) {
        Gson gson = new Gson();
        String runnerString = gson.toJson(game);
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Game", runnerString).commit();
    }

    public void saveGameListSharedPreferences(List counterList) {
        Gson gson = new Gson();
        String gameString = gson.toJson(counterList);
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Notification Game List", gameString).commit();
    }

    public boolean getGameListSharedPreferences() {

        SharedPreferences sharedPreferences = context.getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        String gameString;
        if (sharedPreferences.getString("Notification Game List", null) != null) {
            gameString = sharedPreferences.getString("Notification Game List", null);
            Gson gson = new Gson();
            TypeToken<List<Game>> token = new TypeToken<List<Game>>() {};
            notificationGameList = gson.fromJson(gameString, token.getType());
            return true;
        }
        else
            return false;
    }

    public void sendGETDeleteGame(String paramURL, final int position) throws IOException {

        class HttpGetAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                String loginStatus = "";
                try {
                    String paramURL = strings[0];
                    System.out.println("paramURL: " +paramURL );
                    URL url = new URL(paramURL);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("User-Agent", USER_AGENT);
                    if(urlConnection.getResponseCode() < 400) {
                        InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                        StringBuilder builder = new StringBuilder();

                        String inputString;
                        while ((inputString = bufferedReader.readLine()) != null) {
                            builder.append(inputString);
                        }

                        android.util.Log.e("Response 1: ", builder.toString());
                        loginStatus = builder.toString();
                        urlConnection.disconnect();

                        return loginStatus;
                    }
                    else {
                        System.out.println("Response Code: " + urlConnection.getResponseMessage());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if(result != null) {
                    System.out.println(result);
                    if(result.contains("Game deleted successfully")) {
                        progressDialog.dismiss();
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Game deleted successfully", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(context, "Couldn't delete Game", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
    }


}
