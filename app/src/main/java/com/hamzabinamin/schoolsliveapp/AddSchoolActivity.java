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
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

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

public class AddSchoolActivity extends AppCompatActivity implements View.OnClickListener {


    Button addSchoolButton;
    EditText schoolNameEditText;
    EditText schoolLocationEditText;
    EditText schoolWebsiteEditText;
    EditText schoolTwitterEditText;
    EditText schoolFacebookEditText;
    de.hdodenhof.circleimageview.CircleImageView imageView2;
    ImageView imageView;
    Spinner schoolTypeSpinner;
    String userChoosenTask;
    String ImageTag = "image_tag" ;
    String ImageName = "image_data" ;
    String SchoolName = "school_name" ;
    String ServerUploadPath ="http://schools-live.com/school-images/insertImage.php" ;
    String schoolName = "";
    ProgressDialog progressDialog ;
    ByteArrayOutputStream byteArrayOutputStream ;
    byte[] byteArray ;
    String ConvertImage ;
    String GetImageNameFromEditText;
    HttpURLConnection httpURLConnection ;
    URL url;
    OutputStream outputStream;
    BufferedWriter bufferedWriter ;
    int RC ;
    BufferedReader bufferedReader ;
    StringBuilder stringBuilder;
    String[] schoolTypeArray = new String[] { "Primary School", "High School", "Pri-High School", "College" };
    Bitmap bitmapImage;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mActionBarToggle;
    Toolbar mToolBar;
    boolean check = true;
    boolean isImageCaptured;
    boolean isImageChosen;
    private static final String USER_AGENT = "Mozilla/5.0 (Android 4.4; Mobile; rv:41.0) Gecko/41.0 Firefox/41.0";
    private static final Integer REQUEST_CAMERA = 0;
    private static final Integer SELECT_FILE = 1;
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
            setContentView(R.layout.activity_add_school_small);
        else if (screenInches > 4)
            setContentView(R.layout.activity_add_school);

