package com.example.firebasetutorial.phoneAuth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.firebasetutorial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneAuthActivity extends AppCompatActivity {

    public static final String TAG="aaa";
    
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.editTextPhone)
    EditText editTextPhone;
    @BindView(R.id.button)
    AppCompatButton button;

    EditText edtOTP;

    private FirebaseAuth mAuth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        ButterKnife.bind(this);
        edtOTP =findViewById(R.id.edtCode);

        mAuth = FirebaseAuth.getInstance();

        spinner.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                CountryData.CountryNames
                ));
        spinner.setSelection(0);

    }

    @OnClick(R.id.button)
    public void onClick()
    {
//        String phone=editTextPhone.getText().toString();
//        String code=CountryData.ContryCode[spinner.getSelectedItemPosition()];
//        String phoneComplete="+"+code+phone;
//        if(phone.isEmpty() || phone.length() < 6)
//        {
//            editTextPhone.setError("لطفا شماره معتبر وارد کنید");
//            return;
//        }
//
//        Intent intent = new Intent(PhoneAuthActivity.this,
//                VerifyPhoneActivity.class);
//        intent.putExtra(CountryData.KeyCountryCode,phoneComplete);
//        startActivity(intent);

        getOTP();

    }

    public void getOTP()
    {
        if (TextUtils.isEmpty(editTextPhone.getText().toString()))
        {
            // when mobile number text field is empty
            // displaying a toast message.
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // if the text field is not empty we are calling our
            // send OTP method for getting OTP from Firebase.
            String phone = "+98" + editTextPhone.getText().toString();
            sendVerificationCode(phone);
        }
    }

    private void sendVerificationCode(String number)
    {
        Log.d(TAG, "sendVerificationCode: "+number);
        // this method is used for getting
        // OTP on user phone number.
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)		 // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)				 // Activity (for callback binding)
                        .setCallbacks(mCallBack)		 // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new
            PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s,
                               PhoneAuthProvider.ForceResendingToken
                                       forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            Log.d(TAG, "onCodeSent: ");
            verificationId = s;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential
                                                    phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            Log.d(TAG, "onVerificationCompleted: ");
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null)
            {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                edtOTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            Log.d(TAG, "onVerificationFailed: "+e.getMessage());
            Toast.makeText(PhoneAuthActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifyCode(String code) {
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential)
    {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Intent i = new Intent(PhoneAuthActivity.this,
                                    VerifyPhoneActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Toast.makeText(PhoneAuthActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}