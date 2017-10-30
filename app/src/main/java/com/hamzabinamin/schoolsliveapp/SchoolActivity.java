package com.hamzabinamin.schoolsliveapp;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.security.AccessController.getContext;


public class SchoolActivity extends AppCompatActivity implements View.OnClickListener {

    Button weekDropDownButton;
    Button gameDropDownButton;
    Button addGameButton;
    TextView schoolTypeTextView;
    TextView schoolNameTextView;
    TextView schoolLocationTextView;
    TextView schoolLinkTextView;
    TextView fixturesTextView;
    TextView liveNowTextView;
    TextView resultsTextView;
    TextView facebookTextView;
    TextView twitterTextView;
    TextView addGameTextView;
    TextView gameTextView;
    TextView weekTextView;
    ImageView imageView;
    ListView listView;
    ProgressDialog progressDialog;
    List arrayList;
    List<Game> gameListStore = new ArrayList<>();
    CustomAdapter customAdapter;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    School school;
    List<String> schoolNames = new ArrayList<>();
    String date1;
    String date2;
    boolean firstDialogOpened;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dens = dm.densityDpi;
        double wi = (double) width / (double) dens;
        double hi = (double) height / (double) dens;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);
        double screenInches = Math.sqrt(x + y);
        if (screenInches <= 4)
            setContentView(R.layout.activity_school_small);
        else if (screenInches > 4)
            setContentView(R.layout.activity_school);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        weekDropDownButton = (Button) findViewById(R.id.weekDropDown);
        gameDropDownButton = (Button) findViewById(R.id.gameDropDown);
        addGameButton = (Button) findViewById(R.id.addGameDropDown);
        weekDropDownButton = (Button) findViewById(R.id.weekDropDown);
        schoolTypeTextView = (TextView) findViewById(R.id.schoolTypeTextView);
        schoolNameTextView = (TextView) findViewById(R.id.schoolNameTextView);
        schoolLocationTextView = (TextView) findViewById(R.id.schoolLocationTextView);
        schoolLinkTextView = (TextView) findViewById(R.id.schoolLinkTextView);
        fixturesTextView = (TextView) findViewById(R.id.fixturesTextView);
        liveNowTextView = (TextView) findViewById(R.id.liveNowTextView);
        resultsTextView = (TextView) findViewById(R.id.resultsTextView);
        facebookTextView = (TextView) findViewById(R.id.facebookTextView);
        twitterTextView = (TextView) findViewById(R.id.twitterTextView);
        addGameTextView = (TextView) findViewById(R.id.addGameTextView);
        gameTextView = (TextView) findViewById(R.id.gameTextView);
        weekTextView = (TextView) findViewById(R.id.weekTextView);
        listView = (ListView) findViewById(R.id.listView);
        progressDialog = new ProgressDialog(this);
        Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(),R.drawable.logopaarl);
        Bitmap roundImage = RoundedImageView.getCroppedBitmap(bitmapImage, 6);
        arrayList = new ArrayList<Game>();
        //schoolLogoImageView.setBackgroundResource(R.drawable.logopaarl);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.addSchool:
                        finish();
                        startActivity(new Intent(getBaseContext(), AddSchoolActivity.class));
                        break;

                    case R.id.editSchool:
                        finish();
                        startActivity(new Intent(getBaseContext(), EditSelectSchoolActivity.class));
                        break;

                    case R.id.changeSchool:
                        finish();
                        startActivity(new Intent(getBaseContext(), ChangeSchoolActivity.class));
                        break;

                    case R.id.notifications:
                        finish();
                        startActivity(new Intent(getBaseContext(), NotificationActivity.class));
                        break;

                    case R.id.leaderboard:
                        finish();
                        startActivity(new Intent(getBaseContext(), LearderboardActivity.class));
                        break;

                    case R.id.settings:
                        finish();
                        startActivity(new Intent(getBaseContext(), UpdateAccountActivity.class));
                        break;

                    case R.id.share:
                        launchMarket();
                        break;

                    case R.id.game:
                        finish();
                        startActivity(new Intent(getBaseContext(), SchoolActivity.class));
                        break;
                }
                return false;
            }
        });

        View hView = mNavigationView.getHeaderView(0);
        imageView = (ImageView) hView.findViewById(R.id.profile_image);
        fixturesTextView.setTextColor(getResources().getColor(R.color.colorRed));
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        weekDropDownButton.setOnClickListener(this);
        gameDropDownButton.setOnClickListener(this);
        addGameButton.setOnClickListener(this);
        liveNowTextView.setOnClickListener(this);
        resultsTextView.setOnClickListener(this);
        facebookTextView.setOnClickListener(this);
        twitterTextView.setOnClickListener(this);
        addGameTextView.setOnClickListener(this);
        weekTextView.setOnClickListener(this);

        if(getSchoolSharedPreferences()) {
            progressDialog.setMessage("Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            try {
                String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String url = String.format("http://schools-live.com/getOneSchool.php?name=%s", schoolName);
                sendGET(url);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            schoolTypeTextView.setText(school.getSchoolType());
            schoolNameTextView.setText(school.getSchoolName());
            //schoolLocationTextView.setText(school.getSchoolLocation());
            //schoolLinkTextView.setText(school.getSchoolWebsite());

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(school.getSchoolImage(), imageView, options);

        }

  /*      listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {

                customAdapter.remove(arrayList.remove()[position]);
                customAdapter.notifyDataSetChanged();

                return false;
            }

        }); */

        //scheduleNotification(getNotification("u16A vs u17A"), 5000);
    }

    private void setNextSchedule(long interval) {
        long time = interval ;
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pi = PendingIntent.getService(this, 0, new Intent(this, ScheduledService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        // am.setInexactRepeating(AlarmManager.RTC_WAKEUP, time, pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis(), interval, pi);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void launchMarket() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
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

    public  void addList() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getLocalTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Calendar calendar = Calendar.getInstance();
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public String getLocalTimePreviousWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, - 7);
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public String getLocalTimeNextWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, + 7);
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public String getLocalTimeFirstDayOfCurrentWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public String getLocalTimeLastDayOfCurrentWeek() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        calendar.add(Calendar.DATE, 7);
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public void openCalendar() {
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear=mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker=new DatePickerDialog(SchoolActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
                Calendar calendar = Calendar.getInstance();
                calendar.set(selectedyear, selectedmonth, selectedday);
                date1 = sdf.format(calendar.getTime());
                firstDialogOpened = true;
                openCalendar2();

            }
        },mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    public void openCalendar2() {
        Calendar mcurrentDate=Calendar.getInstance();
        int mYear=mcurrentDate.get(Calendar.YEAR);
        int mMonth=mcurrentDate.get(Calendar.MONTH);
        int mDay=mcurrentDate.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog mDatePicker=new DatePickerDialog(SchoolActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                // TODO Auto-generated method stub
                   if(firstDialogOpened) {
                       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
                       Calendar calendar = Calendar.getInstance();
                       calendar.set(selectedyear, selectedmonth, selectedday);
                       date2 = sdf.format(calendar.getTime());

                       progressDialog.setMessage("Please Wait");
                       progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                       progressDialog.setIndeterminate(true);
                       progressDialog.setCancelable(false);
                       progressDialog.setCanceledOnTouchOutside(false);
                       progressDialog.show();

                       String range1 = null;
                       try {
                           String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                           String sport = URLEncoder.encode(gameTextView.getText().toString(), "UTF-8");
                           range1 = URLEncoder.encode(date1, "UTF-8");
                           String range2 = URLEncoder.encode(date2, "UTF-8");
                           if(sport.contains("ALL GAMES")) {
                               String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                               sendGETforGames(url);
                           }
                           else {
                               String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                               sendGETforGames(url);
                           }
                       } catch (UnsupportedEncodingException e) {
                           e.printStackTrace();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }

                   }
                   else {
                       Toast.makeText(getBaseContext(), "Please select the starting Date first", Toast.LENGTH_SHORT).show();
                   }
            }
        },mYear, mMonth, mDay);
        mDatePicker.setTitle("Select Date");
        mDatePicker.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.weekDropDown:
                PopupMenu popup = new PopupMenu(SchoolActivity.this, weekDropDownButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup2, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().toString().equals("CUSTOM DATE RANGE")) {
                            weekTextView.setText("C.D. RANGE");
                        }
                        else {
                            weekTextView.setText(item.getTitle().toString());
                        }
                        customAdapter = new CustomAdapter(getBaseContext(), new ArrayList<Game>());
                        listView.setAdapter(customAdapter);

                        try {
                            String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                            String sport = URLEncoder.encode(gameTextView.getText().toString(), "UTF-8");

                            if(item.getTitle().toString().equals("THIS WEEK")) {
                                progressDialog.setMessage("Please Wait");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                String range1 = URLEncoder.encode(getLocalTimeFirstDayOfCurrentWeek(), "UTF-8");
                                String range2 = URLEncoder.encode(getLocalTimeLastDayOfCurrentWeek(), "UTF-8");
                                System.out.println("Sport: " + sport);
                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(item.getTitle().toString().equals("LAST WEEK")) {
                                progressDialog.setMessage("Please Wait");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                String range1 = URLEncoder.encode(getLocalTimePreviousWeek(), "UTF-8");
                                String range2 = URLEncoder.encode(getLocalTime(), "UTF-8");
                                System.out.println("Sport: " + sport);
                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(item.getTitle().toString().equals("NEXT WEEK")) {
                                progressDialog.setMessage("Please Wait");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setCancelable(false);
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                String range1 = URLEncoder.encode(getLocalTime(), "UTF-8");
                                String range2 = URLEncoder.encode(getLocalTimeNextWeek(), "UTF-8");
                                System.out.println("Sport: " + sport);
                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }

                            }
                            else if(item.getTitle().toString().equals("C.D. RANGE")) {
                                openCalendar();
                            }

                            else {
                                String url = String.format("http://schools-live.com/getGameBySport.php?home=%s&away=%s&sport=%s", schoolName, schoolName, sport);
                                sendGETforGames(url);
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
                break;

            case R.id.gameDropDown:
                //Creating the instance of PopupMenu
                popup = new PopupMenu(SchoolActivity.this, gameDropDownButton);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        gameTextView.setText(item.getTitle().toString());
                        customAdapter = new CustomAdapter(getBaseContext(), new ArrayList<Game>());
                        listView.setAdapter(customAdapter);
                        progressDialog.setMessage("Please Wait");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        try {
                            String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                            String sport = URLEncoder.encode(item.getTitle().toString(), "UTF-8");
                            String range1 = "";URLEncoder.encode(getLocalTime(), "UTF-8");
                            String range2 = "";URLEncoder.encode(getLocalTimeNextWeek(), "UTF-8");

                            if(weekTextView.getText().toString().equals("THIS WEEK")) {
                                range1 = URLEncoder.encode(getLocalTimeFirstDayOfCurrentWeek(), "UTF-8");
                                range2 = URLEncoder.encode(getLocalTimeLastDayOfCurrentWeek(), "UTF-8");

                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(weekTextView.getText().toString().equals("LAST WEEK")) {
                                range1 = URLEncoder.encode(getLocalTimePreviousWeek(), "UTF-8");
                                range2 = URLEncoder.encode(getLocalTime(), "UTF-8");

                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(weekTextView.getText().toString().equals("NEXT WEEK")) {
                                range1 = URLEncoder.encode(getLocalTime(), "UTF-8");
                                range2 = URLEncoder.encode(getLocalTimeNextWeek(), "UTF-8");

                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(weekTextView.getText().toString().equals("C.D. RANGE")) {
                                progressDialog.dismiss();
                                openCalendar();
                            }
                            else {
                                Toast.makeText(getBaseContext(), "Got Here", Toast.LENGTH_SHORT).show();
                            }

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return true;
                    }
                });

                popup.show(); //showing popup menu
                break;

            case R.id.addGameDropDown:
                finish();
                startActivity(new Intent(getBaseContext(), AddGameActivity.class));
                break;

        /*    case R.id.addGameTextView:
                finish();
                startActivity(new Intent(getBaseContext(), AddGameActivity.class));
                break; */

            case R.id.liveNowTextView:
                finish();
                startActivity(new Intent(getBaseContext(), LiveNowActivity.class));
                break;

            case R.id.resultsTextView:
                finish();
                startActivity(new Intent(getBaseContext(), ResultsActivity.class));
                break;

            case R.id.facebookTextView:
                if(school.getSchoolFacebook().length() > 0) {
                    System.out.println("Result: " + school.getSchoolFacebook());
                    String newValue = school.getSchoolFacebook();
                    Intent intent = new Intent(getApplicationContext(), FacebookActivity.class);
                    intent.putExtra("mUrl", newValue);
                    startActivity(intent);
                }
                break;

            case R.id.twitterTextView:
                if(school.getSchoolTwitter().length() > 0) {
                    System.out.println("Result: " + school.getSchoolTwitter());
                    String newValue = school.getSchoolTwitter();
                    Intent intent = new Intent(getApplicationContext(), TwitterActivity.class);
                    intent.putExtra("mUrl", newValue);
                    startActivity(intent);
                }
                break;

        }
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

    public class httpDownloadImages extends AsyncTask<Void,Void,String> {
        Bitmap myBitmap[] = new Bitmap[10];
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("Here in Do In Background");
                //       for(int i=1; i<=10; i++){
                // schoolName = URLEncoder.encode(schoolName, "UTF-8");
                if (school.getSchoolName().contains("%20") || school.getSchoolName().contains(" ")) {
                    String src = "http://schools-live.com/school-images/" + URLEncoder.encode(school.getSchoolName(), "UTF-8").replace("+", "%20").replace("B", "0") + ".png";
                    System.out.println("URL: " + "http://schools-live.com/school-images/" + URLEncoder.encode(school.getSchoolName(), "UTF-8").replace("+", "%20").replace("B", "0") + ".png");
                    java.net.URL url = new java.net.URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap[0] = BitmapFactory.decodeStream(input);
                    if (myBitmap[0] != null) {
                        System.out.println("Here in If condition");
                        //imageView.setImageBitmap(myBitmap[0]);
                        //saveimagetoFile(myBitmap[0], 0);
                    } else {
                        System.out.println("Here in Else condition");
                    }
                    //     }
                }
                else {
                    String src = "http://schools-live.com/school-images/" + URLEncoder.encode(school.getSchoolName(), "UTF-8") + ".png";
                    System.out.println("URL: " + "http://schools-live.com/school-images/" + URLEncoder.encode(school.getSchoolName(), "UTF-8") + ".png");
                    java.net.URL url = new java.net.URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap[0] = BitmapFactory.decodeStream(input);
                    if (myBitmap[0] != null) {
                        System.out.println("Here in If condition");
                        //imageView.setImageBitmap(myBitmap[0]);
                        //saveimagetoFile(myBitmap[0], 0);
                    } else {
                        System.out.println("Here in Else condition");
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return BitMapToString(myBitmap[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            imageView.setImageBitmap(StringToBitMap(s));
            progressDialog.dismiss();
        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void sendGET(String paramURL) throws IOException {

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
                        progressDialog.dismiss();
                        result = result.replace("Got Result<br>","");
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String schoolID = "";
                        String schoolLogo = "";
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                schoolID = arr.getJSONObject(i).getString("School_ID");
                                schoolLogo = arr.getJSONObject(i).getString("School_Logo");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                   /*     try {
                            String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                            String url = String.format("http://schools-live.com/getAllFixturesGames.php?home=%s&away=%s", schoolName, schoolName);
                            sendGETforGames(url);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } */
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "There was an Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
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
                progressDialog.dismiss();
                if(result != null) {
                    progressDialog.dismiss();
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
                                String won = arr.getJSONObject(i).getString("Won");
                                gameList.add(new Game(gameID, homeSchoolName, awaySchoolName, schoolsType, field, sport, ageGroup, team, startTime, weather, temperature, status, score, lastUpdateBy, lastUpdateTime, homeSchoolURL, awaySchoolURL, won));

                                if(!schoolNames.contains(homeSchoolName)) {
                                    schoolNames.add(homeSchoolName);
                                }
                                if(!schoolNames.contains(awaySchoolName)) {
                                    schoolNames.add(awaySchoolName);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                       System.out.println("Size: " + schoolNames.size());

                        customAdapter = new CustomAdapter(getBaseContext(), gameList);
                        listView.setAdapter(customAdapter);
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "No Games to display", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(getSchoolSharedPreferences()) {
            customAdapter = new CustomAdapter(getBaseContext(), new ArrayList<Game>());
            listView.setAdapter(customAdapter);

            progressDialog.setMessage("Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            try {
            /*    String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String url = String.format("http://schools-live.com/getAllFixturesGames.php?home=%s&away=%s", schoolName, schoolName);
                sendGETforGames(url); */

                String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String sport = URLEncoder.encode(gameTextView.getText().toString(), "UTF-8");
                String range1 = "";
                String range2 = "";

                if(weekTextView.getText().toString().equals("THIS WEEK")) {
                    range1 = URLEncoder.encode(getLocalTimeFirstDayOfCurrentWeek(), "UTF-8");
                    range2 = URLEncoder.encode(getLocalTimeLastDayOfCurrentWeek(), "UTF-8");


                    if(sport.contains("ALL+GAMES")) {
                        String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                        sendGETforGames(url);
                    }
                    else {
                        String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                        sendGETforGames(url);
                    }
                }
                else if(weekTextView.getText().toString().equals("LAST WEEK")) {
                    range1 = URLEncoder.encode(getLocalTimePreviousWeek(), "UTF-8");
                    range2 = URLEncoder.encode(getLocalTime(), "UTF-8");

                    if(sport.contains("ALL+GAMES")) {
                        String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                        sendGETforGames(url);
                    }
                    else {
                        String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                        sendGETforGames(url);
                    }
                }
                else if(weekTextView.getText().toString().equals("NEXT WEEK")) {
                    range1 = URLEncoder.encode(getLocalTime(), "UTF-8");
                    range2 = URLEncoder.encode(getLocalTimeNextWeek(), "UTF-8");

                    if(sport.contains("ALL+GAMES")) {
                        String url = String.format("http://schools-live.com/getAllFixtureGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                        sendGETforGames(url);
                    }
                    else {
                        String url = String.format("http://schools-live.com/getFixtureGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                        sendGETforGames(url);
                    }
                }
                else if(weekTextView.toString().equals("C.D. RANGE")) {
                    openCalendar();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(!isMyServiceRunning(ScheduledService.class)) {
                Log.e("School Activity", "Calling Scheduled Service");
                setNextSchedule(60000);
            }
        }
        else {
            Toast.makeText(getBaseContext(), "Either add a new School or Select existing one from  Change School", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getBaseContext(), ManageSchoolsActivity.class));
        }
    }
}
