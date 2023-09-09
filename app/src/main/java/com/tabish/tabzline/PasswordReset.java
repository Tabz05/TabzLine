package com.tabish.tabzline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class PasswordReset extends AppCompatActivity {

    //initializing authentication variables
    FirebaseAuth frbAuth;

    //initializing xml variables
    TextView passwordResetText;
    EditText passwordResetEmail;
    Button passwordResetButton;

    //initializing String variables
    String email_id;

    public void SubmitPasswordReset (View view) //when reset password button is clicked
    {
        email_id= passwordResetEmail.getText().toString(); //getting the value of email entered by the user and storing it in string "email_id"

        if (!email_id.isEmpty() ) { //if email_id is not empty
            frbAuth.sendPasswordResetEmail(email_id) //sending password reset email to email_id
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) { //if password reset mail is sent
                                passwordResetText.setText("An email has been sent to " + email_id + " with further instructions");
                            } else {
                                //if password reset fails, displaying a message to the user
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();

                                passwordResetText.setText("not sent " + e.getMessage());
                            }
                        }
                    });
        }
        else //if email_id is empty
        {
            Toast.makeText(this,"please enter email id",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        //connecting to firebase authentication
        frbAuth = FirebaseAuth.getInstance();

        //assigning the xml variables to their corresponding ids
        passwordResetText=findViewById(R.id.passwordResetText);
        passwordResetEmail=findViewById(R.id.passwordResetEmail);
        passwordResetButton=findViewById(R.id.passwordResetButton);
    }
}