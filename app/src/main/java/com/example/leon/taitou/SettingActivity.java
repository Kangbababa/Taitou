package com.example.leon.taitou;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.app.Activity;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private View riseView;
    private View leaveView;
    private View feedbackView;
    private View aboutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setting");

        riseView = findViewById(R.id.setting_rise_hint);
        leaveView = findViewById(R.id.setting_leave_hint);
        feedbackView = findViewById(R.id.setting_feedback_hint);
        aboutView = findViewById(R.id.setting_about_hint);
        riseView.setOnClickListener(this);
        leaveView.setOnClickListener(this);
        feedbackView.setOnClickListener(this);
        aboutView.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(riseView)) {

        } else if (view.equals(leaveView)) {

        } else if (view.equals(feedbackView)) {
            Intent intent = new Intent(this, FeedbackActivity.class);
            startActivity(intent);
        } else if (view.equals(aboutView)) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }
    }
}
