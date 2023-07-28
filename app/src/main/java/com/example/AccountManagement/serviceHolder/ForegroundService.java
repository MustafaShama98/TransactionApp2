package com.example.AccountManagement.serviceHolder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;


import com.example.AccountManagement.DBHolder.AppDB;
import com.example.AccountManagement.MainActivity;
import com.example.AccountManagement.R;
import com.example.AccountManagement.Transaction;
import com.example.AccountManagement.TransactionViewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class ForegroundService extends Service  {
    String CHANNEL_ID = "CHANNEL_SAMPLE";
    AppDB DataBase;
    Notification.Builder NotifyBuilder;
    NotificationManager notificationManager;
    MainActivity mainActivity = MainActivity.getactiv();
    ZonedDateTime time;
    LocalDate lt;
    Intent Brodcastintent = new Intent();
    TransactionViewModel transactionViewModel;
    private TransactionAdapterListener mainActivityCallback;

    @Override
    public void onCreate() {
        super.onCreate();

            CreateNotificationChannel();

    }


    /**It is a Singleton, however every call to
        startForegroundService will trigger a call to
     onStartCommand ’ inside the service **/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //start mainAcitivty when we click on the notification
        Intent notifcationIntent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notifcationIntent,0);
        //send notefication to the user
        startForeground(1,sendNotification("No Reminders"));

        // Check for transactions with type "pending" and time passed 1 hour
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    List<Transaction> allTransactions = mainActivity.getAllList();
                    for (Transaction transaction : allTransactions) {
                        if (transaction.getType().equals("pending")) {
                            long currentTimeMillis = System.currentTimeMillis();
                            long transactionTimeMillis = transaction.getMinute();
                            if (currentTimeMillis - transactionTimeMillis > 60000) {
                                sendNotification("Transaction Passed: " + transaction.getTitle());
                            }
                        }
                    }
                    try {
                        Thread.sleep(60000); // Check every minute
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


        return START_STICKY;
    }

    public void CreateNotificationChannel(){
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(notificationChannel);

    }

    public Notification sendNotification(String title) {
        NotifyBuilder = new Notification.Builder(this,CHANNEL_ID)
                .setAutoCancel(true)
                .setContentText("Test service")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Don't Forget " + title);

        return NotifyBuilder.build();
    }
    public void setMainActivityCallback(TransactionAdapterListener callback) {
        mainActivityCallback = callback;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
