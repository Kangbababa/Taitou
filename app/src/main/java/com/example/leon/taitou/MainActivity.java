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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.flurgle.camerakit.Facing;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import android.media.MediaPlayer;

import com.orhanobut.logger.Logger;


import android.content.Intent;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;
import android.os.Message;
import com.example.leon.taitou.UploadUtil.OnUploadProcessListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.content.Context;

public class MainActivity extends AppCompatActivity implements OnUploadProcessListener{


    private CameraView cameraView;
    private TextView goodposTextView;
    private TextView leftposTextView;
    private TextView rightposTextView;
    private TextView downposTextView;
    private TextView upposTextView;
    private TextView imagePercentView;
    private Button btnCapture;
    private ImageView btnSetting;
    private ImageView btnCamera_change;

    private MediaPlayer mPlayerTip,mPlayerThere;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int count = 0;
    private static boolean alert = false;
    private static int historyAlert = 0;
    private static Bitmap  prePic;

    private static float[] floatValues = new float[227 * 227 * 3];
    private ActivityInference activityInference;
    private static final int IMAGE_MEAN = 0;
    private static final int INPUT_SIZE = 227;
    private static final float IMAGE_STD = 255;
    private int[] intValues = new int[227 * 227];
    boolean btnflag = false;
    private int camera_change=0;
    private static String requestURL = "http://wipos.cn/iface/body/upload.na";
    private String picPath = null;
    private static final String TAG = "uploadImage";

    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;  //
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;

    private Executor executor = Executors.newSingleThreadExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (CameraView) findViewById(R.id.cameraView);
        goodposTextView = (TextView) findViewById(R.id.goodpos_prob);
        leftposTextView = (TextView) findViewById(R.id.leftpos_prob);
        rightposTextView = (TextView) findViewById(R.id.rightpos_prob);
        downposTextView = (TextView) findViewById(R.id.downpos_prob);
        upposTextView = (TextView) findViewById(R.id.uppos_prob);
        imagePercentView= (TextView) findViewById(R.id.imgpercent_prob);

        btnCapture = (Button) findViewById(R.id.btnToggleCamera);
        btnSetting = (ImageView)findViewById(R.id.btnSetting);
        btnCamera_change=(ImageView)findViewById(R.id.camera_change);


         btnSetting.setOnClickListener(new OnClickListener(){
             @Override
             public void onClick(View v) {
                // TODO Auto-generated method stub
        //         //textView.setText("Welcome!!");
                 if (mTimer!=null) {
                     btnflag=true;
                     StartStop();
                 }

                Intent intent = new Intent();
                 intent.setClass(MainActivity.this, SettingActivity.class);
                 startActivity(intent);
        }
         });

