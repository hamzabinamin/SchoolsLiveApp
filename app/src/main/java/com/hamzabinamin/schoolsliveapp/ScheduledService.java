package com.hamzabinamin.schoolsliveapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
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
import java.util.TimeZone;

public class ScheduledService extends Service {

    School school;
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
                String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String url = String.format("http://schools-live.com/getAllFixturesGames.php?home=%s&away=%s", schoolName, schoolName);
                sendGETforGames(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Not School, Service didn't start");
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

    public SimpleDateFormat convertUTCTimeInToLocal(String dateString) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy / HH:mm a");
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        String formattedDate = null;
        try {
            date = df.parse(dateString);
            df.setTimeZone(TimeZone.getDefault());
            //formattedDate = df.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return df;
    }

    public SimpleDateFormat getLocalTime() {
        //SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy / h:mm a");
        SimpleDateFormat sdf = new SimpleDateFormat("M-d-yyyy / h:mm a");
        Calendar calendar = Calendar.getInstance();
        //sdf = sdf.format(calendar.getTime());
        return sdf;
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Live Game Right Now!");
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
                                String weather = arr.getJSONObject(i).getString("Weather");
                                String temperature = arr.getJSONObject(i).getString("Temperature");
                                String status = arr.getJSONObject(i).getString("Status");
                                String score = arr.getJSONObject(i).getString("Score");
                                String lastUpdateBy = arr.getJSONObject(i).getString("Last_Update_By");
                                String lastUpdateTime = arr.getJSONObject(i).getString("Last_Update_Time");
                                String homeSchoolURL = arr.getJSONObject(i).getString("Home_School_Logo");
                                String awaySchoolURL = arr.getJSONObject(i).getString("Away_School_Logo");
                                gameList.add(new Game(gameID, homeSchoolName, awaySchoolName, schoolsType, field, sport, ageGroup, team, startTime, weather, temperature, status, score, lastUpdateBy, lastUpdateTime, homeSchoolURL, awaySchoolURL));


                                if(gameList.size() > 0) {
                                    for (int j = 0; j < gameList.size(); j++) {
                                        Game game = gameList.get(j);
                                        String gameid = game.getGameID();
                                        String gamedate = game.getStartTime();
                                        //gamedate = convertUTCTimeInToLocal(gamedate);
                                        Calendar calendar = Calendar.getInstance();
                                        String currentTime = getLocalTime().format(calendar.getTime());
                                        //calendar = getLocalTime().getCalendar();
                                        calendar.setTime(getLocalTime().parse(currentTime));
                                        Calendar gameCalendar = Calendar.getInstance();
                                        gameCalendar.setTime(getLocalTime().parse(gamedate));
                                        System.out.println("Game Calendar: " + gameCalendar.getTime());
                                        System.out.println("Current Calendar: " + calendar.getTime());

                                        System.out.println("Game Calendar Date: " + gameCalendar.get(Calendar.DATE));
                                        System.out.println("Current Calendar Date: " + calendar.get(Calendar.DATE));

                                        System.out.println("Game Time: " + gamedate);
                                        System.out.println("Current Time: " + currentTime);

                                        if (gamedate.equals(currentTime) || (calendar.get(Calendar.HOUR) == gameCalendar.get(Calendar.HOUR) && calendar.get(Calendar.AM_PM) == gameCalendar.get(Calendar.AM_PM) && ((calendar.get(Calendar.MINUTE) - gameCalendar.get(Calendar.MINUTE) <= 10) && (calendar.get(Calendar.MINUTE) - gameCalendar.get(Calendar.MINUTE) >= 0)) && calendar.get(Calendar.DATE) == gameCalendar.get(Calendar.DATE) && calendar.get(Calendar.MONTH) == gameCalendar.get(Calendar.MONTH) && calendar.get(Calendar.YEAR) == gameCalendar.get(Calendar.YEAR))) {
                                            String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("-")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("-")[1];
                                            scheduleNotification(getNotification(text), 0);
                                            status = URLEncoder.encode("1st HALF", "UTF-8");
                                            String url = String.format("http://www.schools-live.com/updateGameStatus.php?id=%s&status=%s", gameid, status);
                                            sendGETUpdateGame(url);
                                        }
                                    }
                                }
                                else {
                                    stopSelf();
                                    cancelAlarm();
                                }

                             /*   if(!schoolNames.contains(homeSchoolName)) {
                                    schoolNames.add(homeSchoolName);
                                }
                                if(!schoolNames.contains(awaySchoolName)) {
                                    schoolNames.add(awaySchoolName);
                                } */


                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                      //  System.out.println("Size: " + schoolNames.size());
                    }
                    else {
                        stopSelf();
                        cancelAlarm();
                    }
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

}