package com.hamzabinamin.schoolsliveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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

public class AddGameActivity extends AppCompatActivity implements View.OnClickListener {

    Button backButton;
    Button addGameButton;
    Spinner schoolASpinner;
    Spinner schoolBSpinner;
    Spinner fieldSpinner;
    Spinner sportSpinner;
    Spinner ageGroupSpinner;
    Spinner teamSpinner;
    NumberPicker monthNumberPicker;
    NumberPicker dateNumberPicker;
    NumberPicker yearNumberPicker;
    NumberPicker hourNumberPicker;
    NumberPicker minNumberPicker;
    NumberPicker ampmNumberPicker;
    ProgressDialog progressDialog;
    List<String> schoolIDList = new ArrayList<>();
    List<String> schoolTypeList = new ArrayList<>();
    List<School> schoolList = new ArrayList<School>();
    User user;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";

    private String[] schoolAArray;
    private String[] schoolBArray;
    private String[] fieldArray;
    private String[] sportArray;
    private String[] ageGroupArray;
    private String[] teamArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_game);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        backButton = (Button) findViewById(R.id.backButton);
        addGameButton = (Button) findViewById(R.id.addGameButton);
        schoolASpinner = (Spinner) findViewById(R.id.schoolASpinner);
        schoolBSpinner = (Spinner) findViewById(R.id.schoolBSpinner);
        fieldSpinner = (Spinner) findViewById(R.id.fieldSpinner);
        sportSpinner = (Spinner) findViewById(R.id.sportSpinner);
        ageGroupSpinner = (Spinner) findViewById(R.id.ageGroupSpinner);
        teamSpinner = (Spinner) findViewById(R.id.teamSpinner);
        monthNumberPicker = (NumberPicker) findViewById(R.id.monthNumberPicker);
        dateNumberPicker = (NumberPicker) findViewById(R.id.dateNumberPicker);
        yearNumberPicker = (NumberPicker) findViewById(R.id.yearNumberPicker);
        hourNumberPicker = (NumberPicker) findViewById(R.id.hourNumberPicker);
        minNumberPicker = (NumberPicker) findViewById(R.id.minNumberPicker);
        ampmNumberPicker = (NumberPicker) findViewById(R.id.ampmNumberPicker);
        progressDialog = new ProgressDialog(this);

        backButton.setOnClickListener(this);
        addGameButton.setOnClickListener(this);

        schoolAArray = new String[]{ "Select School" };
        schoolBArray = new String[]{ "Select School" };
        fieldArray = new String[]{ "School A", "School B" };
        sportArray = new String[]{ "Basketball", "Rugby", "Cricket", "Hockey", "Netball", "Soccer", "Water polo" };
        ageGroupArray = new String[]{"U/6", "U/7", "U/8", "U/9", "U/10", "U/11", "U/12", "U/13", "U/14", "U/15", "U/16", "U/17", "U/18", "U/19"};
        teamArray = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, schoolAArray);
        schoolASpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, schoolBArray);
        schoolBSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, fieldArray);
        fieldSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, sportArray);
        sportSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                R.layout.spinner, ageGroupArray);
        ageGroupSpinner.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                R.layout.spinner, teamArray);
        teamSpinner.setAdapter(adapter);

        monthNumberPicker.setMinValue(1);
        monthNumberPicker.setMaxValue(12);

        dateNumberPicker.setMinValue(1);
        dateNumberPicker.setMaxValue(31);

        yearNumberPicker.setMinValue(2017);
        yearNumberPicker.setMaxValue(3000);

        hourNumberPicker.setMinValue(0);
        hourNumberPicker.setMaxValue(12);

        minNumberPicker.setMinValue(0);
        minNumberPicker.setMaxValue(60);

        minNumberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });

        ampmNumberPicker.setMaxValue(0);
        ampmNumberPicker.setMaxValue(1);
        ampmNumberPicker.setDisplayedValues(new String[]{"AM", "PM"});

        AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.BLUE);
                ((TextView) parent.getChildAt(0)).setTextSize(5);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };

        String url = String.format("http://schools-live.com/getSchools.php");
        try {
            progressDialog.setMessage("Please Wait");
            progressDialog.show();
            sendGET(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(!getUserSharedPreferences()) {
            Toast.makeText(getBaseContext(), "Please Login First", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getBaseContext(), LogInActivity.class));
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
                        String schoolID = "";
                        String schoolName = "";
                        String schoolType = "";
                        String schoolWebsite = "";
                        String schoolTwitter = "";
                        String schoolFacebook = "";
                        String schoolLocation = "";
                        String schoolLogo = "";

                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                schoolID = arr.getJSONObject(i).getString("School_ID");
                                schoolName = arr.getJSONObject(i).getString("School_Name");
                                schoolType = arr.getJSONObject(i).getString("School_Type");
                                schoolWebsite = arr.getJSONObject(i).getString("School_Website");
                                schoolTwitter = arr.getJSONObject(i).getString("School_Twitter");
                                schoolFacebook = arr.getJSONObject(i).getString("School_Facebook");
                                schoolLocation = arr.getJSONObject(i).getString("School_Location");
                                schoolLogo = arr.getJSONObject(i).getString("School_Logo");
                                System.out.println("School Image Logo: " + schoolLogo);
                                schoolList.add(new School(schoolName, schoolType, schoolWebsite, schoolTwitter, schoolFacebook, schoolLocation, schoolLogo));
                                list.add(schoolName);
                                schoolTypeList.add(schoolType);
                                schoolIDList.add(schoolID);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        String[] schoolArray = list.toArray(new String[list.size()]);
                        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, schoolArray);
                        schoolASpinner.setAdapter(adapter);
                        schoolBSpinner.setAdapter(adapter);
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

    public void sendGETAddGame(String paramURL) throws IOException {

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
                        progressDialog.hide();
                        Toast.makeText(getBaseContext(), "New Game Created Successfully", Toast.LENGTH_SHORT).show();

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

    public static String GetUTCdatetimeAsString() {
        final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
        final String DATEFORMAT2 = "M-d-yyyy / hh:mm a";
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT2);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backButton:
                finish();
                startActivity(new Intent(getBaseContext(), SchoolActivity.class));
                break;

            case R.id.addGameButton:
                if(validation()) {
                    String schoolA =  schoolASpinner.getSelectedItem().toString();
                    String schoolB =  schoolBSpinner.getSelectedItem().toString();
                   String schoolsType = "";
                    schoolsType = schoolTypeList.get(schoolASpinner.getSelectedItemPosition()) + "/" + schoolTypeList.get(schoolBSpinner.getSelectedItemPosition());
                    String field = fieldSpinner.getSelectedItem().toString();
                    String sport = sportSpinner.getSelectedItem().toString();
                    String agegroup = ageGroupSpinner.getSelectedItem().toString();
                    String team = teamSpinner.getSelectedItem().toString();
                    String ampm = "";
                    if(ampmNumberPicker.getValue() == 0) {
                        ampm = "AM";
                    }
                    else if(ampmNumberPicker.getValue() == 1) {
                        ampm = "PM";
                    }
                    String starttime = String.valueOf(monthNumberPicker.getValue()) + "-" + String.valueOf(dateNumberPicker.getValue()) + "-" + String.valueOf(yearNumberPicker.getValue()) + " / " + String.valueOf(hourNumberPicker.getValue()) + ":" + String.valueOf(String.format("%02d", minNumberPicker.getValue())) + " " + ampm;
                    String weather = "";
                    String temperature = "";
                    String status = "";
                    String score = "";
                    String updateBy = "";
                    String updateTime = "";
                    String homeSchoolURL = "";
                    String awaySchoolURL = "";

                    for(int i=0; i<schoolList.size(); i++) {
                        System.out.println("School Name: " + schoolList.get(i).getSchoolName());
                        System.out.println("School Image: " + schoolList.get(i).getSchoolImage());
                    }

                    try {
                        if(getSchoolItem(schoolList, schoolA) != null) {
                            homeSchoolURL = getSchoolItem(schoolList, schoolA).getSchoolImage();
                        }
                        if(getSchoolItem(schoolList, schoolB) != null) {
                            awaySchoolURL = getSchoolItem(schoolList, schoolB).getSchoolImage();
                        }

                        schoolA = URLEncoder.encode(schoolA, "UTF-8");
                        schoolB = URLEncoder.encode(schoolB, "UTF-8");
                        schoolsType = URLEncoder.encode(schoolsType, "UTF-8");
                        field = URLEncoder.encode(field, "UTF-8");
                        sport = URLEncoder.encode(sport, "UTF-8");
                        agegroup = URLEncoder.encode(agegroup, "UTF-8");
                        team = URLEncoder.encode(team, "UTF-8");
                        starttime = URLEncoder.encode(starttime, "UTF-8");
                        weather = URLEncoder.encode("Sunshine", "UTF-8");
                        temperature = URLEncoder.encode("25°C", "UTF-8");
                        status = URLEncoder.encode("NOT STARTED", "UTF-8");
                        if(!sport.equals("Cricket")) {
                            score = URLEncoder.encode("0-0", "UTF-8");
                        }
                        else {
                            score = URLEncoder.encode("0/0", "UTF-8");
                        }
                       updateBy = URLEncoder.encode(user.getName(), "UTF-8");
                       updateTime = URLEncoder.encode(GetUTCdatetimeAsString() , "UTF-8");
                       homeSchoolURL = homeSchoolURL.replace("http", "");
                       awaySchoolURL = awaySchoolURL.replace("http", "");
                       homeSchoolURL = URLEncoder.encode(homeSchoolURL, "UTF-8");
                       awaySchoolURL = URLEncoder.encode(awaySchoolURL, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String url = String.format("http://www.schools-live.com/insertGame.php?homeschool=%s&awayschool=%s&schoolstype=%s&field=%s&sport=%s&agegroup=%s&team=%s&starttime=%s&weather=%s&temperature=%s&status=%s&score=%s&updateby=%s&updatetime=%s&homeschoollogo=%s&awayschoollogo=%s", schoolA, schoolB, schoolsType, field, sport, agegroup, team, starttime, weather, temperature, status, score, updateBy, updateTime, homeSchoolURL, awaySchoolURL);

                    try {
                        progressDialog.setMessage("Please Wait");
                        progressDialog.show();
                        sendGETAddGame(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public String convertStringIntoUTC(String string) {
        try {
             Calendar calendar = Calendar.getInstance();
             calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
             SimpleDateFormat sdf = new SimpleDateFormat("M-d-yyyy / hh:mm a");
             calendar.setTime(sdf.parse(string));
             String dateAsString = sdf.format(calendar.getTime());
             return dateAsString;
            } catch (ParseException e) {
            e.printStackTrace();
         }
        return null;
    }

    public School getSchoolItem(List<School> schoolList, String name) {

        for(int i=0; i<schoolList.size(); i++) {

            if(schoolList.get(i).getSchoolName().equals(name)) {
                System.out.println("Got Inside If");
                System.out.println("String Name Inside If: " + name);
                System.out.println("School Name Inside If: " + schoolList.get(i).getSchoolName());
                System.out.println("School URL Inside If: " + schoolList.get(i).getSchoolImage());
                return schoolList.get(i);
            }
            else {
                System.out.println("Got Inside Else");
            }
        }
        return null;
    }

    public boolean validation() {

        String schoolA = schoolASpinner.getSelectedItem().toString();
        String schoolB = schoolBSpinner.getSelectedItem().toString();
        String field = fieldSpinner.getSelectedItem().toString();
        String sport = sportSpinner.getSelectedItem().toString();
        String agegroup = ageGroupSpinner.getSelectedItem().toString();
        String team = teamSpinner.getSelectedItem().toString();

        if(schoolA.length() > 0 && schoolB.length() > 0 && field.length() > 0 && sport.length() > 0 && agegroup.length() > 0 && team.length()> 0) {
            return true;
        }
        else {
            return false;
        }
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

}