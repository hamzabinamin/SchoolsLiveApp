package com.hamzabinamin.schoolsliveapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Hamza on 7/3/2017.
 */

public class FacebookVerificationActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";
    public static int APP_REQUEST_CODE = 99;
    String phoneNumberString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_verification);

        progressDialog = new ProgressDialog(this);

        AccountKit.initialize(getApplicationContext());
        initAccountKitSmsFlow();
    }

    public void initAccountKitSmsFlow() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        // ... perform additional configuration ...
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    private void getAccount(){
        AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
            @Override
            public void onSuccess(final Account account) {
                String accountKitId = account.getId();
                PhoneNumber phoneNumber = account.getPhoneNumber();
                String phoneNumber2 = phoneNumber.toString();
                phoneNumberString = phoneNumber.toString();
                String email = account.getEmail();

                try {
                    phoneNumber2 = URLEncoder.encode(phoneNumberString, "UTF-8");
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    String url = String.format("http://schools-live.com/getUser.php?phonenumber=%s", phoneNumber2);
                    sendGET(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(final AccountKitError error) {
                Log.e("AccountKit",error.toString());
                // Handle Error
            }
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            String toastMessage= "";
            if (loginResult.getError() != null) {
                toastMessage = loginResult.getError().getErrorType().getMessage();
            } else if (loginResult.wasCancelled()) {
                toastMessage = "Login Cancelled";
            } else {
                if (loginResult.getAccessToken() != null) {
                    getAccount();
                }
            }
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
                    if(result.contains("Got Result")) {
                        progressDialog.hide();
                        result = result.replace("Got Result<br>","");
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            String name = "";
                            String phoneNumber = "";
                            String lastUpdateTime = "";
                            String totalUpdates = "";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    name = jsonArray.getJSONObject(i).getString("Name");
                                    phoneNumber = jsonArray.getJSONObject(i).getString("Phone_Number");
                                    lastUpdateTime = jsonArray.getJSONObject(i).getString("Last_Update_Time");
                                    totalUpdates = jsonArray.getJSONObject(i).getString("Total_Updates");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(getBaseContext(), "Phone Number already Registered", Toast.LENGTH_SHORT).show();
                            User user = new User(name, phoneNumber, lastUpdateTime, totalUpdates);
                            saveUserSharedPreferences(user);
                            finish();
                            startActivity(new Intent(getBaseContext(), SchoolActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else {
                        progressDialog.hide();
                        Intent intent = new Intent(getBaseContext(), AccountActivity.class);
                        intent.putExtra("Phone Number", phoneNumberString);
                        finish();
                        startActivity(intent);
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
