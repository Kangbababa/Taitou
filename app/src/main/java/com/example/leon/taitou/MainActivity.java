/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.example.leon.taitou;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.media.MediaPlayer;

import android.app.Activity;
import android.content.Intent;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {



    private CameraView cameraView;
    private TextView goodposTextView;
    private TextView leftposTextView;
    private TextView rightposTextView;
    private TextView downposTextView;
    private TextView upposTextView;
    private Button btnCapture;
    private Button btnSetting;
    private MediaPlayer mPlayer;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int count=0;
    private static boolean alert=false;
    private static  boolean preAlert=true;

    private static float[] floatValues=new float[227*227*3];
    private ActivityInference activityInference;
    private static final int IMAGE_MEAN = 0;
    private static final int INPUT_SIZE=227;
    private static final float IMAGE_STD = 255;
    private int[] intValues=new int[227*227];
    boolean btnflag= true;

    private Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        goodposTextView = (TextView)findViewById(R.id.goodpos_prob);
        leftposTextView = (TextView)findViewById(R.id.leftpos_prob);
        rightposTextView = (TextView)findViewById(R.id.rightpos_prob);
        downposTextView = (TextView)findViewById(R.id.downpos_prob);
        upposTextView = (TextView)findViewById(R.id.uppos_prob);
        btnCapture = (Button) findViewById(R.id.btnToggleCamera);
       // btnSetting = (Button)findViewById(R.id.btnSetting);

       // btnSetting.setOnClickListener(new OnClickListener(){
       //     @Override
       //     public void onClick(View v) {
       //         // TODO Auto-generated method stub
       //         //textView.setText("Welcome!!");
       //         Intent intent = new Intent();
       //         intent.setClass(MainActivity.this, SettingActivity.class);
       //         startActivity(intent);
       //}
       // });

        mPlayer = MediaPlayer.create(this, R.raw.hello);

        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                //Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                bitmap=ImageCrop(bitmap);
                bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
                bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                for (int i = 0; i < intValues.length; ++i) {
                    final int val = intValues[i];
                    floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                    floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                    floatValues[i * 3 + 2] = ((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                }
                activityPrediction();


            }
            @Override
            public void onCameraOpened() {
                super.onCameraOpened();
            }

            @Override
            public void onCameraClosed() {
                super.onCameraClosed();
            }

        });

         btnCapture.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //cameraView.captureImage();
                 if(btnflag){
                     btnflag = false;
                     StopMonintor();
                     btnCapture.setText("Start Capture");
                 }
                 else {
                     btnflag =true;
                     StartMonintor();
                     btnCapture.setText("Stop Capture");
                 }

             }
         });

        //activityInference = new ActivityInference(getApplicationContext());
        initTensorFlowAndLoadModel();

    }
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    activityInference = new ActivityInference(getApplicationContext());
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                activityInference=null;
            }
        });
    }
    private void activityPrediction()
    {


        // Perform inference using Tensorflow
        float[] results = activityInference.getActivityProb(floatValues);

         goodposTextView.setText(Float.toString(results[0]));
         leftposTextView.setText(Float.toString(results[1]));
         rightposTextView.setText(Float.toString(results[2]));
         downposTextView.setText(Float.toString(results[3]));
         upposTextView.setText(Float.toString(results[4]));

        if(BadPosition(results) & preAlert) {
            alert=true;
        }
        else{
            alert=false;
        }
        preAlert=alert;
    }
    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int wh = w / 2;

        int retX =w /4;
        int retY = h/8;

        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCapture.setVisibility(View.VISIBLE);
            }
        });
    }

    private  void  StartMonintor(){
        //持续60秒，间隔3秒，采集1帧，如果左或下，则语音提醒；
        //语音提醒5分钟后，重新监测
        mTimer = new Timer();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                if(!alert)
                {
                    cameraView.captureImage();

                }
                if (count % 6 == 0)//60秒一直不正
                {

                    if(alert)   mPlayer.start();
                    preAlert=true;

                }
                if((count % 30==0) & (alert))  //提醒后5分钟恢复采集
                {alert=false;}
            }
        };
        //开始一个定时任务
        mTimer.schedule(mTimerTask, 0, 10000);

    }
    private  void  StopMonintor(){

    }

    private  boolean BadPosition(float[] floatArray) {
        //最大是1 or 3项
        if(maxValue(floatArray,1) | maxValue(floatArray,3) )
        return true;
        else return  false;
    }
    private boolean maxValue(float[] floatArray,int pos) {
        for (int i=0;i<=floatArray.length;i++) {
            if (floatArray[pos]-floatArray[i]<=0){return false;}
        }
       return true;
    }

}
