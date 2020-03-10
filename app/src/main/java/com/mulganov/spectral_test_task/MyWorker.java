package com.mulganov.spectral_test_task;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
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
                        break;
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        createNotificationChannel();
        createNotification(0, "Время вышло");

        Log.d(TAG, "doWork: end");

        return WorkerResult.SUCCESS;
    }

    private String CHANNEL_ID = "Test Task";

    private void createNotification(int n, String text){
        createNotificationChannel();
        System.out.println("---------------------------------");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Test Task")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

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
