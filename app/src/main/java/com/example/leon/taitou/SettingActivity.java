package com.example.leon.taitou;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;

public class SettingActivity extends AppCompatActivity {


    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initToolbar();
        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingFragment()).commit();
    }


    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        mToolbar=(Toolbar)findViewById(R.id.toolbar_preference);
        mToolbar.setTitle(getResources().getString(R.string.title_activity_setting));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_left_back);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 选项菜单
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}