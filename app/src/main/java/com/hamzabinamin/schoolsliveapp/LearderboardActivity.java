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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.List;

public class LearderboardActivity extends AppCompatActivity implements View.OnClickListener {

    Button thisOrOverallDropDown;
    Button monthOrWeekDropDown;
    TextView schoolTypeTextView;
    TextView schoolNameTextView;
    TextView schoolLocationTextView;
    TextView schoolLinkTextView;
    ImageView imageView;
    ListView listView;
    List arrayList;
    CustomAdapterRanking customAdapter;
    School school;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    ProgressDialog progressDialog;
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
            setContentView(R.layout.activity_learderboard_small);
        else if (screenInches >= 4)
            setContentView(R.layout.activity_learderboard);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

       //thisOrOverallDropDown = (Button) findViewById(R.id.thisOrOverallDropDown);
       //monthOrWeekDropDown = (Button) findViewById(R.id.monthOrWeekDropDown);
        schoolTypeTextView = (TextView) findViewById(R.id.schoolTypeTextView);
        schoolNameTextView = (TextView) findViewById(R.id.schoolNameTextView);
        schoolLocationTextView = (TextView) findViewById(R.id.schoolLocationTextView);
        schoolLinkTextView = (TextView) findViewById(R.id.schoolLinkTextView);
        listView = (ListView) findViewById(R.id.listView);
        arrayList = new ArrayList<Rank>();
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

        View hView = mNavigationView.getHeaderView(0);
        imageView = (ImageView) hView.findViewById(R.id.profile_image);
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //addList();
        //customAdapter = new CustomAdapterRanking(this, arrayList);
       //listView.setAdapter(customAdapter);

        if(getSchoolSharedPreferences()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(school.getSchoolImage(), imageView, options);

            schoolTypeTextView.setText(school.getSchoolType());
            schoolNameTextView.setText(school.getSchoolName());
            schoolLocationTextView.setText(school.getSchoolLocation());
            schoolLinkTextView.setText(school.getSchoolWebsite());

            String url = "http://schools-live.com/getNoOfUpdates.php";
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
        else {
            Toast.makeText(getBaseContext(), "Either add a new School or Select existing one from  Change School", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getBaseContext(), ManageSchoolsActivity.class));
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

  /*  public void addList() {
        arrayList.add(new Rank("Hamza", "29/04/2017", 4321));
        arrayList.add(new Rank("Albert", "21/04/2017", 3321));
        arrayList.add(new Rank("Sherlock", "31/03/2017", 3363));
        arrayList.add(new Rank("Watson", "30/04/2017", 2222));
    } */

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
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                int j=0;
                                String name = arr.getJSONObject(i).getString("Name");
                                String lastUpdateTime = arr.getJSONObject(i).getString("Last_Update_Time");
                                String totalUpdates = arr.getJSONObject(i).getString("Total_Updates");
                                if(totalUpdates.length() == 0) {
                                    arrayList.add(new Rank(++j, name, lastUpdateTime, 0));
                                }
                                else {
                                    arrayList.add(new Rank(++j, name, lastUpdateTime, Integer.parseInt(totalUpdates)));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                       /* for(int i=0; i<schoolNames.size(); i++) {
                            System.out.println("New School: " + schoolNames.get(i));
                        } */

                        customAdapter = new CustomAdapterRanking(getBaseContext(), arrayList);
                        listView.setAdapter(customAdapter);
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

           /* case R.id.thisOrOverallDropDown:
                PopupMenu popup = new PopupMenu(LearderboardActivity.this, thisOrOverallDropDown);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.popupleaderboard, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {


                        return true;
                    }
                });

                popup.show(); //showing popup menu
                break;

            case R.id.monthOrWeekDropDown:

                break; */
        }
    }
}
