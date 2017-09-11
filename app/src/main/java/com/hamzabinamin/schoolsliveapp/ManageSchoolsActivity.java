package com.hamzabinamin.schoolsliveapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ManageSchoolsActivity extends AppCompatActivity implements View.OnClickListener {

    Button addSchoolButton;
    Button editSchoolButton;
    Button changeSchoolButton;
    Button gamesButton;
    ImageView imageView;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    School school;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schools);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        addSchoolButton = (Button) findViewById(R.id.addSchoolButton);
        editSchoolButton = (Button) findViewById(R.id.editSchoolButton);
        changeSchoolButton = (Button) findViewById(R.id.changeSchoolButton);
        gamesButton = (Button) findViewById(R.id.gamesButton);

        addSchoolButton.setOnClickListener(this);
        editSchoolButton.setOnClickListener(this);
        changeSchoolButton.setOnClickListener(this);
        gamesButton.setOnClickListener(this);

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
                        // startActivity(new Intent(getBaseContext(), HistoryActivity.class));
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

        if(getSchoolSharedPreferences()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(android.R.drawable.arrow_down_float)
                    .showImageOnFail(android.R.drawable.ic_menu_report_image)
                    .showImageOnLoading(android.R.drawable.arrow_up_float).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.addSchoolButton:
                finish();
                startActivity(new Intent(getBaseContext(), AddSchoolActivity.class));
                break;

            case R.id.editSchoolButton:
                finish();
                startActivity(new Intent(getBaseContext(), EditSelectSchoolActivity.class));
                break;

            case R.id.changeSchoolButton:
                finish();
                startActivity(new Intent(getBaseContext(), ChangeSchoolActivity.class));
                break;

            case R.id.gamesButton:
                if(getSchoolSharedPreferences()) {
                    finish();
                    startActivity(new Intent(getBaseContext(), SchoolActivity.class));
                }
                else {
                    Toast.makeText(getBaseContext(), "Either add a new School or Select existing one from  Change School", Toast.LENGTH_SHORT).show();
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
}
