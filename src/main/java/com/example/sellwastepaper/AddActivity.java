package com.example.sellwastepaper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sellwastepaper.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class AddActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText quantity,desc,address,cell;
    private static final int GALLERY_REQUEST = 100;

    private FirebaseFirestore db;
    private CollectionReference dbPost;
    private String phnNum;
    private Uri selectedImage;

    private ProgressDialog progressDialog;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        imageView = findViewById(R.id.image);
        quantity = findViewById(R.id.quantity);
        desc = findViewById(R.id.description);
        address = findViewById(R.id.address);
        cell = findViewById(R.id.cellphone);

        db = FirebaseFirestore.getInstance();
        dbPost = db.collection("post");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //get saved phone number
        SharedPreferences prefs =  getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        phnNum = prefs.getString("phoneNumber", NULL);

        cell.setText(phnNum);



    }

    public void addNewProduct(View view) {
        boolean allCorrectInput = true;

        if (quantity.getText().toString().isEmpty()){
            Toasty.error(getApplicationContext(), "Empty quantity", Toast.LENGTH_SHORT, true).show();
            allCorrectInput = false;
        }
        else if (desc.getText().toString().isEmpty()){
            Toasty.error(getApplicationContext(), "Empty description", Toast.LENGTH_SHORT, true).show();
            allCorrectInput = false;
        }
        else if (address.getText().toString().isEmpty()){
            Toasty.error(getApplicationContext(), "Empty address", Toast.LENGTH_SHORT, true).show();
            allCorrectInput = false;
        }
        else if (cell.getText().toString().isEmpty()){
            Toasty.error(getApplicationContext(), "Empty cellphone", Toast.LENGTH_SHORT, true).show();
            allCorrectInput = false;
        }

        if (allCorrectInput){
            uploadImage();
        }

    }

    private void uploadImage() {
        if(selectedImage != null)
        {

            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(selectedImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    // getting image uri and converting into string
                                    insertInDb(uri.toString());
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toasty.error(getApplicationContext(), "Failed to save"+e.toString(), Toast.LENGTH_LONG, true).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else{
            Toasty.error(getApplicationContext(), "No image select", Toast.LENGTH_SHORT, true).show();
        }


    }

    private void insertInDb(String url) {
        Post post = new Post(
                quantity.getText().toString().trim(),
                desc.getText().toString().trim(),
                address.getText().toString().trim(),
                cell.getText().toString().trim(),
                url,
                phnNum,
                new Date()
        );

        dbPost.add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toasty.success(getApplicationContext(), "Insert Success", Toast.LENGTH_SHORT, true).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getApplicationContext(), "Insert Failed", Toast.LENGTH_SHORT, true).show();
                    }
                });

        progressDialog.dismiss();

        startActivity(new Intent(this,MainActivity.class));
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
                case GALLERY_REQUEST:
                    selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }
}
