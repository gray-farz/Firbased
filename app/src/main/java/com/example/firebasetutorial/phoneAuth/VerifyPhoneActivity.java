package com.example.firebasetutorial.phoneAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasetutorial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth firebaseAuth;
    String phoneNumber, verificationId;
    EditText edtPhone;
    Button btnVerify;
    TextView txtTimer, txtResendCode;
    ProgressDialog progressDialog;
    public static final String TAG="aaa";
    long timeCount;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        edtPhone =findViewById(R.id.editTextPhone);
        btnVerify = findViewById(R.id.btnVerify);
        txtTimer = findViewById(R.id.txtTimer);
        txtResendCode = findViewById(R.id.txtResend);
        creatProgressDialog();

        firebaseAuth = FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra(CountryData.KeyCountryCode);
        //Toast.makeText(this, "number "+phoneNumber, Toast.LENGTH_SHORT).show();
        sendVerificationCode(phoneNumber);
        getTime();

        btnVerify.setOnClickListener(this);
    }

    private void getTime()
    {
        timeCount=60000;
        timer= new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timeCount -= 1000;
                        if(timeCount >= 0)  txtTimer.setText(getTimeStyle(timeCount));
                        else
                        {
                            timer.cancel();
                            txtResendCode.setVisibility(View.VISIBLE);
                            txtResendCode.setOnClickListener(view ->
                            {
                                finish();
                            });
                        }
                    }
                });

            }
        },0,1000);
    }

    private String getTimeStyle(long timeCount)
    {
        int all= (int) (timeCount/1000);
        int min= (int) (all/60);
        int sec = (all%60);
        return String.valueOf(min)+":"+String.valueOf(sec);
    }

    private void creatProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }

    private void sendVerificationCode(String number)
    {
        //number = "00989132319749";
        Log.d(TAG, "sendVerificationCode: "+number);
        progressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,60, TimeUnit.SECONDS, this,mycallback
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mycallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            //super.onCodeSent(s, forceResendingToken);
            progressDialog.dismiss();
            Toast.makeText(VerifyPhoneActivity.this, "کد ارسال شد", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onCodeSent: "+s);
            timer.cancel();
            txtTimer.setText("00:00");
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted: ");
            String code= phoneAuthCredential.getSmsCode();
            if(code != null)
            {
                edtPhone.setText(code);
                verifyCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e)
        {
            progressDialog.dismiss();
            Toast.makeText(VerifyPhoneActivity.this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onVerificationFailed: "+e.getMessage().toString());
        }
    };

    private void verifyCode(String code)
    {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneNumber(credential);
    }

    private void signInWithPhoneNumber(PhoneAuthCredential credential)
    {
        progressDialog.show();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful())
                {
                    Log.d(TAG, "onComplete signInWithCredential: ");
                    Toast.makeText(VerifyPhoneActivity.this, "ثبت نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(VerifyPhoneActivity.this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "task is not successful: "+task.getException().getMessage());
                }


            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnVerify:
                onConfirmClicked() ;
                break;
        }
    }

    private void onConfirmClicked()
    {
        String code = edtPhone.getText().toString();
        if(code.isEmpty() || code.length()<6)
        {
            edtPhone.setError("لطفا کد را درست وارد کنید");
            return;
        }
        verifyCode(code);
    }
}