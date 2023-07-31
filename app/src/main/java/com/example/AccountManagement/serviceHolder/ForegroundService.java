package com.example.AccountManagement.serviceHolder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;


import com.example.AccountManagement.DBHolder.AppDB;
import com.example.AccountManagement.MainActivity;
import com.example.AccountManagement.R;
import com.example.AccountManagement.Transaction;
import com.example.AccountManagement.TransactionViewModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

        // Start the service in the foreground
        startForeground(1, buildNotification("No Reminders"));

        // Check for transactions with type "pending" and time passed 7 days
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    List<Transaction> allTransactions = mainActivity.getAllList();

                    // Filter transactions that have passed over 7 days
                    List<Transaction> reminders = new ArrayList<>();
                    for (Transaction transaction : allTransactions) {
                        Calendar transactionDate = Calendar.getInstance();
                        transactionDate.set(transaction.getYear(), transaction.getMonth() - 1, transaction.getDay());
                        if (transactionDate.getTimeInMillis() <= System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
                        && transaction.getType().equals("Pending")) {
                            reminders.add(transaction);
                        }
                    }

                    // Send notification for each transaction that has passed over 7 days
                    for (Transaction reminder : reminders) {
                        Notification notification = buildNotification("Transaction: " + reminder.getDescription() + " has passed over 7 days.");
                        notificationManager.notify(reminder.hashCode(), notification);
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

    private Notification buildNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Reminder")
                        .setContentText(contentText)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        return notificationBuilder.build();
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
