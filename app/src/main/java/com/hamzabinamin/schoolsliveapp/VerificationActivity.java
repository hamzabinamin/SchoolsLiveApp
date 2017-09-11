package com.hamzabinamin.schoolsliveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class VerificationActivity extends AppCompatActivity implements View.OnClickListener {

    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        submitButton = (Button) findViewById(R.id.submitButton);

        submitButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.submitButton:
                finish();
                startActivity(new Intent(getBaseContext(), SchoolActivity.class));
        }
    }
}
