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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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

public class EditSelectSchoolActivity extends AppCompatActivity implements View.OnClickListener {

    Button editSchoolButton;
    ImageView imageView;
    Spinner schoolNameSpinner;
    ProgressDialog progressDialog;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    School school;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_select_school);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        editSchoolButton = (Button) findViewById(R.id.editSchoolButton);
        schoolNameSpinner = (Spinner) findViewById(R.id.schoolNameSpinner);

        editSchoolButton.setOnClickListener(this);

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
                        //  startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                        break;
                }
                return false;
            }
        });

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
                    .showImageForEmptyUri(android.R.drawable.arrow_down_float)
                    .showImageOnFail(android.R.drawable.ic_menu_report_image)
                    .showImageOnLoading(android.R.drawable.arrow_up_float).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(school.getSchoolImage(), imageView, options);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String url = String.format("http://schools-live.com/getSchools.php");
        try {
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            sendGET(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.editSchoolButton:
                if(schoolNameSpinner.getSelectedItem() != null) {
                    saveEditSchoolSharedPreferences(schoolNameSpinner.getSelectedItem().toString());
                    finish();
                    startActivity(new Intent(getBaseContext(), EditSchoolActivity.class));
                }
                break;
        }
    }

    public void saveEditSchoolSharedPreferences(String schoolName) {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("School Name", schoolName).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mActionBarToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    progressDialog.hide();
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
                        List<String> list = new ArrayList<String>();
                        List<String> idList = new ArrayList<>();
                        String schoolName = "";
                        String schoolID = "";

                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                schoolName = arr.getJSONObject(i).getString("School_Name");
                                schoolID = arr.getJSONObject(i).getString("School_ID");
                                list.add(schoolName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String[] schoolArray = list.toArray(new String[list.size()]);
                        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, schoolArray);
                        schoolNameSpinner.setAdapter(adapter);
                    }

                    else {
                        progressDialog.hide();
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
}
