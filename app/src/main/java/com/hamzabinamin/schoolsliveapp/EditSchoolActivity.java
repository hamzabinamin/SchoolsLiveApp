package com.hamzabinamin.schoolsliveapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class EditSchoolActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView2;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    de.hdodenhof.circleimageview.CircleImageView imageView;
    Button editSchoolButton;
    EditText schoolNameEditText;
    EditText schoolLocationEditText;
    EditText schoolWebsiteEditText;
    EditText schoolTwitterEditText;
    EditText schoolFacebookEditText;
    Spinner schoolTypeSpinner;
    ProgressDialog progressDialog;
    String ImageTag = "image_tag" ;
    String ImageName = "image_data" ;
    String SchoolName = "school_name";
    String ServerUploadPath ="http://schools-live.com/school-images/insertImage.php" ;
    String oldImagePath = "";
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    Bitmap bitmapImage;
    String schoolID;
    String schoolName;
    String schoolNameStore;
    String userChoosenTask;
    School school;
    School schoolStore;
    boolean check = true;
    boolean isImageCaptured;
    boolean isImageChosen;
    private static final Integer REQUEST_CAMERA = 0;
    private static final Integer SELECT_FILE = 1;
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
            setContentView(R.layout.activity_edit_school_small);
        else if (screenInches > 4)
            setContentView(R.layout.activity_edit_school);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

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

        View hView = mNavigationView.getHeaderView(0);
        imageView2 = (ImageView) hView.findViewById(R.id.profile_image);
        mNavigationView.setItemIconTintList(null);
        mToolBar = (Toolbar) findViewById(R.id.navigation_action);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout.addDrawerListener(mActionBarToggle);
        mActionBarToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getSchoolSharedPreferences()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).resetViewBeforeLoading(true)
                    .showImageForEmptyUri(R.drawable.placeholder)
                    .showImageOnFail(R.drawable.placeholder)
                    .showImageOnLoading(R.drawable.placeholder).build();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
            imageLoader.displayImage(school.getSchoolImage(), imageView2, options);
        }

        imageView = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);
        editSchoolButton = (Button) findViewById(R.id.editSchoolButton);
        schoolNameEditText = (EditText) findViewById(R.id.schoolNameEditText);
        schoolLocationEditText = (EditText) findViewById(R.id.schoolLocationEditText);
        schoolWebsiteEditText = (EditText) findViewById(R.id.schoolWebsiteEditText);
        schoolTwitterEditText = (EditText) findViewById(R.id.schoolTwitterEditText);
        schoolFacebookEditText = (EditText) findViewById(R.id.schoolFacebookEditText);
        schoolTypeSpinner = (Spinner) findViewById(R.id.schoolTypeSpinner);
        progressDialog = new ProgressDialog(this);
        byteArrayOutputStream = new ByteArrayOutputStream();

        editSchoolButton.setOnClickListener(this);
        imageView.setOnClickListener(this);

        if(getEditSchoolSharedPreferences()) {
            try {
                schoolName = URLEncoder.encode(schoolName.trim(), "UTF-8");
                String url = String.format("http://schools-live.com/getOneSchool.php?name=%s", schoolName);

                progressDialog.setMessage("Please Wait");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                sendGET(url);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Unable to find the market app", Toast.LENGTH_LONG).show();
        }
    }

 /*   @Override
    protected void onResume() {
        super.onResume();
        if(getEditSchoolSharedPreferences()) {
            try {
                schoolName = URLEncoder.encode(schoolName, "UTF-8");
                String url = String.format("http://schools-live.com/getOneSchool.php?name=%s", schoolName);

                progressDialog.setMessage("Please Wait");
                progressDialog.show();
                sendGET(url);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    } */

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean getSchoolSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("School String", null) != null) {
            String serviceProviderString = sharedPreferences.getString("School String", null);
            Gson gson = new Gson();
            TypeToken<School> token = new TypeToken<School>() {};
            school = gson.fromJson(serviceProviderString, token.getType());
            schoolStore = gson.fromJson(serviceProviderString, token.getType());
            return true;
        }
        else
            return false;
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public boolean getEditSchoolSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("School Name", null) != null) {
            schoolName = sharedPreferences.getString("School Name", null);
            schoolNameStore = sharedPreferences.getString("School Name", null);
            schoolNameStore = schoolNameStore.trim();
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
                        String schoolName = "";
                        String scholType = "";
                        String schoolWebsite = "";
                        String schoolTwitter = "";
                        String schoolFacebook = "";
                        String schoolLocation = "";
                        String schoolLogo = "";


                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                schoolID = arr.getJSONObject(i).getString("School_ID");
                                schoolName = arr.getJSONObject(i).getString("School_Name");
                                scholType = arr.getJSONObject(i).getString("School_Type");
                                schoolWebsite = arr.getJSONObject(i).getString("School_Website");
                                schoolTwitter = arr.getJSONObject(i).getString("School_Twitter");
                                schoolFacebook = arr.getJSONObject(i).getString("School_Facebook");
                                schoolLocation = arr.getJSONObject(i).getString("School_Location");
                                schoolLogo = arr.getJSONObject(i).getString("School_Logo");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        schoolNameEditText.setText(schoolName.trim());
                        schoolLocationEditText.setText(schoolLocation);
                        schoolWebsiteEditText.setText(schoolWebsite);
                        schoolTwitterEditText.setText(schoolTwitter);
                        schoolFacebookEditText.setText(schoolFacebook);
                        String[] schoolTypeArray = new String[] { "Primary School", "High School", "Pri-High School", "College"  };
                        ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line, schoolTypeArray);
                        schoolTypeSpinner.setAdapter(adapter);

                        if(scholType.equals("High School")) {
                            schoolTypeSpinner.setSelection(1);
                        }
                        else if(scholType.equals("Primary School")) {
                            schoolTypeSpinner.setSelection(0);
                        }
                        else if(scholType.equals("Pri-High School")) {
                            schoolTypeSpinner.setSelection(2);
                        }
                        else if(scholType.equals("College")) {
                            schoolTypeSpinner.setSelection(3);
                        }

                        MemoryCacheUtils.removeFromCache(school.getSchoolImage(), ImageLoader.getInstance().getMemoryCache());
                        DiskCacheUtils.removeFromCache(school.getSchoolImage(), ImageLoader.getInstance().getDiscCache());

                        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                                .cacheOnDisk(true).resetViewBeforeLoading(true)
                                .showImageForEmptyUri(R.drawable.placeholder)
                                .showImageOnFail(R.drawable.placeholder)
                                .showImageOnLoading(R.drawable.placeholder).build();

                        ImageLoader imageLoader = ImageLoader.getInstance();
                        imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
                        imageLoader.displayImage(schoolLogo, imageView, options);
                        oldImagePath = schoolLogo;
                        //httpDownloadImages task = new httpDownloadImages();
                        //task.execute();
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

            case R.id.profile_image:
                selectImage();
                break;

            case R.id.editSchoolButton:
                if(validation()) {
                    progressDialog.setMessage("Please Wait");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    String schoolName = schoolNameEditText.getText().toString().trim();
                    String schoolLocation = schoolLocationEditText.getText().toString().trim();
                    String schoolWebsite = schoolWebsiteEditText.getText().toString().trim();
                    String schoolTwitter = schoolTwitterEditText.getText().toString().trim();
                    String schoolFacebook = schoolFacebookEditText.getText().toString().trim();
                    String schoolType = schoolTypeSpinner.getSelectedItem().toString().trim();

                    try {
                        schoolName = URLEncoder.encode(schoolName, "UTF-8");
                        schoolLocation = URLEncoder.encode(schoolLocation, "UTF-8");
                        schoolWebsite = URLEncoder.encode(schoolWebsite, "UTF-8");
                        schoolTwitter = URLEncoder.encode(schoolTwitter, "UTF-8");
                        schoolFacebook = URLEncoder.encode(schoolFacebook, "UTF-8");
                        schoolType = URLEncoder.encode(schoolType, "UTF-8");
                        String url = String.format("http://schools-live.com/updateSchool.php?id=%s&name=%s&type=%s&website=%s&twitter=%s&facebook=%s&location=%s", schoolID, schoolName, schoolType, schoolWebsite, schoolTwitter, schoolFacebook, schoolLocation);

                        if(bitmapImage != null) {
                            sendGET(url, BitMapToString(bitmapImage));
                        }
                        else {
                            System.out.println("Got here, no new image selected");
                            System.out.println("Old Image: " + oldImagePath);
                            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            sendGET(url, BitMapToString(bitmap));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
              /*  else {
                   /* if(bitmapImage == null && (!isImageCaptured || !isImageChosen)) {
                        Toast.makeText(getBaseContext(), "Please select an image for your School", Toast.LENGTH_SHORT).show();
                    } */

                break;

        }
    }

    public boolean validation() {

        String schoolType = "";
        if(schoolTypeSpinner.getSelectedItem() != null) {
            schoolType = schoolTypeSpinner.getSelectedItem().toString();
        }

        if(schoolNameEditText.getText().toString().length() > 0 && schoolLocationEditText.getText().toString().length() > 0 && schoolWebsiteEditText.getText().toString().length() > 0 && schoolTwitterEditText.getText().toString().length() > 0 && schoolFacebookEditText.getText().toString().length() > 0 && schoolType.length() > 0) {

            if(imageView.getDrawable() == null) {
                Toast.makeText(getBaseContext(), "Please choose an Image first", Toast.LENGTH_SHORT);
                return false;
            }
         /*   else (bitmapImage == null && (!isImageCaptured || !isImageChosen)) {
                return false;
            } */
            if(!schoolFacebookEditText.getText().toString().contains("www.facebook.com")) {
                System.out.println("Facebook");
                Toast.makeText(getBaseContext(), "Please write the Facebook URL in correct format", Toast.LENGTH_SHORT).show();
                return false;
            }
            if(!schoolTwitterEditText.getText().toString().contains("www.twitter.com")) {
                System.out.println("Twitter");
                Toast.makeText(getBaseContext(), "Please write the Twitter URL in correct format", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
        else {
            Toast.makeText(getBaseContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditSchoolActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(EditSchoolActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                imageView.setImageBitmap(bm);
                schoolName = schoolNameEditText.getText().toString().trim();
                isImageChosen = true;
                bitmapImage = bm;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //    String path = saveToInternalStorage(cropped);
        //    android.util.Log.e("Path", path);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Bitmap cropped = getRoundedCroppedImage(thumbnail);
        imageView.setImageBitmap(thumbnail);
        isImageCaptured = true;
        bitmapImage = thumbnail;
        //String path = saveToInternalStorage(cropped);
        //android.util.Log.e("Path", path);
        // avatarImage.setImageResource(null);
    }

    public class httpDownloadImages extends AsyncTask<Void,Void,String> {
        Bitmap myBitmap[] = new Bitmap[10];
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                System.out.println("Here in Do In Background");
                //       for(int i=1; i<=10; i++){
                   // schoolName = URLEncoder.encode(schoolName, "UTF-8");
                 //URLEncoder.encode(schoolName, "UTF-8").replace("+","%20").replace("B","0")
                    String src = "http://schools-live.com/school-images/" + URLEncoder.encode(schoolName.trim(), "UTF-8") + ".png";
                System.out.println("URL: " + "http://schools-live.com/school-images/" + URLEncoder.encode(schoolName.trim(), "UTF-8") + ".png" );
                    java.net.URL url = new java.net.URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap[0] = BitmapFactory.decodeStream(input);
                    if(myBitmap[0]!= null) {
                        System.out.println("Here in If condition");
                        //imageView.setImageBitmap(myBitmap[0]);
                        //saveimagetoFile(myBitmap[0], 0);
                    }
                    else {
                        System.out.println("Here in Else condition");
                    }
           //     }

            } catch (IOException e) {

                return null;
            }
            return BitMapToString(myBitmap[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            //System.out.println("Got the logo: " + s);
            imageView.setImageBitmap(StringToBitMap(s));

        }
    }

    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void UploadImageToServer(Bitmap FixBitmap){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byteArray = byteArrayOutputStream.toByteArray();

        ConvertImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {

                super.onPreExecute();

                //          progressDialog = ProgressDialog.show(AddSchoolActivity.this,"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {

                super.onPostExecute(string1);

                progressDialog.dismiss();


                if(schoolStore.getSchoolName().equals(schoolNameStore)) {
                   // Toast.makeText(getBaseContext(), "You Updated your current selected School. Please select it again from Change School Settings to reapply the update on the app.", Toast.LENGTH_SHORT).show();

                    String schoolName = schoolNameEditText.getText().toString().trim();
                    String schoolLocation = schoolLocationEditText.getText().toString().trim();
                    String schoolWebsite = schoolWebsiteEditText.getText().toString().trim();
                    String schoolTwitter = schoolTwitterEditText.getText().toString().trim();
                    String schoolFacebook = schoolFacebookEditText.getText().toString().trim();
                    String schoolType = schoolTypeSpinner.getSelectedItem().toString().trim();

                    School schoolUpdated = new School(schoolName, schoolType, schoolWebsite, schoolTwitter, schoolFacebook, schoolLocation);

                    if(oldImagePath.length() > 0) {
                        schoolUpdated.setSchoolImage(oldImagePath);
                    }
                    else {
                        schoolUpdated.setSchoolImage("http://schools-live.com/school-images/" + schoolName);
                    }
                    saveSchoolSharedPreferences(schoolUpdated);
                    Toast.makeText(EditSchoolActivity.this, "School Updated Successfully", Toast.LENGTH_LONG).show();
                    oldImagePath = "";
                }
                else {
                    String schoolName = schoolNameEditText.getText().toString().trim();
                    String schoolLocation = schoolLocationEditText.getText().toString().trim();
                    String schoolWebsite = schoolWebsiteEditText.getText().toString().trim();
                    String schoolTwitter = schoolTwitterEditText.getText().toString().trim();
                    String schoolFacebook = schoolFacebookEditText.getText().toString().trim();
                    String schoolType = schoolTypeSpinner.getSelectedItem().toString().trim();

                    School schoolUpdated = new School(schoolName, schoolType, schoolWebsite, schoolTwitter, schoolFacebook, schoolLocation);

                    if(oldImagePath.length() > 0) {
                        schoolUpdated.setSchoolImage(oldImagePath);
                    }
                    else {
                        schoolUpdated.setSchoolImage("http://schools-live.com/school-images/" + schoolName);
                    }

                    saveSchoolSharedPreferences(schoolUpdated);
                    Toast.makeText(EditSchoolActivity.this, "School Updated Successfully", Toast.LENGTH_LONG).show();
                    oldImagePath = "";
                }

                System.out.println("String1: " + string1 );
            }

            @Override
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();

                try {
                    if(oldImagePath.length() > 0) {
                        HashMapParams.put(ImageTag, oldImagePath.replace("http://schools-live.com/school-images/", ""));
                    }
                    else {
                        String store = URLEncoder.encode(schoolName.trim(), "UTF-8");
                        HashMapParams.put(ImageTag, store);
                    }

                    HashMapParams.put(ImageName, ConvertImage);

                    HashMapParams.put(SchoolName, schoolName.trim());

                    String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                    return FinalData;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }

    public void saveSchoolSharedPreferences(School school) {
        Gson gson = new Gson();
        String serviceProviderString = gson.toJson(school);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("School String", serviceProviderString).commit();
    }

    public class ImageProcessClass {

        public String ImageHttpRequest(String requestURL, HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                url = new URL(requestURL);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);

                httpURLConnection.setConnectTimeout(20000);

                httpURLConnection.setRequestMethod("POST");

                httpURLConnection.setDoInput(true);

                httpURLConnection.setDoOutput(true);

                outputStream = httpURLConnection.getOutputStream();

                bufferedWriter = new BufferedWriter(

                        new OutputStreamWriter(outputStream, "UTF-8"));

                bufferedWriter.write(bufferedWriterDataFN(PData));

                bufferedWriter.flush();

                bufferedWriter.close();

                outputStream.close();

                RC = httpURLConnection.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {

                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                    stringBuilder = new StringBuilder();

                    String RC2;

                    while ((RC2 = bufferedReader.readLine()) != null) {

                        stringBuilder.append(RC2);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {

            stringBuilder = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {
                if (check)
                    check = false;
                else
                    stringBuilder.append("&");

                stringBuilder.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));

                stringBuilder.append("=");

                stringBuilder.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilder.toString();
        }
    }

    public void sendGET(String paramURL, final String bm) throws IOException {

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
                        //progressDialog.hide();
                        //Toast.makeText(getBaseContext(), "New record created successfully", Toast.LENGTH_SHORT).show();
                        schoolName = schoolNameEditText.getText().toString().trim();
                        //schoolName = URLEncoder.encode(schoolName, "UTF-8");
                        UploadImageToServer(StringToBitMap(bm));
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
        httpGetAsyncTask.execute(paramURL, bm);
    }


    void saveimagetoFile(Bitmap bmp,int num){
       /* try {
            String path = Environment.getExternalStorageDirectory().toString();
            OutputStream fOut = null;
            File file = new File(path, "PriceChecker/pc"+num+".jpg"); // the File to save to
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut); //save image to jpeg file
            fOut.flush();
            fOut.close();
        }
        catch (Exception e){

        } */
       System.out.println("Here in Save Image");
       imageView.setImageBitmap(bmp);
    }
}
