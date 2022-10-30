package com.example.firebasetutorial.imageLoad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.firebasetutorial.R;
import com.example.firebasetutorial.SignUpActivity;
import com.example.firebasetutorial.factory.Datas;
import com.example.firebasetutorial.factory.RecievedFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class LoadPicActivity extends AppCompatActivity {
    ImageView imgCamera;
    public static final int REQUEST_CODE=1;

    Uri filePAth;
    String pathName;
    String fileName;

    ImageView imageProfile;
    ProgressDialog progressDialog;

    StorageReference storageReference;
    DatabaseReference databaseReferenceImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_pic);

        storageReference = FirebaseStorage.getInstance().
                getReference(Constants.STOARAGE_PATH);
        databaseReferenceImage= FirebaseDatabase.getInstance().
                getReference(Constants.DATABASE_PATH);

        creatProgressDialog();
        imageProfile = findViewById(R.id.imageProfile);
        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(view ->
        {
            choosePic();
        });
    }
    private void creatProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("درحال ارتباط با سرور...");
    }

    private void choosePic()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"لطفا عکس خود را انتخاب کنید"),
                REQUEST_CODE);
    }

    private static int getExifOrientation(String src) throws IOException {

        int orientation = 1;
        ExifInterface exif = new ExifInterface(src);
        String orientationString=exif.getAttribute(ExifInterface.TAG_ORIENTATION);

        try {
            orientation = Integer.parseInt(orientationString);
        }
        catch(NumberFormatException e){}

        return orientation;
    }


    public static Bitmap rotateBitmap(String src, Bitmap bitmap)
    {
        try {
            int orientation = getExifOrientation(src);
            if (orientation == 1)
            {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:

                    matrix.setRotate(180);
                    break;
                case 4:

                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:

                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:

                    matrix.setRotate(90);
                    break;
                case 7:

                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:

                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {

                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        return bitmap;
    }

    private String getFileExtension(Uri filePAth)
    {
        ContentResolver resolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(filePAth));
    }

    public void uploadFile()
    {
        if(filePAth != null)
        {

            fileName="img"+System.currentTimeMillis();
            StorageReference sRef= storageReference.child
                    (Constants.STOARAGE_PATH +
                    fileName+"."+getFileExtension(filePAth));
            //Log.d(TAG, "sRef.putFile: ");
            progressDialog.show();
            sRef.putFile(filePAth).addOnSuccessListener
                    (new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {

                    Toast.makeText(LoadPicActivity.this, "باموفقیت اپلود شد", Toast.LENGTH_SHORT).show();
                    Task<Uri> result=taskSnapshot.getMetadata().getReference().
                            getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri)
                        {

                            if(!uri.equals(""))
                            {
                                String link=uri.toString();
                                String id = databaseReferenceImage.push().getKey();
                                ///UploadModel uploadModel=new UploadModel(fileName,link,id);
                                Datas uploadModel=
                                        RecievedFactory.createUploadImage(fileName,link,id);
                                databaseReferenceImage.child(id).setValue(uploadModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        //sharedInfo.saveInfo(id);
                                        Toast.makeText(LoadPicActivity.this,
                                                "به دیتابیس اضافه شد", Toast.LENGTH_SHORT).show();
                                        //Log.d(TAG, "onComplete: all");
                                        progressDialog.dismiss();
                                    }
                                });
                            }

                        }
                    });
                }

            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode== RESULT_OK && data != null)
        {
            filePAth = data.getData();
            pathName = FileChooser.getPath(this,filePAth);  /// getRealPathFromURI(filePAth,this);

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),
                        filePAth);
                imageProfile.setImageBitmap(rotateBitmap(pathName,bitmap));
                uploadFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}