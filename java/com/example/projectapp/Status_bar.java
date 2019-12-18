package com.example.projectapp;



import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.hitomi.cmlibrary.CircleMenu;
import com.hitomi.cmlibrary.OnMenuSelectedListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;


public class Status_bar extends AppCompatActivity {
    String names[]={"Alcohol", "Cannabis", "Nicotine"};
    CircleMenu circleMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status_bar);



        circleMenu=(CircleMenu)findViewById(R.id.circleMenu);

        circleMenu.setMainMenu(Color.parseColor("#CDCDCD"),R.drawable.add,R.drawable.remove)
                .addSubMenu(Color.parseColor("#258CFF"), R.drawable.drink)
                .addSubMenu(Color.parseColor("#ff0000"), R.drawable.cannabis)
                .addSubMenu(Color.parseColor("#00a9f4"), R.drawable.smoke)

                .setOnMenuSelectedListener(new OnMenuSelectedListener() {
                    @Override
                    public void onMenuSelected(int index) {
                        Toast.makeText(getApplicationContext(), "You selected" + names[index], Toast.LENGTH_SHORT).show();
                    }
                });
        final Button addDrink= findViewById(R.id.buttonAdd);
        final ProgressBar pb=findViewById(R.id.progressBar);
        final String channel_ID="personal notifications";
        final int notification_id=001;
        final Button deleteDrink= findViewById(R.id.buttonRemove);
        addDrink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pb.getProgress()<90) {
                    deleteDrink.setEnabled(true);
                    pb.setProgress(pb.getProgress() + 10);
                    Toast.makeText(getApplicationContext(), "You just got more Tipsy, Remember your Limits! ", Toast.LENGTH_SHORT).show();
                }
                else {
                    pb.setIndeterminate(true);
                    addDrink.setEnabled(false);
                    deleteDrink.setEnabled(false);

                    Toast.makeText(getApplicationContext(), "Your Blood is Full with alcohol", Toast.LENGTH_LONG).show();
                    MediaPlayer ring= MediaPlayer.create(Status_bar.this,R.raw.notify);
                    ring.start();


                }
            }
        });


        deleteDrink.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pb.getProgress()==10)
                {
                    deleteDrink.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Nothing to remove", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    deleteDrink.setEnabled(true);
                }
                pb.setProgress(pb.getProgress()-10);
            }
        });





    }

}
