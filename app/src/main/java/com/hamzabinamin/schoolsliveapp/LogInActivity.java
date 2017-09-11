package com.hamzabinamin.schoolsliveapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    Button phoneNumberButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        phoneNumberButton = (Button) findViewById(R.id.phoneNumberButton);

        phoneNumberButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.phoneNumberButton:
                finish();
                startActivity(new Intent(getBaseContext(), FacebookVerificationActivity.class));
                break;
        }
    }
}
