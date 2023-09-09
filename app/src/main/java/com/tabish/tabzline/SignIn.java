package com.tabish.tabzline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {

    private EditText emailSignIn;
    private EditText passSignIn;
    private TextView forgotPassword;

    private FirebaseAuth frbAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    public void loginUser() {
        String email = emailSignIn.getText().toString();
        String password = passSignIn.getText().toString();
        frbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent goToMainActivity = new Intent (getApplicationContext(),MainActivity.class);
                            startActivity(goToMainActivity);
                        } else {

                            Toast.makeText(SignIn.this,"Sign In Unsuccessful, inavlid credentials",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    public void SignInClicked(View view)
    {
        loginUser();
    }

    public void forgotPasswordClick (View view)
    {
        Intent goToForgotPassword = new Intent(getApplicationContext(),PasswordReset.class);

        startActivity(goToForgotPassword);
    }

    public void signUpClick(View view)
    {
        Intent goToSignUp = new Intent(getApplicationContext(),SignUp.class);

        startActivity(goToSignUp );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailSignIn=findViewById(R.id.emailSignIn);
        passSignIn=findViewById(R.id.passSignIn);
        forgotPassword=findViewById(R.id.ForgotPasswordText);

        frbAuth = FirebaseAuth.getInstance();
        currentUser = frbAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }
}