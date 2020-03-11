package com.mulganov.spectral_test_task;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {


    private ProgressBar bar;
    private boolean runBar = false;
    private long t;
    private long d;
    private int time;
    private int timeMax;
    private Button b;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = findViewById(R.id.main);

        bar = findViewById(R.id.progressBar);
        b = findViewById(R.id.start);
        text = findViewById(R.id.text);

        hide();

    }

    public void onClick(View view) {

        if (!(view instanceof Button)) return;

        Button b = (Button) view;

        switch (b.getText().toString().toUpperCase()){
            case "START":
                b.setText("PAUSE");
                if ( bar.getProgress() == 0 ){
                    start();
                }else{
                    t = new Date().getTime() + time;
                }

                runBar = true;
                break;
            case "PAUSE":
                b.setText("START");
                runBar = false;
                break;
        }




    }

    private void start(){
        try {
            time = Integer.parseInt(((TextView)findViewById(R.id.input_time)).getText().toString());
            System.out.println("Ok");

            timeMax = time;

            startTimer();

        }catch (Exception e){
            System.out.println("Error");
            Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG);
        }
    }

    private void startTimer(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                t = date.getTime() + timeMax;

                bar.post(new Runnable() {
                    @Override
                    public void run() {
                        bar.setMax(time);
                    }
                });

                setTimer(0);

                for (;true;){
                    if (runBar == false) continue;
                    Date date1 = new Date();
                    d = date1.getTime();
                    if (d >= t){
                        System.out.println("timer");
                        setTimer(0);

                        b.post(new Runnable() {
                            @Override
                            public void run() {
                                b.setText("START");
                            }
                        });
                        break;
                    }

                    int n = (int) (t - d);
                    //System.out.println(n);
                    setTimer((n));

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void setTimer(final int timer){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bar.setProgress(timer);
                time = timer;
                text.setText(timer + "");
            }
        });

    }

    @Override
    public void onPause(){
        super.onPause();

        System.out.println(runBar);

        if (!runBar) return;

        MyWorker.time = time;
        MyWorker.context = this;
        MyWorker.t = t;
        OneTimeWorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance().enqueue(myWorkRequest);
    }


    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    public View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);

        System.out.println("onPostCreate");
    }

    @Override
    protected void onResume(){
        super.onResume();
        hide();


        System.out.println("onResume");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        hide();

        System.out.println("onRestart");
    }

    @Override
    protected void onStart(){
        super.onStart();
        hide();

        System.out.println("onStart");
    }
    public void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


}
