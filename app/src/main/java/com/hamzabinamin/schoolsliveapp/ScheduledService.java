package com.hamzabinamin.schoolsliveapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.R.id.message;

public class ScheduledService extends Service {

    School school;
    List<Game> notificationGameList = new ArrayList<Game>();
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Scheduled Service","service");

        if(getSchoolSharedPreferences()) {
            try {
                System.out.println("Getting Fixture Games");
                String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String url = String.format("http://schools-live.com/getAllFixturesGames.php?home=%s&away=%s", schoolName, schoolName);
                sendGETforGames(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        System.out.println("Getting Live Games");
                        String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                        String url = String.format("http://schools-live.com/getAllLiveGames.php?home=%s&away=%s", schoolName, schoolName);
                        sendGETforLiveGames(url);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();

            Thread thread2 = new Thread(new Runnable(){
                @Override
                public void run(){
                    try {
                        System.out.println("Checking Games for Score Notifications");
                        if(getGameListSharedPreferences()) {
                            for(int i=0; i<notificationGameList.size(); i++) {
                                Game game = notificationGameList.get(i);
                                String url = String.format("http://schools-live.com/getGame.php?id=%s", game.getGameID());
                                sendGETforScoreUpdates(url, game);
                            }
                        }
                        else {
                            System.out.println("No Game under Notifications");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread2.start();
        }
        else {
            System.out.println("No School, Service didn't start");
        }


        return START_STICKY;
    }

    public boolean getSchoolSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("School String", null) != null) {
            String serviceProviderString = sharedPreferences.getString("School String", null);
            Gson gson = new Gson();
            TypeToken<School> token = new TypeToken<School>() {};
            school = gson.fromJson(serviceProviderString, token.getType());
            return true;
        }
        else
            return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public SimpleDateFormat getLocalTime() {
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy / h:mm a");
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy / hh:mm a");
        Calendar calendar = Calendar.getInstance();
        //sdf = sdf.format(calendar.getTime());
        return sdf;
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, LiveNowActivity.class);
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        //notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, m);
        //notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, m, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentIntent = pendingIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(m, notification);

   /*     long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent); */
    }

    public void showNotificationLive(String title, String message) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        int mNotificationId = m;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_more)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOngoing(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LiveNowActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void showNotificationEnded(String title, String message) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        int mNotificationId = m;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_more)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOngoing(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, ResultsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    public void showNotificationScoreUpdate(String title, String message) {
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        int mNotificationId = m;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(android.R.drawable.stat_notify_more)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setOngoing(true);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LiveNowActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Live Game Right Now!");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return builder.build();
    }

    private Notification getNotification2(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Game Ended!");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return builder.build();
    }

    private Notification getNotification3(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Score Update!");
        builder.setContentText(content);
        builder.setSmallIcon(android.R.drawable.stat_notify_more);
        builder.setDefaults(Notification.DEFAULT_ALL);
        return builder.build();
    }

    public void cancelAlarm() {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, ScheduledService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        am.cancel(pi);
    }

    public void sendGETforGames(String paramURL) throws IOException {

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
                    if(result.contains("Got Result")) {
                        result = result.replace("Got Result<br>","");
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<Game> gameList = new ArrayList<Game>();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                String gameID = arr.getJSONObject(i).getString("Game_ID");
                                String homeSchoolName = arr.getJSONObject(i).getString("Home_School_Name");
                                String awaySchoolName = arr.getJSONObject(i).getString("Away_School_Name");
                                String schoolsType = arr.getJSONObject(i).getString("Schools_Type");
                                String field = arr.getJSONObject(i).getString("Field");
                                String sport = arr.getJSONObject(i).getString("Sport");
                                String ageGroup = arr.getJSONObject(i).getString("Age_Group");
                                String team = arr.getJSONObject(i).getString("Team");
                                String startTime = arr.getJSONObject(i).getString("Start_Time");
                                startTime = convertUTCTimeInToLocal(startTime);
                                String weather = arr.getJSONObject(i).getString("Weather");
                                String temperature = arr.getJSONObject(i).getString("Temperature");
                                String status = arr.getJSONObject(i).getString("Status");
                                String score = arr.getJSONObject(i).getString("Score");
                                String lastUpdateBy = arr.getJSONObject(i).getString("Last_Update_By");
                                String lastUpdateTime = arr.getJSONObject(i).getString("Last_Update_Time");
                                String homeSchoolURL = arr.getJSONObject(i).getString("Home_School_Logo");
                                String awaySchoolURL = arr.getJSONObject(i).getString("Away_School_Logo");
                                String won = arr.getJSONObject(i).getString("Won");
                                gameList.add(new Game(gameID, homeSchoolName, awaySchoolName, schoolsType, field, sport, ageGroup, team, startTime, weather, temperature, status, score, lastUpdateBy, lastUpdateTime, homeSchoolURL, awaySchoolURL, won));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                                if(gameList.size() > 0) {
                                    for (int j = 0; j < gameList.size(); j++) {
                                        Game game = gameList.get(j);
                                        String gameid = game.getGameID();
                                        String gamedate = game.getStartTime();
                                        //gamedate = convertUTCTimeInToLocal(gamedate);
                                        Calendar calendar = Calendar.getInstance();
                                        String currentTime = getLocalTime().format(calendar.getTime());
                                        //calendar = getLocalTime().getCalendar();
                                        try {
                                            calendar.setTime(getLocalTime().parse(currentTime));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Calendar gameCalendar = Calendar.getInstance();
                                        try {
                                            gameCalendar.setTime(getLocalTime().parse(gamedate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("Game Calendar: " + gameCalendar.getTime());
                                        System.out.println("Current Calendar: " + calendar.getTime());

                                        System.out.println("Game Calendar Date: " + gameCalendar.get(Calendar.DATE));
                                        System.out.println("Current Calendar Date: " + calendar.get(Calendar.DATE));

                                        System.out.println("Game Time: " + gamedate);
                                        System.out.println("Current Time: " + currentTime);



                                            if (gamedate.equals(currentTime) || (calendar.get(Calendar.HOUR) == gameCalendar.get(Calendar.HOUR) && calendar.get(Calendar.AM_PM) == gameCalendar.get(Calendar.AM_PM) && ((calendar.get(Calendar.MINUTE) - gameCalendar.get(Calendar.MINUTE) <= 10) && (calendar.get(Calendar.MINUTE) - gameCalendar.get(Calendar.MINUTE) >= 0)) && calendar.get(Calendar.DATE) == gameCalendar.get(Calendar.DATE) && calendar.get(Calendar.MONTH) == gameCalendar.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == gameCalendar.get(Calendar.YEAR))) {
                                                if(getNotificationSharedPreferences() != null && getNotificationSharedPreferences().equals("Checked")) {
                                                    if(!game.getSport().equals("Cricket")) {
                                                        String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("-")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("-")[1];
                                                        //scheduleNotification(getNotification(text), 0);
                                                        showNotificationLive("Live Game Right Now!", text);
                                                    }
                                                    else {
                                                        if(game.getScore().split("/").length == 5) {
                                                            String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getScore().split("/")[2] + " " + "(Bat)" + "-" + " " + game.getScore().split("/")[3] + " " + "(Bowl)" + "-" + " " + game.getScore().split("/")[0].trim() + "/" + game.getScore().split("/")[1].trim() + " " +  " " + "Overs: " + game.getScore().split("/")[4];
                                                            //scheduleNotification(getNotification(text), 0);
                                                            showNotificationLive("Live Game Right Now!", text);
                                                        }
                                                        else {
                                                            String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("/")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("/")[1];
                                                            //scheduleNotification(getNotification(text), 0);
                                                            showNotificationLive("Live Game Right Now!", text);
                                                        }
                                                    }
                                                }
                                                String status = null;
                                                try {
                                                    status = URLEncoder.encode("1st HALF", "UTF-8");
                                                    String url = String.format("http://www.schools-live.com/updateGameStatusService.php?id=%s&status=%s", gameid, status);
                                                    sendGETUpdateGame(url);
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        else {
                                            System.out.println("Notifications are off");
                                        }
                                    }
                                }
                                else {
                                    stopSelf();
                              //      cancelAlarm();
                                }

                        }
                    }
                    else {
                        stopSelf();
                    //    cancelAlarm();
                    }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
    }

    public void sendGETUpdateGame(String paramURL) throws IOException {

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
                    if(result.contains("Record updated successfully")) {

                    }
                    else {
                   //     Toast.makeText(getBaseContext(), "There was an Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
    }

    public void sendGETforLiveGames(String paramURL) throws IOException {

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
                    if(result.contains("Got Result")) {
                        result = result.replace("Got Result<br>","");
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<Game> gameList = new ArrayList<Game>();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                String gameID = arr.getJSONObject(i).getString("Game_ID");
                                String homeSchoolName = arr.getJSONObject(i).getString("Home_School_Name");
                                String awaySchoolName = arr.getJSONObject(i).getString("Away_School_Name");
                                String schoolsType = arr.getJSONObject(i).getString("Schools_Type");
                                String field = arr.getJSONObject(i).getString("Field");
                                String sport = arr.getJSONObject(i).getString("Sport");
                                String ageGroup = arr.getJSONObject(i).getString("Age_Group");
                                String team = arr.getJSONObject(i).getString("Team");
                                String startTime = arr.getJSONObject(i).getString("Start_Time");
                                startTime = convertUTCTimeInToLocal(startTime);
                                String weather = arr.getJSONObject(i).getString("Weather");
                                String temperature = arr.getJSONObject(i).getString("Temperature");
                                String status = arr.getJSONObject(i).getString("Status");
                                String score = arr.getJSONObject(i).getString("Score");
                                String lastUpdateBy = arr.getJSONObject(i).getString("Last_Update_By");
                                String lastUpdateTime = arr.getJSONObject(i).getString("Last_Update_Time");
                                String homeSchoolURL = arr.getJSONObject(i).getString("Home_School_Logo");
                                String awaySchoolURL = arr.getJSONObject(i).getString("Away_School_Logo");
                                String won = arr.getJSONObject(i).getString("Won");
                                gameList.add(new Game(gameID, homeSchoolName, awaySchoolName, schoolsType, field, sport, ageGroup, team, startTime, weather, temperature, status, score, lastUpdateBy, lastUpdateTime, homeSchoolURL, awaySchoolURL, won));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                               if(gameList.size() > 0) {
                                    for (int j = 0; j < gameList.size(); j++) {
                                        Game game = gameList.get(j);
                                        String gameid = game.getGameID();
                                        String gamedate = game.getStartTime();
                                        //gamedate = convertUTCTimeInToLocal(gamedate);
                                        Calendar calendar = Calendar.getInstance();
                                        String currentTime = getLocalTime().format(calendar.getTime());
                                        //calendar = getLocalTime().getCalendar();
                                        try {
                                            calendar.setTime(getLocalTime().parse(currentTime));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Calendar gameCalendar = Calendar.getInstance();
                                        try {
                                            gameCalendar.setTime(getLocalTime().parse(gamedate));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        System.out.println("Game Calendar: " + gameCalendar.getTime());
                                        System.out.println("Current Calendar: " + calendar.getTime());

                                        System.out.println("Game Calendar Date: " + gameCalendar.get(Calendar.DATE));
                                        System.out.println("Current Calendar Date: " + calendar.get(Calendar.DATE));

                                        System.out.println("Game Time: " + gamedate);
                                        System.out.println("Current Time: " + currentTime);

                                            if(game.getSport().equals("Cricket") && calendar.getTimeInMillis() - gameCalendar.getTimeInMillis() >  TimeUnit.HOURS.toMillis(5)) {
                                                if(getNotificationSharedPreferences() != null && getNotificationSharedPreferences().equals("Checked")) {
                                                    if(game.getScore().split("/").length == 5) {
                                                        String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getScore().split("/")[2] + " " + "(Bat)" + "-" + " " + game.getScore().split("/")[3] + " " + "(Bowl)" + "-" + " " + game.getScore().split("/")[0].trim() + "/" + game.getScore().split("/")[1].trim() + " " +  " " + "Overs: " + game.getScore().split("/")[4];
                                                        //scheduleNotification(getNotification2(text), 0);
                                                        showNotificationEnded("Game Ended!", text);
                                                    }
                                                    else {
                                                        String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("/")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("/")[1];
                                                        //scheduleNotification(getNotification2(text), 0);
                                                        showNotificationEnded("Game Ended!", text);
                                                    }

                                                 //   String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("-")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("-")[1];
                                               //     scheduleNotification(getNotification2(text), 0);
                                                }
                                                String status = null;
                                                try {
                                                    status = URLEncoder.encode("ENDED", "UTF-8");
                                                    String url = String.format("http://www.schools-live.com/updateGameStatusService.php?id=%s&status=%s", gameid, status);
                                                    sendGETUpdateGame(url);
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                if(getGameListSharedPreferences()) {
                                                    if(notificationGameList.indexOf(game) != -1) {
                                                        int index = notificationGameList.indexOf(game);
                                                        notificationGameList.remove(index);
                                                        saveGameListSharedPreferences(notificationGameList);
                                                    }
                                                }

                                            }
                                            else if (!game.getSport().equals("Cricket") && calendar.getTimeInMillis() - gameCalendar.getTimeInMillis() >  TimeUnit.HOURS.toMillis(2)) {
                                                if(getNotificationSharedPreferences() != null && getNotificationSharedPreferences().equals("Checked")) {
                                                    String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("-")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("-")[1];
                                                    //scheduleNotification(getNotification2(text), 0);
                                                    showNotificationEnded("Game Ended!", text);
                                                    System.out.println("Got Here Twise");
                                                }
                                                String status = null;
                                                try {
                                                    status = URLEncoder.encode("ENDED", "UTF-8");
                                                    String url = String.format("http://www.schools-live.com/updateGameStatusService.php?id=%s&status=%s", gameid, status);
                                                    sendGETUpdateGame(url);
                                                } catch (UnsupportedEncodingException e) {
                                                    e.printStackTrace();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                if(getGameListSharedPreferences()) {
                                                    if(notificationGameList.indexOf(game) != -1) {
                                                        int index = notificationGameList.indexOf(game);
                                                        notificationGameList.remove(index);
                                                        saveGameListSharedPreferences(notificationGameList);
                                                    }
                                                }
                                            }

                                        else {
                                            System.out.println("Notifications are off");
                                        }
                                    }
                                }
                                else {
                                    stopSelf();
                                }
                    }
                    else {
                        stopSelf();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
     //   httpGetAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramURL);
        httpGetAsyncTask.execute(paramURL);
    }

    public void sendGETforScoreUpdates(String paramURL, final Game originalGame) throws IOException {

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
                    if(result.contains("Got Result")) {
                        result = result.replace("Got Result<br>","");
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<Game> gameList = new ArrayList<Game>();
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                String gameID = arr.getJSONObject(i).getString("Game_ID");
                                String homeSchoolName = arr.getJSONObject(i).getString("Home_School_Name");
                                String awaySchoolName = arr.getJSONObject(i).getString("Away_School_Name");
                                String schoolsType = arr.getJSONObject(i).getString("Schools_Type");
                                String field = arr.getJSONObject(i).getString("Field");
                                String sport = arr.getJSONObject(i).getString("Sport");
                                String ageGroup = arr.getJSONObject(i).getString("Age_Group");
                                String team = arr.getJSONObject(i).getString("Team");
                                String startTime = arr.getJSONObject(i).getString("Start_Time");
                                startTime = convertUTCTimeInToLocal(startTime);
                                String weather = arr.getJSONObject(i).getString("Weather");
                                String temperature = arr.getJSONObject(i).getString("Temperature");
                                String status = arr.getJSONObject(i).getString("Status");
                                String score = arr.getJSONObject(i).getString("Score");
                                String lastUpdateBy = arr.getJSONObject(i).getString("Last_Update_By");
                                String lastUpdateTime = arr.getJSONObject(i).getString("Last_Update_Time");
                                String homeSchoolURL = arr.getJSONObject(i).getString("Home_School_Logo");
                                String awaySchoolURL = arr.getJSONObject(i).getString("Away_School_Logo");
                                String won = arr.getJSONObject(i).getString("Won");
                                gameList.add(new Game(gameID, homeSchoolName, awaySchoolName, schoolsType, field, sport, ageGroup, team, startTime, weather, temperature, status, score, lastUpdateBy, lastUpdateTime, homeSchoolURL, awaySchoolURL, won));


                                if(score.equals(originalGame.getScore())) {

                                }
                                else {
                                    System.out.println("Original Score: " + originalGame.getScore());
                                    System.out.println("New Score: " + score);
                                    if(getNotificationSharedPreferences() != null && getNotificationSharedPreferences().equals("Checked")) {
                                        if(!sport.equals("Cricket")) {
                                            String text = sport + " " + ageGroup + team + " " + "-" + " " + homeSchoolName + " " + score.split("-")[0] + " " + "-" + " " + awaySchoolName + " " + score.split("-")[1];
                                            //showNotification("Score Update!", text);
                                            //scheduleNotification(getNotification3(text), 0);
                                            showNotificationScoreUpdate("Score Update!", text);
                                        }
                                        else {
                                            if(score.split("/").length == 5) {
                                                String text = sport + " " + ageGroup + team + " " + "-" + " " + score.split("/")[2] + " " + "(Bat)" + "-" + " " + score.split("/")[3] + " " + "(Bowl)" + "-" + " " + score.split("/")[0].trim() + "/" + score.split("/")[1].trim() + " " +  " " + "Overs: " + score.split("/")[4];
                                                //scheduleNotification(getNotification3(text), 0);
                                                showNotificationScoreUpdate("Score Update!", text);
                                            }
                                            else {
                                                String text = sport + " " + ageGroup + team + " " + "-" + " " + homeSchoolName + " " + score.split("/")[0] + " " + "-" + " " + awaySchoolName + " " + score.split("/")[1];
                                                //scheduleNotification(getNotification3(text), 0);
                                                showNotificationScoreUpdate("Score Update!", text);
                                            }
                                        }
                                    }
                                    int index = notificationGameList.indexOf(originalGame);
                                    originalGame.setScore(score);
                                    notificationGameList.set(index, originalGame);
                                    saveGameListSharedPreferences(notificationGameList);

                                    for(i=0; i<notificationGameList.size(); i++) {
                                        System.out.println("Game ID: " + notificationGameList.get(i).getGameID());
                                        System.out.println("Game Score: " + notificationGameList.get(i).getScore());
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        //  System.out.println("Size: " + schoolNames.size());
                    }
                    else {
                        System.out.println("No Game under Notifications");
                        stopSelf();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        //   httpGetAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramURL);
        httpGetAsyncTask.execute(paramURL);
    }

    public String getNotificationSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("Notification", null) != null) {
            return sharedPreferences.getString("Notification", null);
        }
        return null;
    }

    public void saveGameListSharedPreferences(List counterList) {
        Gson gson = new Gson();
        String gameString = gson.toJson(counterList);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Notification Game List", gameString).commit();
    }

    public void saveCounterSharedPreferences(int counter) {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("Counter", counter).commit();
    }

    public int getCounterSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        int counter = sharedPreferences.getInt("Counter", -1);
        return counter;
    }

    public boolean getGameListSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
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

}