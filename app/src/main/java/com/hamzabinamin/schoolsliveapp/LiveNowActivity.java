package com.hamzabinamin.schoolsliveapp;

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
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class LiveNowActivity extends AppCompatActivity implements View.OnClickListener {

    TextView schoolTypeTextView;
    TextView schoolNameTextView;
    TextView schoolLocationTextView;
    TextView schoolLinkTextView;
    TextView fixturesTextView;
    TextView liveNowTextView;
    TextView resultsTextView;
    TextView facebookTextView;
    TextView twitterTextView;
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
            setContentView(R.layout.activity_live_now_small);
        else if (screenInches > 4)
            setContentView(R.layout.activity_live_now);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        adView.loadAd(adRequest);

        schoolTypeTextView = (TextView) findViewById(R.id.schoolTypeTextView);
        schoolNameTextView = (TextView) findViewById(R.id.schoolNameTextView);
        schoolLocationTextView = (TextView) findViewById(R.id.schoolLocationTextView);
        schoolLinkTextView = (TextView) findViewById(R.id.schoolLinkTextView);
        fixturesTextView = (TextView) findViewById(R.id.fixturesTextView);
        resultsTextView = (TextView) findViewById(R.id.resultsTextView);
        liveNowTextView = (TextView) findViewById(R.id.liveNowTextView);
        twitterTextView = (TextView) findViewById(R.id.twitterTextView);
        facebookTextView = (TextView) findViewById(R.id.facebookTextView);
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
        liveNowTextView.setTextColor(getResources().getColor(R.color.colorRed));
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fixturesTextView.setOnClickListener(this);
        resultsTextView.setOnClickListener(this);
        facebookTextView.setOnClickListener(this);
        twitterTextView.setOnClickListener(this);

        if(getSchoolSharedPreferences()) {
          /*  progressDialog.setMessage("Please Wait");
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

            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300)).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getApplicationContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .memoryCache(new WeakMemoryCache())
                    .diskCacheSize(100 * 1024 * 1024).build();

            ImageLoader.getInstance().init(config);

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

    private void launchMarket() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        }
        catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
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
                            String url = String.format("http://schools-live.com/getAllLiveGames.php?home=%s&away=%s", schoolName, schoolName);
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
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fixturesTextView:
                finish();
                startActivity(new Intent(getBaseContext(), SchoolActivity.class));
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
                String url = String.format("http://schools-live.com/getAllLiveGames.php?home=%s&away=%s", schoolName, schoolName);
                sendGETforGames(url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            startActivity(new Intent(getBaseContext(), ManageSchoolsActivity.class));
        }
    }
}
