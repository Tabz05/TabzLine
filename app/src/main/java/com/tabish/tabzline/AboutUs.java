package com.tabish.tabzline;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AboutUs extends AppCompatActivity {

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;

    private boolean isNetworkAvailable() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

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
        setContentView(R.layout.activity_about_us);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
    }
}