package com.example.firebasetutorial;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPassActivity extends AppCompatActivity {

    @BindView(R.id.textInputEditText)
    TextInputEditText textEmail;
    @BindView(R.id.button)
    AppCompatButton button;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        creatProgressDialog();
    }

    private void creatProgressDialog()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }

    @OnClick(R.id.button)
    public void onClick()
    {

        if(textEmail.getText().toString().equals(""))
            Toast.makeText(this, "لطفا ایمیل خود را وارد کنید", Toast.LENGTH_SHORT).show();
        else
         whenForgetPass(textEmail.getText().toString()) ;
    }

    public void whenForgetPass(String email)
    {
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if(task.isSuccessful())
                    Toast.makeText(ForgetPassActivity.this, "پسورد به ایمیل ارسال شد", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(ForgetPassActivity.this, "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }
}