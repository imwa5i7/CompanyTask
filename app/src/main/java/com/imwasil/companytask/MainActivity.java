package com.imwasil.companytask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final int IMAGE_PICK_GALLERY_CODE = 102;
    private Button btnAddImage, btnSend;
    private EditText edCommentBox;
    private ImageView imgView;
    private DatabaseReference mRef;
    private Uri pickedImageUri;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ini views
        iniViews();
        //ini firebase
        mRef = FirebaseDatabase.getInstance().getReference("Data").push();


        btnAddImage.setOnClickListener(this);
        btnSend.setOnClickListener(this);


    }






    private void iniViews() {
        btnAddImage = findViewById(R.id.btn_add_image_id);
        btnSend = findViewById(R.id.btn_send_id);
        edCommentBox = findViewById(R.id.ed_comment_box_id);
        imgView = findViewById(R.id.img_view_id);
        mProgress=findViewById(R.id.pgr_id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_image_id:
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                checkAndRequestPermission();
                else
                    selectFromGallery();
                break;
            case R.id.btn_send_id:
                createData();
                break;

        }

    }
    private void createData(){

        final String comment=edCommentBox.getText().toString();
        String fileName="image_"+System.currentTimeMillis()+"";


        if (!comment.isEmpty() && pickedImageUri != null){
            mProgress.setVisibility(View.VISIBLE);
            btnSend.setVisibility(View.INVISIBLE);
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("IMAGES");
            final StorageReference imageFilePath = storageReference.child(fileName);
            imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageDownloadLink = uri.toString();
                            Data data=new Data(comment,imageDownloadLink);
                            addData(data);



                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

        }
        else {
            if (comment.isEmpty()){
                Toast.makeText(this, "Write a Comment", Toast.LENGTH_SHORT).show();
            }
            if (pickedImageUri==null){
                Toast.makeText(this, "Choose an Image", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private  void addData(Data data){
        final String key=mRef.getKey();
        data.setId(key);
        mRef.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Upload Complete", Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.INVISIBLE);
                btnSend.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Failed to Upload "+e.getMessage(), Toast.LENGTH_SHORT).show();
                mProgress.setVisibility(View.INVISIBLE);
                btnSend.setVisibility(View.VISIBLE);

            }
        });
    }

    private void selectFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE && data !=null) {
                pickedImageUri = data.getData();
                imgView.setImageURI(pickedImageUri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void checkAndRequestPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this,READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    READ_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Accept the permission to proceed!", Toast.LENGTH_SHORT).show();
                //that will send us to app settings if permission is denied
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

            }
            else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
            }
        }
        else {
            selectFromGallery();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode== STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { ;
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.icon_list){
            startActivity(new Intent(MainActivity.this,ListActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);

    }
}