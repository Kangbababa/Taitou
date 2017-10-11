package com.example.leon.taitou;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;



public class SettingActivity extends AppCompatActivity {//implements View.OnClickListener {

    //private View riseView;
    //private View leaveView;
    //private View feedbackView;
    //private View aboutView;

    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


        initToolbar();

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new SettingFragment());
        fragmentTransaction.commit();

        //Toolbar toolbar=(Toolbar)findViewById(R.id.setting_toolbar);
        //需要调用该函数才能设置toolbar的信息
        //setSupportActionBar(toolbar);
        //显示向上箭头
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置title，我直接用toolbar会出错
        //getSupportActionBar().setTitle("Setting");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Setting");

        //riseView = findViewById(R.id.setting_rise_hint);
        //leaveView = findViewById(R.id.setting_leave_hint);
        //feedbackView = findViewById(R.id.setting_feedback_hint);
        //aboutView = findViewById(R.id.setting_about_hint);
        //riseView.setOnClickListener(this);
        //leaveView.setOnClickListener(this);
        //feedbackView.setOnClickListener(this);
        //aboutView.setOnClickListener(this);

    }

    private void initToolbar() {
        mToolbar=(Toolbar)findViewById(R.id.setting_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.title_activity_setting));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_left_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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

   // @Override
   // public void onClick(View view) {
   //     if (view.equals(riseView)) {

   //     } else if (view.equals(leaveView)) {

   //     } else if (view.equals(feedbackView)) {
   //         Intent intent = new Intent(this, FeedbackActivity.class);
   //         startActivity(intent);
   //     } else if (view.equals(aboutView)) {
   //         Intent intent = new Intent(this, AboutActivity.class);
   //         startActivity(intent);
   //     }
   // }
}
