package com.example.projectapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class activity_tobacco extends toolbar {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tobacco);
        Spinner spinner_cigarette =(Spinner) findViewById(R.id.cigarette_spinner);
        ArrayList<String> cigarettes=new ArrayList<>();
        cigarettes.add(0, "Choose Cigarette:");
        cigarettes.add("Belmont  0.9mg");
        cigarettes.add("Benson & Hedges  1.03mg");
        cigarettes.add("Canadian Classics  1.2mg");
        cigarettes.add("Du Maurier  1.3mg");
        cigarettes.add("Marlboro  1.9mg");
        cigarettes.add("Camel   0.7mg");
        cigarettes.add("Dunhill   1.4mg");

        ArrayAdapter<String> dataAdapter_cigarette;
        dataAdapter_cigarette= new ArrayAdapter(this, android.R.layout.simple_spinner_item,cigarettes);
        dataAdapter_cigarette.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Button btn_add= findViewById(R.id.button_add_tob);
        Button btn_del= findViewById(R.id.button_delete_tob);


        final ArrayList<String> customized = new ArrayList<>();
        final Spinner spinner_customized =(Spinner) findViewById(R.id.custom_spinner_tob);
        customized.add(0,"Your Smokes:" );
        ArrayAdapter<String> dataAdapter_custom;
        dataAdapter_custom = new ArrayAdapter(this, android.R.layout.simple_spinner_item, customized);
        dataAdapter_custom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_customized.setAdapter(dataAdapter_custom);


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater=LayoutInflater.from(activity_tobacco.this);
                View vv=layoutInflater.inflate(R.layout.activity_add_cannabis,null);
                AlertDialog.Builder alertdialog1=new AlertDialog.Builder(activity_tobacco.this);
                alertdialog1.setView(vv);
                final EditText name = vv.findViewById(R.id.editText_add);
                final NumberPicker n1 = vv.findViewById(R.id.Number_picker_add);
                n1.setMaxValue(100);
                n1.setMinValue(0);

                alertdialog1.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String percentage = n1.getValue()+"%";
                                final String drink = name.getText().toString();

                                SubstanceInfo cust = new SubstanceInfo(drink, percentage,0);


                                customized.add(drink+"   "+percentage);

                                Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }
                        );
                alertdialog1.create();
                alertdialog1.show();

            }
        });
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(activity_tobacco.this);
                View vv = layoutInflater.inflate(R.layout.activity_delete_prompt, null);
                AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(activity_tobacco.this);
                alertdialog1.setView(vv);
                final NumberPicker n1;
                n1 = vv.findViewById(R.id.Number_picker_delete);

                final String[] arr = new String[customized.size() - 1];

                for (int i = 0; i < arr.length; i++) {
                    arr[i] = customized.get(i + 1);
                }

                if (arr.length == 0) {
                    Toast.makeText(getApplicationContext(), "Nothing to delete. Add smokes to delete.", Toast.LENGTH_SHORT).show();
                }
                else{
                    n1.setMaxValue(0);
                    n1.setMaxValue(arr.length - 1);
                    n1.setDisplayedValues(arr);

                    alertdialog1.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final int percentage = n1.getValue();

                                    customized.clear();
                                    customized.add("Your Drinks:");

                                    Toast.makeText(getApplicationContext(), arr[percentage] + "deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }
                            );
                    alertdialog1.create();
                    alertdialog1.show();
                }
            }
        });




        spinner_cigarette.setAdapter(dataAdapter_cigarette);

        spinner_cigarette.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected" + item, Toast.LENGTH_SHORT).show();
                    LayoutInflater layoutInflater=LayoutInflater.from(activity_tobacco.this);
                    View v=layoutInflater.inflate(R.layout.activity_prompt_intake,null);
                    AlertDialog.Builder alertdialog=new AlertDialog.Builder(activity_tobacco.this);
                    alertdialog.setView(v);
                    final NumberPicker n1;
                    n1= v.findViewById(R.id.Number_picker_add);
                    n1.setMaxValue(10);
                    n1.setMinValue(0);
                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }
                            );
                    alertdialog.create();
                    alertdialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Spinner spinner_cigar =(Spinner) findViewById(R.id.cigar_spinner);
        ArrayList<String> cigars=new ArrayList<>();
        cigars.add(0, "Choose Cigar:");
        cigars.add("Arturo Fuente  -mg");
        cigars.add("Padron -mg");
        cigars.add("Ashton  -mg");
        cigars.add("Davidoff  -mg");
        cigars.add("Rocky Patel  -mg");
        cigars.add("Perdomo   -mg");
        cigars.add("Oliva   -mg");

        ArrayAdapter<String> dataAdapter_cigar;
        dataAdapter_cigar= new ArrayAdapter(this, android.R.layout.simple_spinner_item,cigars);
        dataAdapter_cigar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_cigar.setAdapter(dataAdapter_cigar);

        spinner_cigar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "Selected" + item, Toast.LENGTH_SHORT).show();
                    LayoutInflater layoutInflater=LayoutInflater.from(activity_tobacco.this);
                    View v=layoutInflater.inflate(R.layout.activity_prompt_intake,null);
                    AlertDialog.Builder alertdialog=new AlertDialog.Builder(activity_tobacco.this);
                    alertdialog.setView(v);
                    final NumberPicker n1= v.findViewById(R.id.Number_picker);
                    n1.setMaxValue(10);
                    n1.setMinValue(0);
                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_SHORT);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }
                            );



                    alertdialog.create();
                    alertdialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        FirebaseAuth firebaseAuth;
        FirebaseUser user;
        DatabaseReference firebaseReference;
        FirebaseDatabase firebaseDBInstance;

        firebaseAuth=FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDBInstance = FirebaseDatabase.getInstance();
        firebaseReference =  firebaseDBInstance.getReference();

        int id= item.getItemId();
        if (id ==R.id.subitem1)
        {

            Intent intent =new Intent(this, Intake_activity.class);
            startActivity(intent);
            return false;
        }
        else if(id==R.id.subitem2)
        {
            Intent intent =new Intent(this, track_weekly.class);
            startActivity(intent);
            return false;
        }
        else if (id==R.id.subitem3)
        {
            Intent intent =new Intent(this, track_monthly.class);
            startActivity(intent);
            return false;
        }
        else if (id==R.id.item2)
        {
            Intent intent =new Intent(this,Status_bar.class );
            startActivity(intent);
            return false;
        }
        else if(id==R.id.item4)
        {
            Intent intent =new Intent(this,Nutrition.class);
            startActivity(intent);
            return false;
        }
        else if (id==R.id.itemProfile)
        {
            Intent intent =new Intent(this, user_account.class);
            startActivity(intent);
            return false;
        }
        else if(id==R.id.item3)
        {
            firebaseReference.child("users").child(user.getUid()).child("userprofile").child("status").setValue("Logged Off");
            FirebaseAuth.getInstance().signOut();
            Intent intent =new Intent(this, MainPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

}
