package com.example.firebasetutorial.update;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.firebasetutorial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

public class UpdateActivity {

    public static final String KEY_NAME="key_name";
    public static final String TAG="aaa";
    FirebaseRemoteConfig remoteConfig;
    String Version_Code_Key="latest_app_version";
    String App_Url_link="url_app";
    HashMap<String,Object> firebaseDefault;
    int latestAppVersion;
    DownloadManager downloadManager;
    Context context;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_update);
//
//        //RemoteConfigSimple();
//        ConfigUpdateApp();
//
//    }


    public UpdateActivity(Context context)
    {
        this.context=context;
    }

    public void ConfigUpdateApp()
    {
        remoteConfig=FirebaseRemoteConfig.getInstance();

//        firebaseDefault= new HashMap<>();
//        firebaseDefault.put(Version_Code_Key, getCurrentVersion());
//        remoteConfig.setDefaultsAsync(firebaseDefault);
//        //remoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder().build());

        remoteConfig.fetch(24).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    remoteConfig.fetchAndActivate();
                    String appUrl=remoteConfig.getString(App_Url_link);
                    latestAppVersion = (int)remoteConfig.getDouble(Version_Code_Key);
                    Log.d(TAG, "onComplete: "+appUrl);
                    checkForUpdate(appUrl);
                }
            }
        });

    }

    private void checkForUpdate(String url)
    {
        Log.d(TAG, "checkForUpdate: latestAppVersion "+latestAppVersion);
        Log.d(TAG, "getCurrentVersion: "+getCurrentVersion());
        if(latestAppVersion > getCurrentVersion())
        {
            new AlertDialog.Builder(context)
                    .setTitle("بروزرسانی برنامه")
                    .setMessage("لطفا نسخه جدید برنامه را دانلود کنید")
                    .setPositiveButton("بروزرسانی", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DownlaodApp(url);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        else
        {
            Toast.makeText(context, "برنامه شما آخرین نسخه می باشد.", Toast.LENGTH_SHORT).show();
        }
    }

    private void DownlaodApp(String url)
    {
        downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri=Uri.parse(url);
        DownloadManager.Request request= new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        downloadManager.enqueue(request);
        Toast.makeText(context, "دانلود انجام شد", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "DownlaodApp: end");

    }


    private int getCurrentVersion()
    {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),0).versionCode;
        }catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    private void RemoteConfigSimple()
    {
        remoteConfig=FirebaseRemoteConfig.getInstance();
        remoteConfig.fetch(1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    remoteConfig.fetchAndActivate();
                    String value=remoteConfig.getString(KEY_NAME);
                    Log.d(TAG, "onComplete: "+value);
                }
            }
        });
    }
}