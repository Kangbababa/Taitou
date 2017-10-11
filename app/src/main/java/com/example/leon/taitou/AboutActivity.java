package com.example.leon.taitou;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * Created by vic on 2017/9/17.
 */

public class AboutActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar=(Toolbar)findViewById(R.id.about_toolbar);
        //需要调用该函数才能设置toolbar的信息
        setSupportActionBar(toolbar);
        //显示向上箭头
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置title，我直接用toolbar会出错
        getSupportActionBar().setTitle("About");
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