        addSchoolButton = (Button) findViewById(R.id.addSchoolButton);
        schoolNameEditText = (EditText) findViewById(R.id.schoolNameEditText);
        schoolLocationEditText = (EditText) findViewById(R.id.schoolLocationEditText);
        schoolWebsiteEditText = (EditText) findViewById(R.id.schoolWebsiteEditText);
        schoolTwitterEditText = (EditText) findViewById(R.id.schoolTwitterEditText);
        schoolFacebookEditText = (EditText) findViewById(R.id.schoolFacebookEditText);
        imageView2 = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image2);
        schoolTypeSpinner = (Spinner) findViewById(R.id.schoolTypeSpinner);
        byteArrayOutputStream = new ByteArrayOutputStream();
        progressDialog = new ProgressDialog(this);

        addSchoolButton.setOnClickListener(this);
        imageView2.setOnClickListener(this);
        schoolNameEditText.setOnClickListener(this);
        schoolLocationEditText.setOnClickListener(this);
        schoolWebsiteEditText.setOnClickListener(this);
        schoolTwitterEditText.setOnClickListener(this);
        schoolFacebookEditText.setOnClickListener(this);

        schoolNameEditText.setFocusable(false);
        schoolLocationEditText.setFocusable(false);
        schoolWebsiteEditText.setFocusable(false);
        schoolTwitterEditText.setFocusable(false);
        schoolFacebookEditText.setFocusable(false);

        AdView adView = (AdView) findViewById(R.id.addView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        //  .addTestDevice("1E9E1DA0C4E19BA422D51AF125310542").build();
        adView.loadAd(adRequest);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, schoolTypeArray);
        schoolTypeSpinner.setAdapter(adapter);

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
        imageView = (ImageView) hView.findViewById(R.id.profile_image);
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

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.schoolNameEditText:
                schoolNameEditText.setFocusableInTouchMode(true);
                schoolNameEditText.requestFocus();
                break;

            case R.id.schoolLocationEditText:
                schoolLocationEditText.setFocusableInTouchMode(true);
                schoolLocationEditText.requestFocus();
                break;

            case R.id.schoolWebsiteEditText:
                schoolWebsiteEditText.setFocusableInTouchMode(true);
                schoolWebsiteEditText.requestFocus();

                break;

            case R.id.schoolTwitterEditText:
                schoolTwitterEditText.setFocusableInTouchMode(true);
                schoolTwitterEditText.requestFocus();
                break;

            case R.id.schoolFacebookEditText:
                schoolFacebookEditText.setFocusableInTouchMode(true);
                schoolFacebookEditText.requestFocus();
                break;

            case R.id.addSchoolButton:
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
                        String url = String.format("http://schools-live.com/getSchools.php");

                        sendGETCheckSchool(url, schoolName, schoolLocation, schoolWebsite, schoolTwitter, schoolFacebook, schoolType);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    if(bitmapImage == null && (!isImageCaptured || !isImageChosen)) {
                        Toast.makeText(getBaseContext(), "Please select an image for your School", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getBaseContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case R.id.profile_image2:
                selectImage();
                break;

        }

    }

    public boolean validation() {

        String schoolType = "";
        if(schoolTypeSpinner.getSelectedItem() != null) {
            schoolType = schoolTypeSpinner.getSelectedItem().toString();
        }

        if(schoolNameEditText.getText().toString().length() > 0 && schoolLocationEditText.getText().toString().length() > 0 && schoolWebsiteEditText.getText().toString().length() > 0 && schoolTwitterEditText.getText().toString().length() > 0 && schoolFacebookEditText.getText().toString().length() > 0 && schoolType.length() > 0) {

            if(bitmapImage == null && (!isImageCaptured || !isImageChosen)) {
                System.out.println("No Image");
                return false;
            }
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
            System.out.println("Got in Else");
            return false;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(AddSchoolActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(AddSchoolActivity.this);
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

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent()
    {
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
                    if(userChoosenTask != null) {
                        if (userChoosenTask.equals("Take Photo"))
                            cameraIntent();
                        else if (userChoosenTask.equals("Choose from Library"))
                            galleryIntent();
                    }
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
                imageView2.setImageBitmap(bm);
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
        imageView2.setImageBitmap(thumbnail);
        isImageCaptured = true;
        bitmapImage = thumbnail;
        //String path = saveToInternalStorage(cropped);
        //android.util.Log.e("Path", path);
        // avatarImage.setImageResource(null);
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

                Toast.makeText(AddSchoolActivity.this, "School Added Successfully", Toast.LENGTH_LONG).show();
                System.out.println("String1: " + string1 );

                School school = new School(schoolNameEditText.getText().toString().trim(), schoolTypeSpinner.getSelectedItem().toString(), schoolWebsiteEditText.getText().toString(), schoolTwitterEditText.getText().toString(), schoolFacebookEditText.getText().toString(), schoolLocationEditText.getText().toString());
                String image = null;
                try {
                    image = "http://schools-live.com/school-images/" + URLEncoder.encode(schoolNameEditText.getText().toString().trim(), "UTF-8") + ".png";
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                school.setSchoolImage(image);
                if(!getStatusSharedPreferences()) {
                    saveSchoolSharedPreferences(school);
                    saveStatusSharedPreferences("Yes");
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
            protected String doInBackground(Void... params) {

                ImageProcessClass imageProcessClass = new ImageProcessClass();

                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                try {
                    String store = URLEncoder.encode(schoolName.trim(), "UTF-8");
                    HashMapParams.put(ImageTag, store);

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
                    if(result.contains("New record created successfully")) {
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

    public void sendGETCheckSchool(String paramURL, final String schoolName, final String schoolLocation, final String schoolWebsite, final String schoolTwitter, final String schoolFacebook, final String schoolType) throws IOException {

        class HttpGetAsyncTask extends AsyncTask<String, Void, String> {
            boolean isPresent = false;

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
                        result = result.replace("Got Result<br>","");
                        JSONArray arr = null;
                        try {
                            arr = new JSONArray(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String schoolNameOld = "";
                        String schoolTypeOld = "";

                        for (int i = 0; i < arr.length(); i++) {
                            try {
                                schoolNameOld = arr.getJSONObject(i).getString("School_Name").trim();
                                schoolTypeOld = arr.getJSONObject(i).getString("School_Type").trim();

                                try {
                                    schoolNameOld = URLEncoder.encode(schoolNameOld, "UTF-8");
                                    schoolTypeOld = URLEncoder.encode(schoolTypeOld, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                if(schoolName.toLowerCase().equals(schoolNameOld.toLowerCase()) && schoolType.toLowerCase().equals(schoolTypeOld.toLowerCase())) {
                                    System.out.println("School is Present: " + schoolName + "/" + schoolNameOld);
                                    isPresent = true;
                                    break;
                                }
                                else {
                                    System.out.println("School is not Present: " + schoolName + "/" + schoolNameOld);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if(isPresent) {
                            progressDialog.dismiss();
                            Toast.makeText(getBaseContext(), "This school already exists", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String url = String.format("http://schools-live.com/insertSchool.php?name=%s&type=%s&website=%s&twitter=%s&facebook=%s&location=%s", schoolName, schoolType, schoolWebsite, schoolTwitter, schoolFacebook, schoolLocation);
                            try {
                                sendGET(url, BitMapToString(bitmapImage));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
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


    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
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

    public void saveSchoolSharedPreferences(School school) {
        Gson gson = new Gson();
        String serviceProviderString = gson.toJson(school);
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("School String", serviceProviderString).commit();
    }

    public void saveStatusSharedPreferences(String status) {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("Current School", status).commit();
    }

    public boolean getStatusSharedPreferences() {

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("com.hamzabinamin.schoolsliveapp", Context.MODE_PRIVATE);

        if (sharedPreferences.getString("Current School", null) != null) {
            return true;
        }
        else
            return false;
    }

}
