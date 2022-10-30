package com.example.firebasetutorial;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.firebasetutorial.Note.NoteActivity;
import com.example.firebasetutorial.imageLoad.PicturesActivity;
import com.example.firebasetutorial.phoneAuth.PhoneAuthActivity;
import com.example.firebasetutorial.update.UpdateActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.textInputEditText2)
    TextInputEditText textInputEditText2;


    FirebaseAuth firebaseAuth;
    ResideMenu resideMenu;
    ResideMenuItem home,profile,setting, crashApply, updateApp, picturesMenu;

    public static final String TAG = "aaa";
    ProgressDialog progressDialog;
    @BindView(R.id.imgg)
    ImageView imgg;
    @BindView(R.id.button)
    AppCompatButton button;
    @BindView(R.id.txtForgetPass)
    TextView txtForgetPass;

    public static final int PERM=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        ButterKnife.bind(this);


        firebaseAuth = FirebaseAuth.getInstance();
        creatProgressDialog();
        creatMenu();

        checkPermissions();


    }



    private void checkPermissions()
    {
        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERM);
        }
        else
            Toast.makeText(this, "قبلا مجوزها داده شده است", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERM)
        {
            for (int i = 0; i < grantResults.length; i++)
            {
//                if(grantResults[i]==PackageManager.PERMISSION_GRANTED)
//                    Toast.makeText(this, "allowd", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(this, "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void creatProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }


    private void checkLogin(String txtEmail, String txtPass) {
        Log.d(TAG, "checkLogin: ");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(txtEmail, txtPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "onComplete: ");
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "کاربر با موفقیت وارد شد", Toast.LENGTH_SHORT).show();
                    //Log.d(TAG, "onComplete: success");
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    Log.d(TAG, "onComplete: " + firebaseUser.getEmail() +
                            " uid : " + firebaseUser.getUid());
                } else {
                    Toast.makeText(MainActivity.this, "یک خطایی رخ افتاده است", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: " + task.getException().toString());
                }
                progressDialog.dismiss();
            }
        });
    }

    private void creatMenu() {
        resideMenu = new ResideMenu(this);
        resideMenu.setBackgroundColor(getResources().getColor(R.color.accent));
        resideMenu.attachToActivity(this);
        resideMenu.setScaleValue(0.6f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        home = new ResideMenuItem(this, R.drawable.ic_baseline_assignment_ind_24, "ثبت نام");
        profile = new ResideMenuItem(this, R.drawable.phone, "ثبت نام با شماره تلفن");
        setting = new ResideMenuItem(this, R.drawable.note, "یادداشت ها");
        crashApply=new ResideMenuItem(this, R.drawable.log,"crashes");
        updateApp=new ResideMenuItem(this,R.drawable.update,"بروزرسانی");
        picturesMenu = new ResideMenuItem(this,R.drawable.pic_icon,"تصاویر");


        resideMenu.addMenuItem(home, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(profile, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(setting, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(crashApply,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(updateApp,ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(picturesMenu,ResideMenu.DIRECTION_LEFT);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///Toast.makeText(MainActivity.this, "home", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,
                        SignUpActivity.class));
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,
                        PhoneAuthActivity.class));
            }
        });
        setting.setOnClickListener(view ->
        {
            startActivity(new Intent(MainActivity.this,
                    NoteActivity.class));

        });

        crashApply.setOnClickListener(view ->
        {
            startActivity(new Intent(this,Alaki.class));
        });

        updateApp.setOnClickListener(view ->
        {
            //startActivity(new Intent(this, UpdateActivity.class));

            UpdateActivity updateActivity= new UpdateActivity(this);
            updateActivity.ConfigUpdateApp();
        });

        picturesMenu.setOnClickListener(view ->
        {
            startActivity(new Intent(this, PicturesActivity.class));
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @OnClick({R.id.imgg, R.id.button, R.id.txtForgetPass})
    public void onClick(View view)
    {
        switch (view.getId()) {
            case R.id.imgg:
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                break;
            case R.id.button:
                clickOnVorud();
                break;
            case R.id.txtForgetPass:
                startActivity(new Intent(MainActivity.this,ForgetPassActivity.class));
                break;

        }
    }
    public void clickOnVorud()
    {

        Log.d(TAG, "onClick: ");
        String txtEmail = textInputEditText.getText().toString();
        String txtPass = textInputEditText2.getText().toString();

        if (!txtEmail.isEmpty())
        {
            if (!txtPass.isEmpty())
            {
                checkLogin(txtEmail, txtPass);
            } else
                Toast.makeText(this, "لطفا پسورد وارد کنید", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "لطفا ایمیل را وارد کنید", Toast.LENGTH_SHORT).show();
        }
    }
}