package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainPage extends AppCompatActivity {

    Button btnLogin, btnReg;
    ImageView tipsylogo;
     MainPage mainpage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_main);

        mainpage = this;
        btnLogin=findViewById(R.id.button_login);
        btnReg=findViewById(R.id.button_reg);
        tipsylogo = (ImageView)findViewById(R.id.tipsylogo);

        //when Login button is pressed, it goes to Email login page
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainPage.this, EmailLogin.class));
            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }
}
