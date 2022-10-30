package com.example.firebasetutorial;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.firebasetutorial.factory.RecievedFactory;
import com.example.firebasetutorial.factory.Datas;
import com.example.firebasetutorial.imageLoad.Constants;
import com.example.firebasetutorial.imageLoad.FileChooser;
import com.example.firebasetutorial.imageLoad.UploadModel;
import com.example.firebasetutorial.model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.textInputEditText)
    TextInputEditText textInputEditText;
    @BindView(R.id.textInputEditText2)
    TextInputEditText textInputEditText2;
    @BindView(R.id.button)
    AppCompatButton button;

    ProgressDialog progressDialog;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceImage;
    StorageReference storageReference;

    public static final String TAG="aaa";

    SharedInfo sharedInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);

        sharedInfo=new SharedInfo(this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceImage=FirebaseDatabase.getInstance().
                getReference(Constants.DATABASE_PATH);
        storageReference = FirebaseStorage.getInstance().getReference(Constants.STOARAGE_PATH);

        creatProgressDialog();

    }

    private void creatProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }

    @OnClick(R.id.button)
    public void onClick()
    {
        clickOnSabtenam();
        //uploadFile();
    }
    public void clickOnSabtenam()
    {
        progressDialog.show();
        String txtEmail = textInputEditText.getText().toString();
        String txtPass = textInputEditText2.getText().toString();

        if (!txtEmail.isEmpty()) {
            if (!txtPass.isEmpty()) {
                getCheckSignUp(txtEmail, txtPass);
            } else
                Toast.makeText(this, "لطفا پسورد وارد کنید", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "لطفا ایمیل را وارد کنید", Toast.LENGTH_SHORT).show();
        }
    }

    private void getCheckSignUp(String txtEmail, String txtPass)
    {

        firebaseAuth.createUserWithEmailAndPassword(txtEmail,txtPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this,
                                    "ثبت نام با موفقیت انجام شد", Toast.LENGTH_SHORT).show();
                            FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                            String uid=firebaseUser.getUid();
                            //Users users=new Users("new",txtPass,txtEmail,"");
                            Datas users=RecievedFactory.createUsers(txtPass,txtEmail);
                            Log.d(TAG, "onComplete signup: ");
                            databaseReference.child("2").setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete
                                                (@NonNull Task<Void> task)
                                        {
                                            Log.d(TAG, "onComplete: setvalue to database");
                                            if(task.isSuccessful())
                                            {
                                                Toast.makeText(SignUpActivity.this,
                                                        "مقدار موردنظر به دیتابیس اضافه شد", Toast.LENGTH_SHORT).show();
                                                Log.d(TAG, "success: ");
                                            }
                                            else
                                            {
                                                Toast.makeText(SignUpActivity.this,
                                                        "خطا رخ داده است", Toast.LENGTH_SHORT).show();
                                                ////Log.d(TAG, "fail database: " + task.getException().toString());
                                                Log.d(TAG, "onComplete: " + task.getException().toString());
                                            }
                                        }
                                    }
                            );
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this,
                                    "خطایی رخ داده است", Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

}