package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class EmailLogin extends AppCompatActivity {
    EditText emailId, password;
    Button btnLogin, forgotpass;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore database;
    FirebaseUser user;
    DatabaseReference firebaseReference;
    FirebaseDatabase firebaseDBInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email_login);

        firebaseAuth=FirebaseAuth.getInstance();
        emailId=findViewById(R.id.button_email);
        password=findViewById(R.id.button_pass);
        btnLogin=findViewById(R.id.button_login);
        forgotpass=findViewById(R.id.button_fmp);

        database = FirebaseFirestore.getInstance();

        firebaseDBInstance = FirebaseDatabase.getInstance();
        firebaseReference =  firebaseDBInstance.getReference();

        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(EmailLogin.this, SendPassword.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                final String pass = password.getText().toString();

                //following lines check input states
                if (email.isEmpty()) {
                    emailId.setError("Please enter your email");
                    emailId.requestFocus();
                } else if (pass.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    password.setText(null);
                    Toasty.error(EmailLogin.this, "Email is not valid", Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && pass.isEmpty())) {
                    //User has to open validation email link to access the home page
                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(EmailLogin.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {//if user enters email and password correctly
                                if (firebaseAuth.getCurrentUser().isEmailVerified()) {

                                    user = firebaseAuth.getCurrentUser();
                                    firebaseReference.child("users").child(user.getUid()).child("userprofile").child("status").setValue("Logged On");

                                    finish();
                                    Intent login = new Intent(getApplicationContext(), Three_button.class);

                                    //prevents back navigation to login page
                                    login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(login);
                                }
                                else {
                                    Toasty.error(EmailLogin.this, "Please verify your email", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else {

                                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                        if(task.getResult().getSignInMethods().equals(Collections.<String>emptyList())){
                                            Toasty.error(EmailLogin.this, "Email not registered", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            Toasty.error(EmailLogin.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }
}