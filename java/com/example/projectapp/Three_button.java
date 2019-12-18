package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Three_button extends AppCompatActivity {

    private ImageButton alc;
    private ImageButton nic;
    private ImageButton can;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpagebutton);
        alc=findViewById(R.id.button_alcohol);
        alc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Three_button.this, actvity_alcohol.class);
                startActivity(intent);
            }
        });
        nic=findViewById(R.id.button_nicotine);
        nic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Three_button.this, activity_tobacco.class);
                startActivity(intent);
            }
        });
        can=findViewById(R.id.button_cannabis);
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Three_button.this, activity_cannabis.class);
                startActivity(intent);
            }
        });

    }

}


