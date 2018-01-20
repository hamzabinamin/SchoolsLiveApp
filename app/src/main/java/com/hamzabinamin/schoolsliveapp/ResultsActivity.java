package com.hamzabinamin.schoolsliveapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements View.OnClickListener {

    Button weekDropDown;
    Button gameDropDown;
    Button addGameDropDown;
    TextView weekTextView;
    TextView gameTextView;
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
    ImageView imageView;
    ListView listView;
    ProgressDialog progressDialog;
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
            setContentView(R.layout.activity_results_small);
        else if (screenInches > 4)
            setContentView(R.layout.activity_results);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);

        weekDropDown = (Button) findViewById(R.id.weekDropDown);
        gameDropDown = (Button) findViewById(R.id.gameDropDown);
        addGameDropDown = (Button) findViewById(R.id.addGameDropDown);
        weekTextView = (TextView)findViewById(R.id.weekTextView);
        gameTextView = (TextView) findViewById(R.id.gameTextView);
        schoolTypeTextView = (TextView) findViewById(R.id.schoolTypeTextView);
        schoolNameTextView = (TextView) findViewById(R.id.schoolNameTextView);
        schoolLocationTextView = (TextView) findViewById(R.id.schoolLocationTextView);
        schoolLinkTextView = (TextView) findViewById(R.id.schoolLinkTextView);
        fixturesTextView = (TextView) findViewById(R.id.fixturesTextView);
        resultsTextView = (TextView) findViewById(R.id.resultsTextView);
        liveNowTextView = (TextView) findViewById(R.id.liveNowTextView);
        twitterTextView = (TextView) findViewById(R.id.twitterTextView);
        facebookTextView = (TextView) findViewById(R.id.facebookTextView);
        addGameTextView = (TextView) findViewById(R.id.addGameTextView);
        listView = (ListView) findViewById(R.id.listView);
        progressDialog = new ProgressDialog(this);
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
                        sendEmail();
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
        resultsTextView.setTextColor(getResources().getColor(R.color.colorRed));
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        weekDropDown.setOnClickListener(this);
        gameDropDown.setOnClickListener(this);
        addGameDropDown.setOnClickListener(this);
        fixturesTextView.setOnClickListener(this);
        liveNowTextView.setOnClickListener(this);
        resultsTextView.setOnClickListener(this);
        facebookTextView.setOnClickListener(this);
        twitterTextView.setOnClickListener(this);
        addGameTextView.setOnClickListener(this);

        if(getSchoolSharedPreferences()) {
           /* progressDialog.setMessage("Please Wait");
            progressDialog.show();
            try {
                String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String url = String.format("http://schools-live.com/getOneSchool.php?name=%s", schoolName);
                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                sendGET(url);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } */
            schoolTypeTextView.setText(school.getSchoolType());
            schoolNameTextView.setText(school.getSchoolName());
            schoolLocationTextView.setText(school.getSchoolLocation());
            schoolLinkTextView.setText(school.getSchoolWebsite());

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(school.getSchoolImage(), imageView, options);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void sendEmail() {
        String[] TO = {"info@schools-live.com"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Schools-Live");
        //emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getBaseContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
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

        DatePickerDialog mDatePicker=new DatePickerDialog(ResultsActivity.this, new DatePickerDialog.OnDateSetListener() {
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

        final DatePickerDialog mDatePicker=new DatePickerDialog(ResultsActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                        System.out.println("Sport: " + sport);
                        range1 = URLEncoder.encode(date1, "UTF-8");
                        String range2 = URLEncoder.encode(date2, "UTF-8");
                        if(sport.contains("ALL+GAMES")) {
                            String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                            sendGETforGames(url);
                        }
                        else {
                            String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
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

                        try {
                            String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                            String url = String.format("http://schools-live.com/getAllEndedGames.php?home=%s&away=%s", schoolName, schoolName);
                            sendGETforGames(url);

                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
                                String category = arr.getJSONObject(i).getString("Category");
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
                                gameList.add(new Game(gameID, homeSchoolName, awaySchoolName, schoolsType, category, sport, ageGroup, team, startTime, weather, temperature, status, score, lastUpdateBy, lastUpdateTime, homeSchoolURL, awaySchoolURL, won ));

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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.addGameTextView:
                finish();
                startActivity(new Intent(getBaseContext(), AddGameActivity.class));
                break;

            case R.id.weekDropDown:
                PopupMenu popup = new PopupMenu(ResultsActivity.this, weekDropDown);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popup3, popup.getMenu());

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
                                    String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
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
                                    String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
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
                                    String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }

                            }
                            else if(item.getTitle().toString().equals("C.D. RANGE")) {
                                openCalendar();
                            }

                            else {
                                String url = String.format("http://schools-live.com/getEndedGameBySport.php?home=%s&away=%s&sport=%s", schoolName, schoolName, sport);
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
                popup = new PopupMenu(ResultsActivity.this, gameDropDown);
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
                                    String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(weekTextView.getText().toString().equals("LAST WEEK")) {
                                range1 = URLEncoder.encode(getLocalTimePreviousWeek(), "UTF-8");
                                range2 = URLEncoder.encode(getLocalTime(), "UTF-8");

                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(weekTextView.getText().toString().equals("NEXT WEEK")) {
                                range1 = URLEncoder.encode(getLocalTime(), "UTF-8");
                                range2 = URLEncoder.encode(getLocalTimeNextWeek(), "UTF-8");

                                if(sport.contains("ALL+GAMES")) {
                                    String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                                    sendGETforGames(url);
                                }
                                else {
                                    String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                                    sendGETforGames(url);
                                }
                            }
                            else if(weekTextView.getText().toString().trim().contains("C.D. RANGE")) {
                                progressDialog.dismiss();
                                openCalendar();
                            }
                            else {
                                System.out.println("Week: " + weekTextView.getText().toString());
                                Toast.makeText(getBaseContext(), "Nothing" , Toast.LENGTH_SHORT).show();
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

            case R.id.fixturesTextView:
                finish();
                startActivity(new Intent(getBaseContext(), SchoolActivity.class));
                break;

            case R.id.liveNowTextView:
                finish();
                startActivity(new Intent(getBaseContext(), LiveNowActivity.class));
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
                String schoolName = URLEncoder.encode(school.getSchoolName(), "UTF-8");
                String sport = URLEncoder.encode(gameTextView.getText().toString(), "UTF-8");
                String range1 = "";
                String range2 = "";

                if(weekTextView.getText().toString().equals("THIS WEEK")) {
                    range1 = URLEncoder.encode(getLocalTimeFirstDayOfCurrentWeek(), "UTF-8");
                    range2 = URLEncoder.encode(getLocalTimeLastDayOfCurrentWeek(), "UTF-8");


                    if(sport.contains("ALL+GAMES")) {
                        String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                        sendGETforGames(url);
                    }
                    else {
                        String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                        sendGETforGames(url);
                    }
                }
                else if(weekTextView.getText().toString().equals("LAST WEEK")) {
                    range1 = URLEncoder.encode(getLocalTimePreviousWeek(), "UTF-8");
                    range2 = URLEncoder.encode(getLocalTime(), "UTF-8");

                    if(sport.contains("ALL+GAMES")) {
                        String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                        sendGETforGames(url);
                    }
                    else {
                        String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                        sendGETforGames(url);
                    }
                }
                else if(weekTextView.getText().toString().equals("NEXT WEEK")) {
                    range1 = URLEncoder.encode(getLocalTime(), "UTF-8");
                    range2 = URLEncoder.encode(getLocalTimeNextWeek(), "UTF-8");

                    if(sport.contains("ALL+GAMES")) {
                        String url = String.format("http://schools-live.com/getAllEndedGamesInRange.php?home=%s&away=%s&range1=%s&range2=%s", schoolName, schoolName, range1, range2);
                        sendGETforGames(url);
                    }
                    else {
                        String url = String.format("http://schools-live.com/getEndedGamesInRange.php?home=%s&away=%s&sport=%s&range1=%s&range2=%s", schoolName, schoolName, sport, range1, range2);
                        sendGETforGames(url);
                    }
                }
                else if(weekTextView.getText().toString().equals("C.D. RANGE")) {
                    progressDialog.dismiss();
                    openCalendar();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        /*    String url = String.format("http://schools-live.com/getAllEndedGames.php?home=%s&away=%s", schoolName, schoolName);
            sendGETforGames(url); */
        }
        else {
            startActivity(new Intent(getBaseContext(), ManageSchoolsActivity.class));
        }
    }
}
