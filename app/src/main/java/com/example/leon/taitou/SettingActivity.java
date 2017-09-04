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

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar=(Toolbar)findViewById(R.id.setting_toolbar);
        //需要调用该函数才能设置toolbar的信息
        setSupportActionBar(toolbar);
        //显示向上箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置title，我直接用toolbar会出错
        getSupportActionBar().setTitle("Setting");
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
}
