package com.hamzabinamin.schoolsliveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class AccountActivity extends AppCompatActivity implements View.OnClickListener {

    Button backButton;
    Button nextButton;
    TextView phoneNumberTextView;
    EditText nameEditText;
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
            setContentView(R.layout.activity_account_small);
        else if (screenInches >= 4)
            setContentView(R.layout.activity_account);

        backButton = (Button) findViewById(R.id.backButton);
        nextButton = (Button) findViewById(R.id.nextButton);
        phoneNumberTextView = (TextView) findViewById(R.id.phoneNumberTextView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        progressDialog = new ProgressDialog(this);

        backButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

        if(getIntent().getExtras() != null) {
            if(getIntent().getExtras().getString("Phone Number") != null) {
                phoneNumberTextView.setText(getIntent().getExtras().getString("Phone Number"));
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backButton:
                finish();
                startActivity(new Intent(getBaseContext(), FacebookVerificationActivity.class));
                break;

            case R.id.nextButton:
                if(phoneNumberTextView.getText().toString().length() > 0 && nameEditText.getText().toString().length() > 0) {
                    try {
                        String name = nameEditText.getText().toString();
                        name = URLEncoder.encode(name, "UTF-8");
                        String phoneNumber = phoneNumberTextView.getText().toString();
                        phoneNumber = URLEncoder.encode(phoneNumber, "UTF-8");
                        progressDialog.setMessage("Please Wait");
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        String url = String.format("http://schools-live.com/insertUser.php?name=%s&phonenumber=%s", name, phoneNumber);
                        sendGET(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    nameEditText.setText("Please enter a Name");
                }
                break;
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
                    if(result.contains("New record created successfully")) {
                        progressDialog.dismiss();
                        Toast.makeText(getBaseContext(), "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        User user = new User(nameEditText.getText().toString(), phoneNumberTextView.getText().toString(), "", "0");
                        saveUserSharedPreferences(user);
                        finish();
                        startActivity(new Intent(getBaseContext(), SchoolActivity.class));
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

    public void saveUserSharedPreferences(User user) {
        Gson gson = new Gson();
        String serviceProviderString = gson.toJson(user);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("User String", serviceProviderString).commit();
    }
}
