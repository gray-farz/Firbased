package com.example.firebasetutorial.imageLoad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.firebasetutorial.R;
import com.example.firebasetutorial.SignUpActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.grpc.internal.AbstractReadableBuffer;

public class PicturesActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recPic;
    GridLayoutManager gridLayoutManager;
    LinearLayoutManager linearLayoutManager;
    public static final String TAG="aaa";
    List<UploadModel> listPicsFirebase=new ArrayList<UploadModel>();
    ProgressDialog progressDialog;
    DatabaseReference databaseReferenceImage;
    PictureAdapter pictureAdapter;
    TextView txtAddPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        databaseReferenceImage= FirebaseDatabase.getInstance().
                getReference(Constants.DATABASE_PATH);

        recPic = findViewById(R.id.recPic);
        txtAddPic=findViewById(R.id.txtAddPic);
        txtAddPic.setOnClickListener(this);

        gridLayoutManager=new GridLayoutManager(this,2);
        recPic.setLayoutManager(gridLayoutManager);
        pictureAdapter=new PictureAdapter(this);

        creatProgressDialog();
        getPicsFromFirebase();

    }

//    @Override
//    public boolean onCreateOptionsMenu(@NonNull Menu menu)
//    {
//        getMenuInflater().inflate(R.menu.menu_add_pic,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item)
//    {
//        switch (item.getItemId())
//        {
//            case R.id.btnAddPic:
//                startActivity(new Intent(this,LoadPicActivity.class));
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }

    private void creatProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }

    private void getPicsFromFirebase()
    {

        if(listPicsFirebase != null)
            listPicsFirebase.clear();
        Log.d(TAG, "getPicsFromFirebase: ");
        progressDialog.show();
        databaseReferenceImage.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Log.d(TAG, "onDataChange: ");
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    UploadModel uploadModel=dataSnapshot.getValue(UploadModel.class);
                    listPicsFirebase.add(uploadModel);
                }

                pictureAdapter.setListPics(listPicsFirebase);
                recPic.setAdapter(pictureAdapter);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Log.d(TAG, "onCancelled in signupActivity: "+error.toString());
                Toast.makeText(PicturesActivity.this, "خطایی روی داده است", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this,LoadPicActivity.class));
    }
}