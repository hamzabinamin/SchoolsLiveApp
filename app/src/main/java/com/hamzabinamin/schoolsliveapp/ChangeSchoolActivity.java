package com.hamzabinamin.schoolsliveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChangeSchoolActivity extends AppCompatActivity implements View.OnClickListener {

    Button changeSchoolButton;
    ImageView imageView;
    Spinner changeSchoolSpinner;
    ProgressDialog progressDialog;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    List<School> schoolList = new ArrayList<School>();
    School school;
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
            setContentView(R.layout.activity_change_school_small);
        else if (screenInches >= 4)
            setContentView(R.layout.activity_change_school);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        changeSchoolButton = (Button) findViewById(R.id.changeSchoolButton);
        changeSchoolSpinner = (Spinner) findViewById(R.id.schoolNameSpinner);
        progressDialog = new ProgressDialog(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActionBarToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open , R.string.navigation_drawer_close);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id) {
                    case R.id.manageSchools:
                        finish();
                        startActivity(new Intent(getBaseContext(), ManageSchoolsActivity.class));
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
                }
                return false;
            }
        });

        changeSchoolButton.setOnClickListener(this);
        View hView = mNavigationView.getHeaderView(0);
        imageView = (ImageView) hView.findViewById(R.id.profile_image);
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        if(getSchoolSharedPreferences()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(school.getSchoolImage(), imageView, options);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String url = String.format("http://schools-live.com/getSchools.php");
        try {
            progressDialog.setMessage("Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            sendGET(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        progressDialog.dismiss();
                        result = result.replace("Got Result<br>","");
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<String> list = new ArrayList<String>();
                        String schoolName = "";
                        String schoolType = "";
                        String schoolWebsite = "";
                        String schoolTwitter = "";
                        String schoolFacebook = "";
                        String schoolLocation = "";
                        String schoolLogo = "";

                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                schoolName = arr.getJSONObject(i).getString("School_Name");
                                schoolType = arr.getJSONObject(i).getString("School_Type");
                                schoolWebsite = arr.getJSONObject(i).getString("School_Website");
                                schoolTwitter = arr.getJSONObject(i).getString("School_Twitter");
                                schoolFacebook = arr.getJSONObject(i).getString("School_Facebook");
                                schoolLocation = arr.getJSONObject(i).getString("School_Location");
                                schoolLogo = arr.getJSONObject(i).getString("School_Logo");
                                list.add(schoolName);

                                School school = new School(schoolName, schoolType, schoolWebsite, schoolTwitter, schoolFacebook, schoolLocation, schoolLogo);
                                schoolList.add(school);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String[] schoolArray = list.toArray(new String[list.size()]);
                        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, schoolArray);
                        changeSchoolSpinner.setAdapter(adapter);
                    }

                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "No Schools have been added", Toast.LENGTH_SHORT).show();
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

            case R.id.changeSchoolButton:
                if(changeSchoolSpinner != null) {
                    if(changeSchoolSpinner.getSelectedItem().toString().length() > 0) {
                        School school = schoolList.get(changeSchoolSpinner.getSelectedItemPosition());

                  /*      DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                                .cacheOnDisc(true).cacheInMemory(true)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .displayer(new FadeInBitmapDisplayer(300)).build();

                        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                                getApplicationContext())
                                .defaultDisplayImageOptions(defaultOptions)
                                .memoryCache(new WeakMemoryCache())
                                .diskCacheSize(100 * 1024 * 1024).build();

                        ImageLoader.getInstance().init(config); */

                        MemoryCacheUtils.removeFromCache(school.getSchoolImage(), ImageLoader.getInstance().getMemoryCache());
                        DiskCacheUtils.removeFromCache(school.getSchoolImage(), ImageLoader.getInstance().getDiscCache());

                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true).resetViewBeforeLoading(true)
                                .showImageForEmptyUri(R.drawable.placeholder)
                                .showImageOnFail(R.drawable.placeholder)
                                .showImageOnLoading(R.drawable.placeholder).build();

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
                        imageLoader.displayImage(school.getSchoolImage(), imageView, options);
                        System.out.println("Change School: " + school.getSchoolImage());
                        saveSchoolSharedPreferences(school);
                        Toast.makeText(getBaseContext(), "School Changed Successfully", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public void saveSchoolSharedPreferences(School school) {
        Gson gson = new Gson();
        String serviceProviderString = gson.toJson(school);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("School String", serviceProviderString).commit();
    }
}
