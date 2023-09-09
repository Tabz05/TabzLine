package com.tabish.tabzline;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;

public class PicUpload extends AppCompatActivity {

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private Uri selectedImage;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ImageView imageView;

    private Button removeProfilePic;

    private void getPhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    //requesting permission

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto();
            }
        }
    }

    public void uploadProfilePic (View view)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //checking if permission for gallery has been granted or not
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            getPhoto();
        }
    }

    public void submitProfilePic(View view)
    {
        if (selectedImage != null) {

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            // Defining the child of storageReference
            StorageReference ref = storageReference.child("users").child(currentUser.getUid()).child("profilePic");

            // adding listeners on upload
            // or failure of image
            ref.putFile(selectedImage).addOnSuccessListener(
                    new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(
                                UploadTask.TaskSnapshot taskSnapshot)
                        {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    Map<String, Object> userProfilePic = new HashMap<>();
                                    userProfilePic.put("hasProfilePic", true);
                                    userProfilePic.put("profilePicUri", uri.toString());

                                    db.collection("users").document(currentUser.getUid())
                                            .update(userProfilePic)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    // Image uploaded successfully
                                                    // Dismiss dialog
                                                    progressDialog.dismiss();
                                                    Toast.makeText(PicUpload.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();

                                                    Intent goToMainActivity = new Intent(getApplicationContext(),MainActivity.class);

                                                    startActivity(goToMainActivity);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    e.printStackTrace();
                                                }
                                            });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(PicUpload.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                                }
                            });
        }
        else
        {
            Toast.makeText(this,"no file",Toast.LENGTH_LONG).show();
        }
    }

    public void skipPicUpload (View view)
    {
        Intent goToMainActivity = new Intent(getApplicationContext(),MainActivity.class);

        startActivity(goToMainActivity);
    }

    public void removeProfilePic(View view)
    {
        imageView.setImageResource(R.drawable.profilepic);
        removeProfilePic.setVisibility(View.INVISIBLE);
        removeProfilePic.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_upload);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageView = findViewById(R.id.profileImageView);
        removeProfilePic=findViewById(R.id.removeProfilePic);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes

                        Intent data = result.getData();

                        selectedImage = data.getData();

                        Glide.with(getApplicationContext()).asBitmap().load(selectedImage.toString()).into(imageView);

                        removeProfilePic.setVisibility(View.VISIBLE);
                        removeProfilePic.setEnabled(true);
                    }
                }
            });
}