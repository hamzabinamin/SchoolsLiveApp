package com.hamzabinamin.schoolsliveapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UpdateGameActivity extends AppCompatActivity implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    Button updateGameButton;
    Button updateWeatherButton;
    Button submitButton;
    TextView schoolTypeTextView;
    TextView schoolNameTextView;
    TextView schoolType1TextView;
    TextView schoolName1TextView;
    TextView schoolType2TextView;
    TextView schoolName2TextView;
    TextView schoolLocationTextView;
    TextView schoolLinkTextView;
    TextView temperatureTextView;
    TextView timeTextView;
    TextView teamNameTextView;
    TextView weather2TextView;
    TextView scoreTextView;
    TextView currentOverTextView;
    TextView batbowl2TextView;
    TextView batbowlTextView;
    TextView statusTextView;
    TextView lastUpdateByTextView;
    TextView lastUpdateTimeTextView;
    EditText chatEditText;
    ImageView weatherImageView;
    ImageView imageView;
    ImageView school1Logo;
    ImageView school2Logo;
    ImageView uploadImageView;
    ListView listView;
    CustomAdapterChat customAdapterChat;
    List<Chat> chatList = new ArrayList<Chat>();
    List<Game> notificationGameList = new ArrayList<>();
    ProgressDialog progressDialog;
    School school;
    Game game;
    String overs;
    String batting;
    String bowling;
    String utcTime;
    User user;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";

    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;

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
            setContentView(R.layout.activity_update_game_small);
        else if (screenInches > 4)
            setContentView(R.layout.activity_update_game);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        updateGameButton = (Button) findViewById(R.id.updateGameButton);
        updateWeatherButton = (Button) findViewById(R.id.updateWeatherButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        schoolTypeTextView = (TextView) findViewById(R.id.schoolTypeTextView);
        schoolNameTextView = (TextView) findViewById(R.id.schoolNameTextView);
        schoolType1TextView = (TextView) findViewById(R.id.schoolType1TextView);
        schoolName1TextView = (TextView) findViewById(R.id.schoolName1TextView);
        schoolType2TextView = (TextView) findViewById(R.id.schoolType2TextView);
        schoolName2TextView = (TextView) findViewById(R.id.schoolName2TextView);
        schoolLocationTextView = (TextView) findViewById(R.id.schoolLocationTextView);
        schoolLinkTextView = (TextView) findViewById(R.id.schoolLinkTextView);
        teamNameTextView = (TextView) findViewById(R.id.teamNameTextView);
        temperatureTextView = (TextView) findViewById(R.id.temperature1TextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        weather2TextView = (TextView) findViewById(R.id.weather2TextView);
        scoreTextView = (TextView) findViewById(R.id.scoreTextView);
        currentOverTextView = (TextView) findViewById(R.id.currentOverTextView);
        batbowl2TextView = (TextView) findViewById(R.id.batbowl2TextView);
        batbowlTextView = (TextView) findViewById(R.id.batbowlTextView);
        statusTextView = (TextView) findViewById(R.id.matchStatusTextView);
        lastUpdateByTextView = (TextView) findViewById(R.id.lastUpdateTextView);
        lastUpdateTimeTextView = (TextView) findViewById(R.id.lastUpdateTimeTextView);
        chatEditText = (EditText) findViewById(R.id.chatEditText);
        weatherImageView = (ImageView) findViewById(R.id.weatherImageView);
        school1Logo = (ImageView) findViewById(R.id.school1Logo);
        school2Logo = (ImageView) findViewById(R.id.school2Logo);
        uploadImageView = (ImageView) findViewById(R.id.uploadImageView);
        listView = (ListView) findViewById(R.id.chatListView);
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

        mNavigationView.setItemIconTintList(null);
        View hView = mNavigationView.getHeaderView(0);
        imageView = (ImageView) hView.findViewById(R.id.profile_image);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        updateGameButton.setOnClickListener(this);
        updateWeatherButton.setOnClickListener(this);
        statusTextView.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        chatEditText.setOnClickListener(this);
        uploadImageView.setOnClickListener(this);

        chatEditText.setFocusable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSchoolSharedPreferences()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(school.getSchoolImage(), imageView, options);

            if (getGameSharedPreferences() && getSchoolSharedPreferences()) {

                if(game.getStatus().equals("ENDED")) {
                    statusTextView.setEnabled(false);
                    updateWeatherButton.setEnabled(false);
                }

                schoolTypeTextView.setText(school.getSchoolType());
                schoolNameTextView.setText(school.getSchoolName());
                schoolLocationTextView.setText(school.getSchoolLocation());
                schoolLinkTextView.setText(school.getSchoolWebsite());

                String[] splitArray = game.getSchoolsType().split("/");
                schoolType1TextView.setText(splitArray[0]);
                schoolName1TextView.setText(game.getHomeSchoolName());
                schoolType2TextView.setText(splitArray[1]);
                schoolName2TextView.setText(game.getAwaySchoolName());

                String category = "";
                if(game.getCategory().trim().equals("Boys")) {
                    category = "(B)";
                }
                else if(game.getCategory().trim().equals("Girls")) {
                    category = "(G)";
                }


                teamNameTextView.setText(category + " " + game.getSport() + " " + game.getAgeGroup() + "/" + game.getTeam());
                temperatureTextView.setText(game.getTemperature());
                timeTextView.setText(convertUTCTimeInToLocal(game.getStartTime()));
                weather2TextView.setText(game.getWeather());
                String[] stringArray = game.getScore().split("/");
                if(stringArray.length == 5) {
                    currentOverTextView.setText(" C.O. : " + stringArray[4]);
                    scoreTextView.setText(stringArray[0].trim() + "/" + stringArray[1].trim());
                    if (schoolName1TextView.getText().toString().equals(stringArray[2])) {
                        batbowl2TextView.setText("(Bat)");
                        batbowlTextView.setText("(Bowl)");
                    } else {
                        batbowl2TextView.setText("(Bowl)");
                        batbowlTextView.setText("(Bat)");
                    }
                }
                else {
                    scoreTextView.setText(game.getScore());
                    batbowlTextView.setVisibility(View.INVISIBLE);
                    batbowl2TextView.setVisibility(View.INVISIBLE);
                    currentOverTextView.setVisibility(View.INVISIBLE);
                }

                if(game.getStatus().equals("ENDED") && game.getSport().equals("Cricket")) {
                    String store = game.getWhoWon();
                    if(store.equals(schoolName1TextView.getText().toString())) {
                        batbowl2TextView.setText("WON");
                        batbowlTextView.setText("LOST");
                    }
                    else {
                        batbowlTextView.setText("WON");
                        batbowl2TextView.setText("LOST");
                    }
                }

                //scoreTextView.setText(game.getScore());
                statusTextView.setText(game.getStatus());
                lastUpdateTimeTextView.setText(convertUTCTimeInToLocal(game.getLastUpdateTime()));
                lastUpdateByTextView.setText(game.getLastUpdateBy());

                if (game.getWeather().equals("Rainy")) {
                    weatherImageView.setImageResource(R.drawable.rainy);
                    weather2TextView.setText("Rainy");
                } else if (game.getWeather().equals("Cloudy")) {
                    weatherImageView.setImageResource(R.drawable.cloudy);
                    weather2TextView.setText("Cloudy");
                } else if (game.getWeather().equals("Partly Cloudy")) {
                    weatherImageView.setImageResource(R.drawable.partlycloudy);
                    weather2TextView.setText("Partly Cloudy");
                } else if (game.getWeather().equals("Sunshine")) {
                    weatherImageView.setImageResource(R.drawable.sunny);
                    weather2TextView.setText("Sunshine");
                } else if (game.getWeather().equals("Strong Wind")) {
                    weatherImageView.setImageResource(R.drawable.windy);
                    weather2TextView.setText("Wind");
                }

                options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisk(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(R.drawable.placeholder)
                        .showImageOnFail(R.drawable.placeholder)
                        .showImageOnLoading(R.drawable.placeholder).build();
                imageLoader = ImageLoader.getInstance();
                ImageLoader imageLoader2 = ImageLoader.getInstance();
                imageLoader.displayImage("http" + game.getHomeSchoolImageURL(), school1Logo, options);
                imageLoader2.displayImage("http" + game.getAwaySchoolImageURL(), school2Logo, options);
            }

            if (getUserSharedPreferences()) {
                lastUpdateByTextView.setText(game.getLastUpdateBy());
            } else {
                Toast.makeText(getBaseContext(), "Please Login First", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getBaseContext(), LogInActivity.class));
            }
        }
        else {
            Toast.makeText(getBaseContext(), "Please Login First", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getBaseContext(), LogInActivity.class));
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

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setMessage("Please Wait");
        progressDialog.show();
        try {
            String gameid = game.getGameID();
            gameid = URLEncoder.encode(gameid, "UTF-8");
            String url = String.format("http://schools-live.com/getMessages.php?gameid=%s", gameid);
            sendGETPopulateListView(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.updateGameButton:
                if(!statusTextView.getText().toString().equals("HALF TIME")) {
                    if(!statusTextView.getText().toString().equals("NOT STARTED")) {
                        if (game.getSport().equals("Cricket")) {
                            if(!statusTextView.getText().toString().equals("ENDED")) {
                                numberPickerDialogScoreCricket();
                            }
                            else {
                                numberPickerDialogScoreEndedCricket();
                            }
                        } else {
                            numberPickerDialogScore();
                        }
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Please update the Game Status to 1st Half before updating Score", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "You can't update score during Half Time", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.updateWeatherButton:
                numberPickerDialog();
                break;

            case R.id.matchStatusTextView:
                numberPickerDialogGameStatus();
                break;

            case R.id.submitButton:
                if(chatEditText.getText().toString().length() > 0) {
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    try {
                        String phone = user.getPhoneNumber();
                        phone = URLEncoder.encode(phone, "UTF-8");
                        String name = user.getName();
                        name = URLEncoder.encode(name, "UTF-8");
                        String message = chatEditText.getText().toString();
                        System.out.println("Message Before: " + message);
                        message = message.replaceAll("'","\\\\'");;
                        System.out.println("Message After: " + message);
                        message = URLEncoder.encode(message, "UTF-8");
                        String time = getLocalTime();
                        time = URLEncoder.encode(time, "UTF-8");
                        String gameid = game.getGameID();
                        gameid = URLEncoder.encode(gameid, "UTF-8");
                        String url = String.format("http://schools-live.com/insertMessage.php?phone=%s&name=%s&message=%s&time=%s&gameid=%s", phone, name, message, time, gameid);
                        sendGETInsertChat(url);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(), "Please type a message first", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.chatEditText:
                chatEditText.setFocusableInTouchMode(true);
                chatEditText.requestFocus();
                break;

            case R.id.uploadImageView:
                //String playstoreLink = "<a href=\"https://play.google.com/store/apps/details?id=com.hamzabinamin.schoolsliveapp\">Playstore Link</a>";
               // String playstoreLink = "https://play.google.com/store/apps/details?id=com.hamzabinamin.schoolsliveapp";
              //  String playstoreLink = "";

                if(!game.getSport().equals("Cricket")) {

                    showShareDialog(game.getCategory() + " " + game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + game.getStatus() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("-")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("-")[1] + " via @SchoolsLive ");
                }
                else {
                    if(game.getScore().split("/").length == 5) {

                        String text = game.getCategory() + " " + game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getScore().split("/")[2] + " " + "(Bat)" + "-" + " " + game.getScore().split("/")[3] + " " + "(Bowl)" + "-" + " " + game.getScore().split("/")[0].trim() + "/" + game.getScore().split("/")[1].trim() + " " +  " " + "Overs: " + game.getScore().split("/")[4] + " via @SchoolsLive ";
                        showShareDialog(text);
                    }
                    else {
                        String text = game.getCategory() + " " + game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getHomeSchoolName() + " " + game.getScore().split("/")[0] + " " + "-" + " " + game.getAwaySchoolName() + " " + game.getScore().split("/")[1] + " via @SchoolsLive ";
                        showShareDialog(text);
                    }
                }
                break;

        }
    }

    public void showShareDialog(final String shareText) {
        final CharSequence[] optionSequence1 = {"Facebook", "Others"};
        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateGameActivity.this);
        builder.setTitle("Share").setSingleChoiceItems(optionSequence1, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String playstoreLink = "http://bit.ly/2izUpjm";

                switch (which) {

                    case 0:
                        shareGame(shareText, "Facebook");
                        dialog.dismiss();
                        break;

                    case 1:
                        shareGame(shareText  + playstoreLink, "Others");
                        dialog.dismiss();
                        break;

                    default:
                        break;
                }

            }
        }).setNegativeButton("Cancel", null).setCancelable(false);

        AlertDialog alertDialog1 = builder.create();
        alertDialog1.show();
    }

    public void shareGame(String shareText, String choice) {
        if(choice.equals("Others")) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            List<Intent> shareIntentsLists = new ArrayList<Intent>();

            List<ResolveInfo> resInfos = getPackageManager().queryIntentActivities(shareIntent, 0);
            if (!resInfos.isEmpty()) {
                System.out.println("Inside If");
                for (ResolveInfo resInfo : resInfos) {
                    String packageName = resInfo.activityInfo.packageName;
                    System.out.println("Inside For");
                    if (!packageName.toLowerCase().contains("facebook")) {
                        System.out.println("Inside Facebook If");
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, shareText);
                        intent.setType("text/plain");
                        intent.setPackage(packageName);
                        shareIntentsLists.add(intent);
                    }
                }
                if (!shareIntentsLists.isEmpty()) {
                    System.out.println("Inside Second If");
                    Intent chooserIntent = Intent.createChooser(shareIntentsLists.remove(0), "Choose app to share");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, shareIntentsLists.toArray(new Parcelable[]{}));
                    startActivity(chooserIntent);
                } else {
                    Toast.makeText(getBaseContext(), "No App can perform this action", Toast.LENGTH_SHORT).show();
                    Log.e("Error", "No App can perform your task");
                }
            }

          /*  Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            startActivity(shareIntent); */
        }
        else if(choice.equals("Facebook")) {
            FacebookSdk.sdkInitialize(getApplicationContext());
            ShareDialog shareDialog;
            ShareLinkContent shareLinkContent;
            shareDialog = new ShareDialog(this);
            if (ShareDialog.canShow(ShareLinkContent.class)) {
                shareLinkContent = new ShareLinkContent.Builder()
                        .setQuote(shareText)
                        //   .setImageUrl(Uri.parse("https://lh3.googleusercontent.com/jUej7mN6M6iulmuwmW6Mk28PrzXgl-Ebn-MpTmkmwtOfj5f0hvnuw8j0NEzK0GuKoDE=w300-rw"))
                        //.setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.hamzabinamin.schoolsliveapp"))
                        .setContentUrl(Uri.parse("http://bit.ly/2izUpjm"))
                        .build();

                shareDialog.show(shareLinkContent);
            }
        }
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

    public static String GetUTCdatetimeAsString() {
        final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
        final String DATEFORMAT2 = "d-M-yyyy / hh:mm a";
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT2);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public Date getCurrentTime() throws ParseException {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd-MM-yyyy / HH:mm");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("dd-MM-yyyy / HH:mm");

        return dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
    }
    
    public String getLocalTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-yyyy / hh:mm a");
        Calendar calendar = Calendar.getInstance();
        String time = sdf.format(calendar.getTime());
        return time;
    }

    public void updateGame() {
        String id = game.getGameID();
        String weather = weather2TextView.getText().toString();
        String temperature = temperatureTextView.getText().toString();
        String status = statusTextView.getText().toString();
        String score = "";
        if(game.getSport().equals("Cricket")) {
            score = scoreTextView.getText().toString() + "/" + batting + "/" + bowling + "/" + overs;
            game.setScore(score);
            saveGameSharedPreferences(game);
        }
        else {
            score = scoreTextView.getText().toString();
            game.setScore(score);
            saveGameSharedPreferences(game);
        }
        String updateBy = user.getName();
        String updateTime = utcTime;
        try {
            weather = URLEncoder.encode(weather, "UTF-8");
            temperature = URLEncoder.encode(temperature, "UTF-8");
            status = URLEncoder.encode(status, "UTF-8");
            score = URLEncoder.encode(score, "UTF-8");
            updateBy = URLEncoder.encode(updateBy, "UTF-8");
            updateTime = URLEncoder.encode(updateTime, "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String url = String.format("http://www.schools-live.com/updateGame.php?id=%s&weather=%s&temperature=%s&status=%s&score=%s&lastupdate=%s&lastupdatetime=%s", id, weather, temperature, status, score, updateBy, updateTime);

        try {
            progressDialog.setMessage("Please Wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            sendGETUpdateGame(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void numberPickerDialog() {
        final Dialog d = new Dialog(UpdateGameActivity.this);
        d.setTitle("Update Weather");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final String[] weatherArray = new String[] { "Rainy", "Cloudy", "Partly Cloudy", "Sunshine", "Strong Wind" };
        np.setMaxValue(4);
        np.setMinValue(0);
        np.setDisplayedValues(weatherArray);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        System.out.println("Game Weather: " + game.getWeather());

        if(game.getWeather().equals("Rainy")) {
            np.setValue(0);
        }
        else if(game.getWeather().equals("Cloudy")) {
            np.setValue(1);
        }
        else if(game.getWeather().equals("Partly Cloudy")) {
            np.setValue(2);
        }
        else if(game.getWeather().equals("Sunshine")) {
            np.setValue(3);
        }
        else if(game.getWeather().equals("Strong Wind")) {
            np.setValue(4);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(np.getValue() == 0) {
                    weatherImageView.setImageResource(R.drawable.rainy);
                    weather2TextView.setText(weatherArray[0]);
                }
                else if(np.getValue() == 1) {
                    weatherImageView.setImageResource(R.drawable.cloudy);
                    weather2TextView.setText(weatherArray[1]);
                }
                else if(np.getValue() == 2) {
                    weatherImageView.setImageResource(R.drawable.partlycloudy);
                    weather2TextView.setText(weatherArray[2]);
                }
                else if(np.getValue() == 3) {
                    weatherImageView.setImageResource(R.drawable.sunny);
                    weather2TextView.setText(weatherArray[3]);
                }
                else if(np.getValue() == 4) {
                    weatherImageView.setImageResource(R.drawable.windy);
                    weather2TextView.setText(weatherArray[4]);
                }
                game.setWeather(weather2TextView.getText().toString());
                saveGameSharedPreferences(game);
                d.dismiss();
                numberPickerDialog2();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    public void numberPickerDialogGameStatus() {
        final Dialog d = new Dialog(UpdateGameActivity.this);
        d.setTitle("Update Game");
        d.setContentView(R.layout.dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final String[] weatherArray = new String[] { "NOT STARTED", "1st HALF", "HALF TIME" , "2nd HALF", "ENDED"};
        np.setMaxValue(4);
        np.setMinValue(0);
        np.setDisplayedValues(weatherArray);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        if(game.getStatus().equals("NOT STARTED")) {
            np.setValue(0);
        }
        else if(game.getStatus().equals("1st HALF")) {
            np.setValue(1);
        }
        else if(game.getStatus().equals("HALF TIME")) {
            np.setValue(2);
        }
        else if(game.getStatus().equals("2nd HALF")) {
            np.setValue(3);
        }
        else if(game.getStatus().equals("ENDED")) {
            np.setValue(4);
        }

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(np.getValue() == 0) {
                statusTextView.setText(weatherArray[0]);
                }
                else if(np.getValue() == 1) {
                    statusTextView.setText(weatherArray[1]);
                }
                else if(np.getValue() == 2) {
                    statusTextView.setText(weatherArray[2]);
                }
                else if(np.getValue() == 3) {
                    statusTextView.setText(weatherArray[3]);
                }
                else if(np.getValue() == 4) {
                    statusTextView.setText(weatherArray[4]);
                }

                try {
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    utcTime = GetUTCdatetimeAsString();
                    lastUpdateTimeTextView.setText(convertUTCTimeInToLocal(GetUTCdatetimeAsString()));
                    String status = statusTextView.getText().toString();
                    game.setStatus(status);
                    saveGameSharedPreferences(game);
                    status = URLEncoder.encode(status, "UTF-8");
                    String updateBy = URLEncoder.encode(user.getName(), "UTF-8");
                    String updateTime = URLEncoder.encode(utcTime, "UTF-8");
                    String url = String.format("http://www.schools-live.com/updateGameStatus.php?id=%s&status=%s&updateby=%s&updatetime=%s", game.getGameID(), status,updateBy, updateTime);
                    sendGETUpdateGame(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                d.dismiss();

            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    public void numberPickerDialog2() {

        final Dialog d = new Dialog(UpdateGameActivity.this);
        d.setTitle("Update Temperature");
        d.setContentView(R.layout.temperature_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);
        String[] displayedArray = new String[] {"+", "-"};

        np.setMaxValue(1);
        np.setMinValue(0);
        np.setDisplayedValues(displayedArray);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        np2.setMaxValue(50);
        np2.setMinValue(0);
        String store = "";

        System.out.println("Game Temp: " + game.getTemperature());

        if(game.getTemperature().contains("+")) {
            np.setValue(0);
            store = game.getTemperature().replace("+", "");
            store = store.replace("°C", "");
            np2.setValue(Integer.parseInt(store));
        }
        else {
            np.setValue(1);
            store = game.getTemperature().replace("-", "");
            store = store.replace("°C", "");
            np2.setValue(Integer.parseInt(store));
        }



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(np.getValue() == 0) {
                    temperatureTextView.setText("+" + np2.getValue()+"°C");
                }
                else if(np.getValue() == 1) {
                    temperatureTextView.setText("-" + np2.getValue()+"°C");
                }
                game.setTemperature(temperatureTextView.getText().toString());
                saveGameSharedPreferences(game);
                lastUpdateTimeTextView.setText(GetUTCdatetimeAsString());
                utcTime = GetUTCdatetimeAsString();
                lastUpdateTimeTextView.setText(convertUTCTimeInToLocal(GetUTCdatetimeAsString()));
                try {
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    String updateBy = URLEncoder.encode(user.getName(), "UTF-8");
                    String updateTime = URLEncoder.encode(utcTime, "UTF-8");
                    String weather = URLEncoder.encode(weather2TextView.getText().toString(), "UTF-8");
                    String temperature = URLEncoder.encode(temperatureTextView.getText().toString(), "UTF-8");
                    String url = String.format("http://www.schools-live.com/updateGameWeatherAndTemp.php?id=%s&weather=%s&temp=%s&updateby=%s&updatetime=%s", game.getGameID(), weather, temperature, updateBy, updateTime);
                    sendGETUpdateGame(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //updateGame();
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    public void numberPickerDialogScore() {

        final Dialog d = new Dialog(UpdateGameActivity.this);
        d.setTitle("Update Score");
        d.setContentView(R.layout.score_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        //final Spinner gameStatusSpinner = (Spinner) d.findViewById(R.id.gameStatusSpinner);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        final NumberPicker np2 = (NumberPicker) d.findViewById(R.id.numberPicker2);

        //ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { "NOT STARTED", "1st HALF", "HALF TIME" , "2nd HALF", "ENDED" });
        //gameStatusSpinner.setAdapter(adapter);

        np.setMaxValue(999);
        np.setMinValue(0);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        np2.setMaxValue(999);
        np2.setMinValue(0);
        np.setValue(Integer.parseInt(game.getScore().split("-")[0].trim()));
        np2.setValue(Integer.parseInt(game.getScore().split("-")[1].trim()));
        np2.setWrapSelectorWheel(false);
        np2.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String homeScore = String.valueOf(np.getValue());
                String awayScore = String.valueOf(np2.getValue());
                scoreTextView.setText(homeScore + " - " + awayScore);
                utcTime = GetUTCdatetimeAsString();
                lastUpdateTimeTextView.setText(convertUTCTimeInToLocal(GetUTCdatetimeAsString()));
                //statusTextView.setText(gameStatusSpinner.getSelectedItem().toString());
                updateGame();
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    public void numberPickerDialogScoreCricket() {

        final Dialog d = new Dialog(UpdateGameActivity.this);
        d.setTitle("Update Score");
        d.setContentView(R.layout.cricket_score_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final Spinner currentOverSpinner = (Spinner) d.findViewById(R.id.currentOverSpinner);
        final Spinner wicketsTakenSpinner = (Spinner) d.findViewById(R.id.wicketsTakenSpinner);
        final Spinner battingSpinner = (Spinner) d.findViewById(R.id.battingSpinner);
        final Spinner bowlingSpinner = (Spinner) d.findViewById(R.id.bowlingSpinner);
        //final Spinner gameStatusSpinner = (Spinner) d.findViewById(R.id.gameStatusSpinner);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);

        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37" , "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50"  });
        currentOverSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        wicketsTakenSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { game.getHomeSchoolName(), game.getAwaySchoolName()});
        battingSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { game.getHomeSchoolName(), game.getAwaySchoolName()});
        bowlingSpinner.setAdapter(adapter);

        //adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { "NOT STARTED", "1st HALF", "HALF TIME" , "2nd HALF", "ENDED" });
       // gameStatusSpinner.setAdapter(adapter);

        np.setMaxValue(500);
        np.setMinValue(0);
        if(game.getScore().split("/").length == 5) {
            String homeTeam = game.getScore().split("/")[2].trim();
            if(battingSpinner.getItemAtPosition(0).equals(homeTeam)) {
                battingSpinner.setSelection(0);
                bowlingSpinner.setSelection(1);
            }
            else {
                battingSpinner.setSelection(1);
                bowlingSpinner.setSelection(0);
            }

            String text = game.getSport() + " " + game.getAgeGroup() + game.getTeam() + " " + "-" + " " + game.getScore().split("/")[2] + " " + "(Bat)" + "-" + " " + game.getScore().split("/")[3] + " " + "(Bowl)" + "-" + " " + game.getScore().split("/")[0].trim() + "/" + game.getScore().split("/")[1].trim() + " " +  " " + "Overs: " + game.getScore().split("/")[4];
            np.setValue(Integer.parseInt(game.getScore().split("/")[0].trim()));
            if(game.getScore().split("/")[4].equals(0)) {
                currentOverSpinner.setSelection(0);
            }
            else {
                currentOverSpinner.setSelection(Integer.parseInt(game.getScore().split("/")[4].trim()));
            }
            if(game.getScore().split("/")[1].equals(0)) {
                wicketsTakenSpinner.setSelection(0);
            }
            else {
                wicketsTakenSpinner.setSelection(Integer.parseInt(game.getScore().split("/")[1].trim()));
            }

        }
        else {

        }
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String score = String.valueOf(np.getValue());
                String wickets = String.valueOf(wicketsTakenSpinner.getSelectedItem());
                //String gameStatus = String.valueOf(gameStatusSpinner.getSelectedItem());
                overs = currentOverSpinner.getSelectedItem().toString();
                batting = battingSpinner.getSelectedItem().toString();
                bowling = bowlingSpinner.getSelectedItem().toString();
                currentOverTextView.setVisibility(View.VISIBLE);
                currentOverTextView.setText(" C.O. : " + overs);
                batbowlTextView.setVisibility(View.VISIBLE);
                batbowl2TextView.setVisibility(View.VISIBLE);
                if (schoolName1TextView.getText().toString().equals(batting)) {
                    batbowl2TextView.setText("(Bat)");
                    batbowlTextView.setText("(Bowl)");
                } else {
                    batbowl2TextView.setText("(Bowl)");
                    batbowlTextView.setText("(Bat)");
                }

                scoreTextView.setText(score + "/" + wickets );
                utcTime = GetUTCdatetimeAsString();
                lastUpdateTimeTextView.setText(convertUTCTimeInToLocal(GetUTCdatetimeAsString()));
                //statusTextView.setText(gameStatus);
                updateGame();
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }


    public void numberPickerDialogScoreEndedCricket() {

        final Dialog d = new Dialog(UpdateGameActivity.this);
        d.setTitle("Update Score");
        d.setContentView(R.layout.end_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final Spinner teamSpinner = (Spinner) d.findViewById(R.id.teamSpinner);


        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, new String[] { game.getHomeSchoolName(), game.getAwaySchoolName()});
        teamSpinner.setAdapter(adapter);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utcTime = GetUTCdatetimeAsString();
                lastUpdateTimeTextView.setText(convertUTCTimeInToLocal(GetUTCdatetimeAsString()));
                String score = "";

                if(game.getScore().split("/").length == 5) {
                    String homeTeam = game.getScore().split("/")[2].trim();
                    String oversStore = game.getScore().split("/")[4];
                    if(teamSpinner.getSelectedItem() != null) {
                        game.setWhoWon(teamSpinner.getSelectedItem().toString());
                        score = game.getWhoWon();
                        System.out.println("Won: " + game.getWhoWon());
                        if (game.getWhoWon().equals(schoolName1TextView.getText().toString())) {
                            batbowl2TextView.setText("WON");
                            batbowlTextView.setText("LOST");
                        } else {
                            batbowl2TextView.setText("LOST");
                            batbowlTextView.setText("WON");
                        }

                        try {
                            progressDialog.setMessage("Please Wait");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            utcTime = GetUTCdatetimeAsString();
                            score = URLEncoder.encode(score, "UTF-8");
                            String updateBy = URLEncoder.encode(user.getName(), "UTF-8");
                            String updateTime = URLEncoder.encode(utcTime, "UTF-8");
                            String url = String.format("http://www.schools-live.com/updateEndGameScore.php?id=%s&score=%s&update=%s&updatetime=%s", game.getGameID(), score, updateBy, updateTime);
                            sendGETUpdateGame(url);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



                }
                else {
                    Toast.makeText(getBaseContext(), "No Score to determine who won", Toast.LENGTH_SHORT).show();

                }

                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
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

                        try {
                            String phoneNumber = user.getPhoneNumber();
                            phoneNumber = URLEncoder.encode(phoneNumber, "UTF-8");
                            String lastUpdate = GetUTCdatetimeAsString();
                            lastUpdate = URLEncoder.encode(lastUpdate, "UTF-8");
                            String totalUpdates = user.getTotalUpdates();
                            totalUpdates = String.valueOf(Integer.parseInt(totalUpdates) + 1);
                            user.setLastUpdateTime(lastUpdate);
                            user.setTotalUpdates(totalUpdates);
                            String url = String.format("http://www.schools-live.com/updateUser2.php?phonenumber=%s&lastupdate=%s&totalupdates=%s", phoneNumber, lastUpdate, totalUpdates);
                            progressDialog.setMessage("Please Wait");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setCancelable(false);
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                            sendGETUpdateGame2(url, game);
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

    public void sendGETPopulateListView(String paramURL) throws IOException {

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
                    System.out.println(result);
                    if(result.contains("Got Result")) {
                        progressDialog.dismiss();
                        result = result.replace("Got Result<br>","");
                        chatList = new ArrayList<>();
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                String phone = arr.getJSONObject(i).getString("Phone_Number");
                                String name = arr.getJSONObject(i).getString("Name");
                                String message = arr.getJSONObject(i).getString("Message");
                                String time = arr.getJSONObject(i).getString("Time");

                                chatList.add(new Chat(phone, name, message, time));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                       /* for(int i=0; i<schoolNames.size(); i++) {
                            System.out.println("New School: " + schoolNames.get(i));
                        } */

                        customAdapterChat = new CustomAdapterChat(getBaseContext(), chatList);
                        listView.setAdapter(customAdapterChat);
                        setListViewHeightBasedOnChildren(listView);
                    }
                    else {
                        progressDialog.dismiss();
                     //   Toast.makeText(getBaseContext(), "There was an Error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
    }

    public void sendGETUpdateGame2(String paramURL, final Game game) throws IOException {

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
                        if(game.getStatus().equals("ENDED")) {
                            if (getGameListSharedPreferences()) {
                                if (notificationGameList.indexOf(game) != -1) {
                                    int index = notificationGameList.indexOf(game);
                                    notificationGameList.remove(index);
                                    saveGameListSharedPreferences(notificationGameList);

                                    for(int i=0; i<notificationGameList.size(); i++) {
                                        System.out.println("Game ID: " + notificationGameList.get(i).getGameID());
                                        System.out.println("Game Type: " + notificationGameList.get(i).getSport());
                                        System.out.println("Game Score: " + notificationGameList.get(i).getScore());
                                    }
                                }
                            }
                        }
                        saveUserSharedPreferences(user);
                        lastUpdateByTextView.setText(user.getName());
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Game Updated Successfully", Toast.LENGTH_SHORT).show();
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

    public void sendGETInsertChat(String paramURL) throws IOException {

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
                    if(result.contains("New record created successfully")) {
                        saveUserSharedPreferences(user);
                        chatEditText.setText("");
                        String gameid = game.getGameID();
                        try {
                            gameid = URLEncoder.encode(gameid, "UTF-8");
                            String url = String.format("http://schools-live.com/getMessages.php?gameid=%s", gameid);
                            sendGETPopulateListView(url);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "We couldn't save your message", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        HttpGetAsyncTask httpGetAsyncTask = new HttpGetAsyncTask();
        // Parameter we pass in the execute() method is relate to the first generic type of the AsyncTask
        // We are passing the connectWithHttpGet() method arguments to that
        httpGetAsyncTask.execute(paramURL);
    }

    public void saveGameSharedPreferences(Game game) {
        Gson gson = new Gson();
        String runnerString = gson.toJson(game);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Game", runnerString).commit();
    }

    public boolean getGameSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("Game", null) != null) {
            String runnerString = sharedPreferences.getString("Game", null);
            Gson gson = new Gson();
            TypeToken<Game> token = new TypeToken<Game>() {};
            game = gson.fromJson(runnerString, token.getType());
            return true;
        }
        else
            return false;
    }

    public boolean getUserSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("User String", null) != null) {
            String serviceProviderString = sharedPreferences.getString("User String", null);
            Gson gson = new Gson();
            TypeToken<User> token = new TypeToken<User>() {};
            user = gson.fromJson(serviceProviderString, token.getType());
            return true;
        }
        else
            return false;
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

    public void saveUserSharedPreferences(User user) {
        Gson gson = new Gson();
        String serviceProviderString = gson.toJson(user);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("User String", serviceProviderString).commit();
    }

    public void saveGameListSharedPreferences(List counterList) {
        Gson gson = new Gson();
        String gameString = gson.toJson(counterList);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Notification Game List", gameString).commit();
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

    public void setListViewHeightBasedOnChildren(ListView listView) {
        CustomAdapterChat listAdapter = (CustomAdapterChat) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
