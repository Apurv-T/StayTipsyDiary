package com.example.projectapp;



import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class user_account extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    RadioGroup rg;
    Button delete;
    Button update;
    TextView name;
    EditText description;
    String gender;
    String name_changed;
    Editable description_changed;
    private static final int PICK_IMAGE=100;
    Uri imageU;
    ImageButton ib;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference firebaseReference;
    FirebaseDatabase firebaseDBInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        rg=(RadioGroup) findViewById(R.id.radioGroup);
        rg.setOnCheckedChangeListener(this);
        name=findViewById(R.id.editText);
        //name_changed=(String)name.getText();
        description=findViewById(R.id.editText4);
        ib= findViewById(R.id.imageButton);
        //description_changed= description.getText();
        ib.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openGallery();

                // Code here executes on main thread after user presses button
            }
        });

        delete = findViewById(R.id.buttonDeleteAccount);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        firebaseDBInstance = FirebaseDatabase.getInstance();
        firebaseReference =  firebaseDBInstance.getReference();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(user_account.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("Deleting this account will result in completely removing your" +
                        " account from the system and you won't be able to access the app.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firebaseReference.child("users").child(firebaseUser.getUid()).removeValue();
                        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toasty.success(user_account.this, "Account deleted", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(user_account.this, MainPage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                else{
                                    Toasty.error(user_account.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });

                dialog.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });

    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId)
        {
            //male
            case R.id.radioButton:
            {  gender="Male";
                break;
            }

            case R.id.radioButton2:

            {
                gender="Female";
                break;
            }
            case R.id.radioButton3:
            {
                gender="Other";
                break;
            }
        }
    }
    public void openGallery()
    {
        Intent gallery= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        ib= findViewById(R.id.imageButton);
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageU =data.getData();
            ib.setImageURI(imageU);
        }
    }
}
