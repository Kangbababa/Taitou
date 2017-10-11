package com.example.leon.taitou;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.FragmentTransaction;
import android.view.MenuItem;


public class SettingActivity extends AppCompatActivity {

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
}
