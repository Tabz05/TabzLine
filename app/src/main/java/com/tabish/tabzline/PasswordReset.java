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

    private FirebaseAuth frbAuth;

    private TextView passwordResetText;
    private EditText passwordResetEmail;
    private Button passwordResetButton;

    private String email_id;

    public void SubmitPasswordReset (View view)
    {
        email_id= passwordResetEmail.getText().toString();
        if (!email_id.isEmpty() ) {
            frbAuth.sendPasswordResetEmail(email_id)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                passwordResetText.setText("An email has been sent to " + email_id + " with further instructions");
                            } else {

                                FirebaseAuthException e = (FirebaseAuthException) task.getException();

                                passwordResetText.setText("not sent " + e.getMessage());
                            }
                        }
                    });
        }
        else
        {
            Toast.makeText(this,"please enter email id",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        frbAuth = FirebaseAuth.getInstance();

        passwordResetText=findViewById(R.id.passwordResetText);
        passwordResetEmail=findViewById(R.id.passwordResetEmail);
        passwordResetButton=findViewById(R.id.passwordResetButton);
    }
}