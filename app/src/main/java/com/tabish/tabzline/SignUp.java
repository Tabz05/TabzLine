package com.tabish.tabzline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private String username;
    private String email;
    private String pass;

    private EditText usernameSignUp;
    private EditText emailSignUp;
    private EditText passSignUp;
    private TextView textsignup;

    private Button signUpButton;

    private CountDownTimer countDownTimer;

    private String existing_username;

    private ArrayList<String> usersList;

    private int found=0;

    private void sendEmailVerification() {

        FirebaseUser user = frbAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            textsignup.setText("Registered. A verification email has been sent to "+ email+" Sign In now");
                        }
                        else {
                            FirebaseAuthException e = (FirebaseAuthException) task.getException();

                            textsignup.setText("not Registered " + e.getMessage());
                        }
                    }
                });
    }

    private void createUser()
    {

        usersList= new ArrayList<String>();
        found=0;

        db.collection("usernames")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                existing_username=(String) document.getId();

                                usersList.add(existing_username);
                            }

                            for(int i=0;i<usersList.size();i+=1)
                            {
                                if(usersList.get(i).equals(username))
                                {
                                    found=1;
                                    break;
                                }
                            }

                            if(found==0)
                            {
                                frbAuth.createUserWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {

                                                    sendEmailVerification();

                                                    currentUser = frbAuth.getCurrentUser();

                                                    Map<String, Object> newUsername = new HashMap<>();
                                                    newUsername.put(username, currentUser.getUid());

                                                    db.collection("usernames").document(username)
                                                            .set(newUsername)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    Map<String, Object> newUser = new HashMap<>();
                                                                    newUser.put("username", username);
                                                                    newUser.put("email", email);
                                                                    newUser.put("hasProfilePic", false);
                                                                    newUser.put("joinedOn", new Timestamp(new Date()));

                                                                    db.collection("users").document(currentUser.getUid())
                                                                            .set(newUser)
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    Toast.makeText(SignUp.this, "Sign Up complete", Toast.LENGTH_LONG).show();

                                                                                    countDownTimer = new CountDownTimer(2000, 1000) {

                                                                                        @Override
                                                                                        public void onTick(long l) { //l is no of milliseconds left in timer

                                                                                        }

                                                                                        @Override
                                                                                        public void onFinish() {

                                                                                            Intent goToPicUpload = new Intent(getApplicationContext(), PicUpload.class);
                                                                                            startActivity(goToPicUpload);
                                                                                        }
                                                                                    }.start();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    textsignup.setText("Not registered " + e);
                                                                                }
                                                                            });

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    textsignup.setText("Not registered " + e);
                                                                }
                                                            });

                                                } else {
                                                    // If sign in fails, display a message to the user.

                                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();

                                                    textsignup.setText("Not registered " + e);

                                                }

                                            }
                                        });
                            }
                            else
                            {
                                textsignup.setText("Username is already taken. Try another username");
                            }

                        } else {
                            //Log.i("Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void SignUpClicked(View view)
    {
        username = usernameSignUp.getText().toString();
        email = emailSignUp.getText().toString();
        pass = passSignUp.getText().toString();

        if (!email.isEmpty() && !pass.isEmpty() && !username.isEmpty()) {
            if (pass.toString().length() >= 6) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    createUser();

                } else {
                    Toast.makeText(SignUp.this, "Please enter a valid email id ", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(SignUp.this, "Password must be at least 6 characters long", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(SignUp.this, "Empty Fields Are not Allowed", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        usernameSignUp=findViewById(R.id.usernameSignUp);
        emailSignUp=findViewById(R.id.emailSignUp);
        passSignUp=findViewById(R.id.passSignUp);
        textsignup=findViewById(R.id.textSignUp);

        signUpButton=findViewById(R.id.signUpButton);
    }
}