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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth frbAuth;
    FirebaseUser currentUser;
    FirebaseFirestore db;

    TextView welcomeUser;

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


    //requesting permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Intent goToMain = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(goToMain);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //this is like a global variable being declared to be able to use
        //menu bar anywhere in program

        MenuInflater menuInflater = getMenuInflater();

        if(currentUser!=null)
        {
            menuInflater.inflate(R.menu.menu_bar_logged_in,menu);
        }
        else
        {
            menuInflater.inflate(R.menu.menu_bar, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //when an item of menubar is selected
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {


            case R.id.aboutus:
                Toast.makeText(this, "About Us", Toast.LENGTH_LONG).show();
                Intent goToAboutUs = new Intent(getApplicationContext(), AboutUs.class);

                startActivity(goToAboutUs);

                return true;

            case R.id.editProfile:
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {
                        try {
                            //Toast.makeText(this, "Edit Profile", Toast.LENGTH_LONG).show();
                            Intent goToEditProfile = new Intent(getApplicationContext(), EditProfile.class);

                            startActivity(goToEditProfile);
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(MainActivity.this,"err: "+e,Toast.LENGTH_LONG).show();
                        }

                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }
                return true;

            case R.id.signUp:
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {
                        Toast.makeText(this, "Sign Up", Toast.LENGTH_LONG).show();
                        Intent goToSignUp = new Intent(getApplicationContext(), SignUp.class);
                        startActivity(goToSignUp);

                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }


                return true;

            case R.id.signIn:
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {


                        Toast.makeText(this, "Sign In", Toast.LENGTH_LONG).show();
                        Intent goToSignIn = new Intent(getApplicationContext(), SignIn.class);
                        startActivity(goToSignIn);
                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }

                return true;

            case R.id.newGroup:
                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {


                        Toast.makeText(this, "Select participants", Toast.LENGTH_LONG).show();
                        Intent goToSignIn = new Intent(getApplicationContext(), UserList.class);
                        startActivity(goToSignIn);
                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }

                return true;


            case R.id.signOut:

                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {

                        frbAuth.signOut();
                        Intent goToMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(goToMainActivity);

                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }

                return true;

            case R.id.myChats:

                if (ContextCompat.checkSelfPermission(this,Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET}, 1);
                }
                else
                {
                    if(isNetworkAvailable())
                    {
                        Intent goToMyChats = new Intent(getApplicationContext(),MyChats.class);
                        startActivity(goToMyChats);
                    }
                    else
                    {
                        Toast.makeText(this, "No internet access", Toast.LENGTH_LONG).show();
                    }

                }

                return true;

            default:
                return false;
        }
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
        setContentView(R.layout.activity_main);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        welcomeUser=findViewById(R.id.welcomeUser);

        if(currentUser!=null)
        {
            db.collection("users").document(currentUser.getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String currUsername = (String) document.get("username");
                            welcomeUser.setText("Welcome "+currUsername);

                        } else {
                            //   Log.d(TAG, "No such document");
                        }
                    } else {
                        //Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        }
        else
        {
            welcomeUser.setText("Welcome, Sign in to chat with people");
        }
    }
}