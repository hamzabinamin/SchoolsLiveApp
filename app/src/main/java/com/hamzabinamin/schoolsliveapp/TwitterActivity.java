package com.hamzabinamin.schoolsliveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwitterActivity extends AppCompatActivity implements View.OnClickListener {

    private WebView mWebView;
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
    ProgressDialog progressDialog;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    School school;

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
            setContentView(R.layout.activity_twitter_small);
        else if (screenInches >= 4)
            setContentView(R.layout.activity_twitter);

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
        twitterTextView.setTextColor(getResources().getColor(R.color.colorRed));
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fixturesTextView.setOnClickListener(this);
        resultsTextView.setOnClickListener(this);
        facebookTextView.setOnClickListener(this);
        twitterTextView.setOnClickListener(this);

        if(getSchoolSharedPreferences()) {
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

        String mUrl = "https://" + getIntent().getStringExtra("mUrl");

        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebViewClient(new MyAppWebViewClient());

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

            case R.id.liveNowTextView:
                finish();
                startActivity(new Intent(getBaseContext(), LiveNowActivity.class));
                break;
        }
    }

    public class MyAppWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            progressDialog.setMessage("Please wait");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, final String url) {
            progressDialog.dismiss();
        }
    }
}