        btnCamera_change.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //         //textView.setText("Welcome!!");
                if(camera_change==0){
                    cameraView.setFacing(1);
                    camera_change=1;
                }else{
                    cameraView.setFacing(0);
                    camera_change=0;
                }

            }
        });

        mPlayerTip = MediaPlayer.create(this, R.raw.tip);
        mPlayerThere=MediaPlayer.create(this, R.raw.isthere);



        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);
                //load from assets bmp file ，floatvalue is different with python load
                //byte[] b = null;

                //try {
                //    AssetManager am = getResources().getAssets();
                //    InputStream is = am.open("1-1.jpeg");
                //    int size = is.available();
                //        b = new byte[size];
                //        // 读取数据
                //        is.read(b);
                //        is.close();
                //} catch (IOException e) {
                //    e.printStackTrace();
                //}
                //Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);


                Bitmap bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
                bitmap = ImageCrop(bitmap);



                Bitmap resizebitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
                //find diff with current pic and previous pic
                // to find wether the kid is there when  difference  percent is more then 0.4
                Bitmap  grayBmp= gray2Binary(resizebitmap);
                if (prePic==null){prePic=grayBmp;}
                double imgDiff=ImgDiffPercent(grayBmp);
                //same bmp threw out
                if(imgDiff>=0.02) {
                    if (imgDiff >= 0.4) {
                        //alert child is there?//
                        mPlayerThere.start();
                    }
                    prePic = grayBmp;

                    //3D array to 1D array
                    resizebitmap.getPixels(intValues, 0, resizebitmap.getWidth(), 0, 0, resizebitmap.getWidth(), resizebitmap.getHeight());
                    //grayBmp.getPixels(intValues, 0, grayBmp.getWidth(), 0, 0, grayBmp.getWidth(), grayBmp.getHeight());
                    for (int i = 0; i < intValues.length; ++i) {
                        final int val = intValues[i];
                        floatValues[i * 3 + 0] = (((val >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                        floatValues[i * 3 + 1] = (((val >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                        floatValues[i * 3 + 2] = ((val & 0xFF) - IMAGE_MEAN) / IMAGE_STD;
                    }
                    activityPrediction(imgDiff, bitmap);
                }

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
                StartStop();

            }
        });

        //activityInference = new ActivityInference(getApplicationContext());
        initTensorFlowAndLoadModel();

    }
    private void StartStop(){
        if (btnflag) {
            btnflag = false;
            StopMonintor();
            btnCapture.setText(R.string.toggle_camera_start);
        } else {
            btnflag = true;

            alert = false;
            historyAlert = 0;
            count = 0;

            StartMonintor();
            btnCapture.setText(R.string.toggle_camera_stop);
        }

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
                activityInference = null;
            }
        });
    }









    @Override
    public void onUploadDone(int responseCode, String message) {

        Message msg = Message.obtain();
        msg.what = UPLOAD_FILE_DONE;
        msg.arg1 = responseCode;
        msg.obj = message;
        handler.sendMessage(msg);
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    toUploadFile();
                    break;

                case UPLOAD_INIT_PROCESS:
                    break;
                case UPLOAD_IN_PROCESS:
                    break;
                case UPLOAD_FILE_DONE:
                    //String result = "响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒";
                    Log.e(TAG,"UPLOAD_FILE_DONE响应码："+msg.arg1+"\n响应信息："+msg.obj+"\n耗时："+UploadUtil.getRequestTime()+"秒");
                    if (msg.arg1 == 1) {
                        //del local file
                        String filename=findFileName(msg.obj.toString());
                        Log.e(TAG,"filename="+filename);
                        SDCardHelper.removeFileFromSDCard("/storage/emulated/0/Android/data/com.example.leon.taitou/cache/"+filename);

                    }

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    @Override
    public void onUploadProcess(int uploadSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_IN_PROCESS;
        msg.arg1 = uploadSize;
        handler.sendMessage(msg );

    }

    @Override
    public void initUpload(int fileSize) {
        Message msg = Message.obtain();
        msg.what = UPLOAD_INIT_PROCESS;
        msg.arg1 = fileSize;
        handler.sendMessage(msg );

    }


    private void activityPrediction(double imgPercent,Bitmap bitmap) {


        // Perform inference using Tensorflow
        float[] results = activityInference.getActivityProb(floatValues);

        goodposTextView.setText(Float.toString(results[0]));
        leftposTextView.setText(Float.toString(results[1]));
        rightposTextView.setText(Float.toString(results[2]));
        downposTextView.setText(Float.toString(results[3]));
        upposTextView.setText(Float.toString(results[4]));
        imagePercentView.setText(Double.toString(imgPercent));

        //if results max<0.8,Save the bmp to sd card private file
        //Logger.d("size " +SDCardHelper.getSDCardFreeSize());
        if(SDCardHelper.getSDCardFreeSize()>=200) {
            if(GetMaxResult(results)<=0.8) {
                Logger.d("saveBitmapToSDCardPrivateCacheDir ");
                SDCardHelper.saveBitmapToSDCardPrivateCacheDir(bitmap, SDCardHelper.genFileName(), this.getApplicationContext());
            }


        }


        if (BadPosition(results)) {
            historyAlert++;
        }
    }

    private float GetMaxResult(float[] arr){
        float max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }

        return max;

    }

    private void toUploadFile()
    {

        String fileKey = "imgFile";
        //picPath="/storage/emulated/0/Android/data/com.example.leon.taitou/cache/20171024193351.JPEG";
        UploadUtil uploadUtil = UploadUtil.getInstance();;
        uploadUtil.setOnUploadProcessListener(this);  //设置监听器监听上传状态

        Map<String, String> params = new HashMap<String, String>();
        //params.put("orderId", picPath);
        //uploadUtil.uploadFile( picPath,fileKey, requestURL,params);

        File[] files = new File(SDCardHelper.getSDCardPrivateCacheDir(this.getApplicationContext())).listFiles();

        for (File file : files) {
            Logger.d("filepath:" +file.getPath()+"filename:"+file.getName());
            params.put("orderId", file.getPath());
            uploadUtil.uploadFile( file.getPath(),fileKey, requestURL,params);
        }

    }
    private void StartMonintor() {
        //持续60秒，间隔10秒，采集1帧，如果左或下，则语音提醒；
        //语音提醒5分钟后，重新监测
        mTimer = new Timer();
        GetSettingInfo();
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                count++;
                if (!alert) {
                    cameraView.captureImage();


                }
                if (count % 6 == 0)//60秒一直不正
                {

                    if (historyAlert >= 5) {
                        mPlayerTip.start();

                        handler.sendEmptyMessage(TO_UPLOAD_FILE);

                        alert = true;

                    }
                    historyAlert = 0;
                }
                if ((count % 30 == 0) & (alert))  //提醒后5分钟恢复采集
                {
                    alert = false;
                }
            }
        };
        //开始一个定时任务
        mTimer.schedule(mTimerTask, 0, 10000);

    }

    private boolean BadPosition(float[] floatArray) {
        //最大是1 or 3项
        boolean badflag = false;
        if (maxValue(floatArray, 1) || maxValue(floatArray, 3)) {
            badflag = true;
        } else {
            badflag = false;
        }
        return badflag;
    }

    private boolean maxValue(float[] floatArray, int pos) {
        boolean maxflag = true;
        for (int i = 0; i < floatArray.length; ++i) {

            if (floatArray[pos] - floatArray[i] < 0) {
                maxflag = false;
                break;
            }
        }
        return maxflag;
    }
    private void GetSettingInfo() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String alert_listPref = sharedPref.getString("alert_tip_list", "");
        Logger.d("Pref " + alert_listPref);
        switch(alert_listPref){
            case "tip":
                mPlayerTip = MediaPlayer.create(this, R.raw.tip);
                break;
            case "tip1":
                mPlayerTip = MediaPlayer.create(this, R.raw.isthere);
                break;
            case "tip2":
                mPlayerTip = MediaPlayer.create(this, R.raw.tip);
                break;
        }

    }

    public static Bitmap ImageCrop(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int wh = w / 2;

        int retX = w / 4;
        int retY = h / 8;

        return Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
    }

    private double ImgDiffPercent(Bitmap pic) {

        long diff = 0;
        for (int y = 0; y < pic.getHeight(); y++) {
            for (int x = 0; x < pic.getWidth(); x++) {
                int rgb1 = pic.getPixel(x, y);
                int rgb2 = prePic.getPixel(x, y);
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >>  8) & 0xff;
                int b1 = (rgb1      ) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >>  8) & 0xff;
                int b2 = (rgb2      ) & 0xff;
                diff += Math.abs(r1 - r2);
                diff += Math.abs(g1 - g2);
                diff += Math.abs(b1 - b2);
            }
        }
        double n = pic.getWidth() * pic.getHeight() * 3;
        //double p = diff / n / 255.0;
        //System.out.println("diff percent: " + (p * 100.0));
        return diff / n / 255.0;
    }
    private String findFileName(String inputStr){
        String[] sArray=inputStr.split(",");
        String filename=null;
        for(int i=0;i<sArray.length;i++)
        {
            if(sArray[i].contains("oldFileNames")) {
                Logger.d(TAG,"oldFilenames"+sArray[i]);
                filename=sArray[i].substring(16,sArray[i].length()-1);
                break;
            }
        }
        return filename;

    }
    public Bitmap gray2Binary(Bitmap grayBmp) {

        grayBmp=Bitmap.createScaledBitmap(grayBmp, 20, 20, false);
        //得到图形的宽度和长度
        int width = grayBmp.getWidth();
        int height = grayBmp.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = grayBmp.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                //对图像进行二值化处理
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                }
                // 新的ARGB
                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }
    /**
     * 判断移动网络是否开启
     *
     * @param context
     * @return
     */
    public  boolean isNetEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        Logger.d("移动网络已经开启");
                        return true;
                    }
                }
            }
        }
        Logger.d("移动网络还未开启");
        return false;
    }

    /**
     * 判断WIFI网络是否开启
     *
     * @param context
     * @return
     */
    public  boolean isWifiEnabled(Context context) {
        WifiManager wm = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        if (wm != null && wm.isWifiEnabled()) {
            Logger.d(TAG, "Wifi网络已经开启");
            return true;
        }
        Logger.d(TAG, "Wifi网络还未开启");
        return false;
    }

    /**
     * 判断移动网络是否连接成功
     *
     * @param context
     * @return
     */
    public  boolean isNetContected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (cm != null && info != null && info.isConnected()) {
            Logger.d( "移动网络连接成功");
            return true;
        }
        Logger.d("移动网络连接失败");
        return false;
    }

    /**
     * 判断WIFI是否连接成功
     *
     * @param context
     * @return
     */
    public  boolean isWifiContected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (info != null && info.isConnected()) {
            Logger.d(TAG, "Wifi网络连接成功");
            return true;
        }
        Logger.d(TAG, "Wifi网络连接失败");
        return false;
    }



    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnCapture.setVisibility(View.VISIBLE);
            }
        });
    }


    private  void  StopMonintor(){
       mTimer.cancel();
    }


}
