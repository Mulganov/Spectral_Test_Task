package com.mulganov.spectral_test_task;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.work.Worker;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MyWorker extends Worker {

    static final String TAG = "workmng";
    public static long time;
    public static Context context;
    public static long t;

    @NonNull
    @Override
    public WorkerResult doWork() {
        Log.d(TAG, "doWork: start");

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (;true;){
                    Date date1 = new Date();
                    long d = date1.getTime();
                    if (d >= t){
                        System.out.println("timer");
                        createNotificationChannel();
                        createNotification(0, "Время вышло");

                        Log.d(TAG, "doWork: end");

                        return;
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();





        return WorkerResult.SUCCESS;
    }

    private String CHANNEL_ID = "Test Task";

    private void createNotification(int n, String text){
        createNotificationChannel();
        System.out.println("---------------------------------");

        long[] vibrate = new long[] { 1000, 1000, 1000,};

        Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE); // Vibrate for 500 milliseconds v.vibrate(500);

        v.vibrate(VibrationEffect.EFFECT_DOUBLE_CLICK);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Test Task")
                .setContentText(text)
                .setVibrate(vibrate)
                .setLights(Color.RED, 1000, 1000)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define


        notificationManager.notify(n, builder.build());
        System.out.println("---------------------------------");
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = CHANNEL_ID;
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
