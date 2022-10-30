package com.example.firebasetutorial.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.firebasetutorial.MainActivity;
import com.example.firebasetutorial.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CustomMassageService extends FirebaseMessagingService
{
    public static final String TAG="aaa";
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        Log.d(TAG, "onMessageReceived: "+message.getFrom());

        String title=message.getNotification().getTitle();
        String body=message.getNotification().getBody();

        Log.d(TAG, "onMessageReceived: "+title+" , "+body);
        simpleNoti(title,body);
    }

    public String createNotificationChannel()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String channelId="Channel_id";
            CharSequence channelName="Application_name";

            String channelDescription="Application_name_Alert";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            boolean channelEnableVibrate=true;

            NotificationChannel notificationChannel=new
                    NotificationChannel(channelId,channelName,
                    channelImportance);
            notificationChannel.setDescription(channelDescription);
            notificationChannel.enableVibration(channelEnableVibrate);

            NotificationManager notificationManager=
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager!=null;
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        }else {
            return null;
        }
    }

    //==============================================================================================

    void simpleNoti(String title,String body)
    {
        
        String channel_id=createNotificationChannel();
        Log.d(TAG, "simpleNoti: first");

        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,
                1,intent,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder=new NotificationCompat.
                Builder(this,channel_id);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setSmallIcon(R.drawable.notification);
        builder.setTicker("Simple Ticker");
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setContentIntent(pendingIntent);

        Notification notification=builder.build();
        NotificationManager manager=(NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1234,notification);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForeground(0,notification);
        Log.d(TAG, "simpleNoti: end");

    }
}
