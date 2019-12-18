package com.example.projectapp;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class actvity_alcohol extends toolbar {

    private NumberPicker n1;

    private FirebaseAuth auth;
    private DatabaseReference firebaseReference;
    private FirebaseDatabase firebaseDBInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alcohol);

        final String user = auth.getInstance().getCurrentUser().getUid();
        firebaseDBInstance = FirebaseDatabase.getInstance();
        firebaseReference = firebaseDBInstance.getReference();

        final Spinner spinner_beer = (Spinner) findViewById(R.id.beer_spinner);

        final ArrayList<String> beers = new ArrayList<>();
        beers.add("Choose Beer: ");

        final Spinner spinner_wine = (Spinner) findViewById(R.id.wine_spinner);

        final ArrayList<String> wines = new ArrayList<>();
        wines.add("Choose Wine: ");

        final Spinner spinner_whiskey = (Spinner) findViewById(R.id.whiskey_spinner);

        final ArrayList<String> whiskeys = new ArrayList<>();
        whiskeys.add("Choose Whiskey: ");

        final Spinner spinner_rum = (Spinner) findViewById(R.id.rum_spinner);

        final ArrayList<String> rums = new ArrayList<>();
        rums.add("Choose Rum: ");

        final ArrayList<String> customized = new ArrayList<>();
        customized.add(0, "Your Drinks:");

        final Spinner spinner_customized = (Spinner) findViewById(R.id.custom_spinner);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ArrayAdapter<String> dataAdapter_beer;
        dataAdapter_beer = new ArrayAdapter(this, android.R.layout.simple_spinner_item, beers);
        dataAdapter_beer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_beer.setAdapter(dataAdapter_beer);


        Button btn_add = findViewById(R.id.button_add_alc);
        Button btn_del = findViewById(R.id.button_delete_alc);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                View vv = layoutInflater.inflate(R.layout.activity_add_drink, null);
                AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(actvity_alcohol.this);
                alertdialog1.setView(vv);
                final EditText name = vv.findViewById(R.id.editText_add);
                n1 = vv.findViewById(R.id.Number_picker_add);
                n1.setMaxValue(100);
                n1.setMinValue(0);

                alertdialog1.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String percentage = n1.getValue() + "%";
                                final String drink = name.getText().toString();

                                if(!drink.equals("")) {
                                    SubstanceInfo cust = new SubstanceInfo(drink, percentage, 0);
                                    firebaseReference.child("users").child(user).child("alcohol").child("customized").child(drink).setValue(cust);
                                    customized.add(drink + "   " + percentage);
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Please enter a name for your drink", Toast.LENGTH_SHORT).show();
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
        });

        final ArrayAdapter<String> dataAdapter_custom;
        dataAdapter_custom = new ArrayAdapter(this, android.R.layout.simple_spinner_item, customized);
        dataAdapter_custom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_customized.setAdapter(dataAdapter_custom);

        spinner_customized.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                firebaseReference.child("users").child(user).child("alcohol").child("customized").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren())
                            if (!customized.contains(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue())))
                                customized.add(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });

                if (position != 0) {
                    final String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected " + item, Toast.LENGTH_SHORT).show();
                    LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                    View v = layoutInflater.inflate(R.layout.activity_prompt_intake, null);
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(actvity_alcohol.this);
                    alertdialog.setView(v);
                    n1 = v.findViewById(R.id.Number_picker);

                    n1.setMaxValue(10);
                    n1.setMinValue(0);
                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                    firebaseReference.child("users").child(user).child("alcohol").child("customized").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            customized.clear();
                                            customized.add("Your Drinks:");
                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                                                String temp = (String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));

                                                if (temp.equals(item)) {
                                                    dsp.child("consumed").getRef().setValue(n1.getValue());
                                                }
                                            }
                                            ArrayAdapter<String> dataAdapter_custom;
                                            dataAdapter_custom = new ArrayAdapter(actvity_alcohol.this, android.R.layout.simple_spinner_item, customized);
                                            dataAdapter_custom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner_customized.setAdapter(dataAdapter_custom);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            spinner_customized.setAdapter(dataAdapter_custom);
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


        spinner_beer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                firebaseReference.child("users").child(user).child("alcohol").child("beer").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren())
                            if (!beers.contains(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue())))
                                beers.add(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });

                if (position != 0) {
                    final String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected " + item, Toast.LENGTH_SHORT).show();

                    LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                    View v = layoutInflater.inflate(R.layout.activity_prompt_intake, null);

                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(actvity_alcohol.this);
                    alertdialog.setView(v);
                    n1 = v.findViewById(R.id.Number_picker);
                    n1.setMaxValue(10);
                    n1.setMinValue(0);

                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                    firebaseReference.child("users").child(user).child("alcohol").child("beer").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            beers.clear();
                                            ;
                                            beers.add("Choose Beer: ");
                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                String temp = (String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));

                                                if (temp.equals(item)) {
                                                    dsp.child("consumed").getRef().setValue(n1.getValue());
                                                }
                                            }
                                            ArrayAdapter<String> dataAdapter_beer;
                                            dataAdapter_beer = new ArrayAdapter(actvity_alcohol.this, android.R.layout.simple_spinner_item, beers);
                                            dataAdapter_beer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner_beer.setAdapter(dataAdapter_beer);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            spinner_beer.setAdapter(dataAdapter_beer);
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

        final ArrayAdapter<String> dataAdapter_whiskey;
        dataAdapter_whiskey = new ArrayAdapter<String>(actvity_alcohol.this, android.R.layout.simple_spinner_item, whiskeys);
        dataAdapter_whiskey.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_whiskey.setAdapter(dataAdapter_whiskey);


        spinner_whiskey.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {

                firebaseReference.child("users").child(user).child("alcohol").child("whiskeys").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren())
                            if (!whiskeys.contains(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue())))
                                whiskeys.add(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });

                if (position != 0) {
                    final String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected " + item, Toast.LENGTH_SHORT).show();

                    LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                    View v = layoutInflater.inflate(R.layout.activity_prompt_intake, null);

                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(actvity_alcohol.this);
                    alertdialog.setView(v);
                    n1 = v.findViewById(R.id.Number_picker);
                    n1.setMaxValue(10);
                    n1.setMinValue(0);
                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, final int which) {
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                                    firebaseReference.child("users").child(user).child("alcohol").child("whiskeys").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            whiskeys.clear();
                                            whiskeys.add("Choose Whiskey: ");
                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                String temp = (String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));

                                                if (temp.equals(item)) {
                                                    dsp.child("consumed").getRef().setValue(n1.getValue());
                                                }
                                            }
                                            ArrayAdapter<String> dataAdapter_whiskey;
                                            dataAdapter_whiskey = new ArrayAdapter<String>(actvity_alcohol.this, android.R.layout.simple_spinner_item, whiskeys);
                                            dataAdapter_whiskey.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner_whiskey.setAdapter(dataAdapter_whiskey);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            spinner_whiskey.setAdapter(dataAdapter_whiskey);
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


        final ArrayAdapter<String> dataAdapter_wine;
        dataAdapter_wine = new ArrayAdapter(this, android.R.layout.simple_spinner_item, wines);
        dataAdapter_wine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_wine.setAdapter(dataAdapter_wine);

        spinner_wine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                firebaseReference.child("users").child(user).child("alcohol").child("wines").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren())
                            if (!wines.contains(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue())))
                                wines.add(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });

                if (position != 0) {
                    final String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected " + item, Toast.LENGTH_SHORT).show();
                    LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                    View v = layoutInflater.inflate(R.layout.activity_prompt_intake, null);
                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(actvity_alcohol.this);
                    alertdialog.setView(v);

                    n1 = v.findViewById(R.id.Number_picker);
                    n1.setMaxValue(10);
                    n1.setMinValue(0);

                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Toast.makeText(getApplicationContext(), n1.getValue() + " Saved", Toast.LENGTH_SHORT).show();

                                    firebaseReference.child("users").child(user).child("alcohol").child("wines").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            wines.clear();
                                            wines.add("Choose Wine: ");
                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                                                String temp = (String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));

                                                if (temp.equals(item)) {
                                                    dsp.child("consumed").getRef().setValue(n1.getValue());
                                                }
                                            }
                                            ArrayAdapter<String> dataAdapter_wine;
                                            dataAdapter_wine = new ArrayAdapter(actvity_alcohol.this, android.R.layout.simple_spinner_item, wines);
                                            dataAdapter_wine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner_wine.setAdapter(dataAdapter_wine);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            spinner_wine.setAdapter(dataAdapter_wine);
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

        final ArrayAdapter<String> dataAdapter_rum;
        dataAdapter_rum = new ArrayAdapter(this, android.R.layout.simple_spinner_item, rums);
        dataAdapter_rum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rum.setAdapter(dataAdapter_rum);

        spinner_rum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, final long id) {


                firebaseReference.child("users").child(user).child("alcohol").child("rums").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot dsp : dataSnapshot.getChildren())
                            if (!rums.contains(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue())))
                                rums.add(String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        databaseError.toException();
                    }
                });

                if (position != 0) {
                    final String item = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(), "Selected " + item, Toast.LENGTH_SHORT).show();

                    LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                    View v = layoutInflater.inflate(R.layout.activity_prompt_intake, null);

                    AlertDialog.Builder alertdialog = new AlertDialog.Builder(actvity_alcohol.this);
                    alertdialog.setView(v);

                    n1 = v.findViewById(R.id.Number_picker);
                    n1.setMaxValue(10);
                    n1.setMinValue(0);

                    alertdialog.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();


                                    firebaseReference.child("users").child(user).child("alcohol").child("rums").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            rums.clear();
                                            rums.add("Choose Rum: ");

                                            for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                String temp = (String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue()));
                                                if (temp.equals(item)) {
                                                    dsp.child("consumed").getRef().setValue(n1.getValue());

                                                }
                                            }
                                            ArrayAdapter<String> dataAdapter_rum;
                                            dataAdapter_rum = new ArrayAdapter(actvity_alcohol.this, android.R.layout.simple_spinner_item, rums);
                                            dataAdapter_rum.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spinner_rum.setAdapter(dataAdapter_rum);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                            spinner_rum.setAdapter(dataAdapter_rum);
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
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(actvity_alcohol.this);
                View vv = layoutInflater.inflate(R.layout.activity_delete_prompt, null);
                AlertDialog.Builder alertdialog1 = new AlertDialog.Builder(actvity_alcohol.this);
                alertdialog1.setView(vv);

                n1 = vv.findViewById(R.id.Number_picker_delete);

                final String[] arr = new String[customized.size() - 1];

                for (int i = 0; i < arr.length; i++) {
                    arr[i] = customized.get(i + 1);
                }

                if (arr.length == 0) {
                    Toast.makeText(getApplicationContext(), "Nothing to delete. Add drinks to delete.", Toast.LENGTH_SHORT).show();
                } else {
                    n1.setMaxValue(0);
                    n1.setMaxValue(arr.length - 1);
                    n1.setDisplayedValues(arr);

                    alertdialog1.setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    final int percentage = n1.getValue();

                                    firebaseReference.child("users").child(user).child("alcohol").child("customized").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for (DataSnapshot dsp : dataSnapshot.getChildren())
                                                if ((String.valueOf(dsp.child("name").getValue()) + "   " + String.valueOf(dsp.child("percent").getValue())).equals(arr[percentage])) {
                                                    dsp.getRef().removeValue();
                                                }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            databaseError.toException();
                                        }
                                    });

                                    customized.clear();
                                    customized.add("Your Drinks:");

                                    Toast.makeText(getApplicationContext(), arr[percentage] + " deleted", Toast.LENGTH_SHORT).show();
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
