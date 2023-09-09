package com.tabish.tabzline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MyProfile extends AppCompatActivity {

    FirebaseAuth frbAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    FirebaseStorage storage;
    StorageReference storageReference;

    TextView myProfileText;
    ImageView myProfilePicture;

    LinearLayout linearLayout;

    long i;

    boolean hasProfilePic;

    String profilePicUri;

    public void showProfilePic()
    {
        StorageReference ref = storageReference.child("users").child(currentUser.getUid()).child("image");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(getApplicationContext()).load(uri.toString()).into(myProfilePicture);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public boolean isNetworkAvailable() { // to check if connected to internet
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else
        {
            connected = false;
        }

        return connected;
    }

    public void homeButton(View view)
    {
        if (currentUser != null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            }
            else
            {
                if (isNetworkAvailable()) {
                    Intent goToHome = new Intent(getApplicationContext(), MainActivity.class);

                    startActivity(goToHome);
                } else {
                    Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                }
            }
        }
        else
        {
            Intent goToHome = new Intent(getApplicationContext(), MainActivity.class);

            startActivity(goToHome);
        }
    }

    public void myProfileButton(View view)
    {
        if(currentUser!=null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            } else {
                if (isNetworkAvailable()) {
                    Toast.makeText(this, "Your Profile", Toast.LENGTH_SHORT).show();
                    Intent goToMyProfile = new Intent(getApplicationContext(), MyProfile.class);
                    startActivity(goToMyProfile);

                } else {
                    Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                }

            }
        }
        else
        {
            Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
            Intent goToSignIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(goToSignIn);
        }
    }

    public void findPeopleButton(View view)
    {
        if(currentUser!=null)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1);
            } else {
                if (isNetworkAvailable()) {
                    Toast.makeText(this, "User List", Toast.LENGTH_SHORT).show();
                    Intent goToUserList = new Intent(getApplicationContext(), UserList.class);

                    startActivity(goToUserList);
                } else {
                    Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                }

            }
        }
        else
        {
            Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
            Intent goToSignIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(goToSignIn);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        myProfileText=findViewById(R.id.MyProfileText);
        myProfilePicture=findViewById(R.id.imageView2);

        linearLayout=findViewById(R.id.myProfileLinearLayout);

        //myProfilePicture.setScaleType(ImageView.ScaleType.FIT_XY);

        DocumentReference docRef = db.collection("users").document(currentUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        String currUsername = (String) document.get("username");
                        String email = (String) document.get("email");
                        profilePicUri = (String) document.get("profilePicUri");

                        String info = "Username: "+currUsername+"\n\n"+"Email: "+email+"\n\n"+"Profile picture: ";
                        myProfileText.setText(info.toString());

                        hasProfilePic= (boolean) document.get("hasProfilePic");

                        if(hasProfilePic)
                        {
                            Glide.with(getApplicationContext()).load(profilePicUri.toString()).into(myProfilePicture);
                        }
                        else
                        {
                            //myProfilePicture.setImageResource(R.drawable.profilepic);
                            myProfilePicture.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.profilepic));
                        }

                    } else {
                        //   Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    @Override //to prevent going back when back button is clicked
    public void onBackPressed()
    {

    }
}