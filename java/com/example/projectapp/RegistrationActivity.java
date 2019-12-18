package com.example.projectapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RegistrationActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    RegistrationValidator validator;
    boolean formIsValid;
    public DatabaseReference firebaseReference;
    public FirebaseDatabase firebaseDBInstance;
    Button btnreg;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton googlesignin;
    CallbackManager callbackManager;
    LoginButton fb_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();

        validator = new RegistrationValidator();
        formIsValid = true;
        btnreg=findViewById(R.id.button_reg);
        callbackManager = CallbackManager.Factory.create();
        fb_button = findViewById(R.id.fb_login_button);
        fb_button.setReadPermissions("email", "public_profile");


        //for facebook login
        fb_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegistrationActivity.this, "process cancelled",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(RegistrationActivity.this, "Process error",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /*for google sign in*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(RegistrationActivity.this, gso);

        googlesignin=(SignInButton)findViewById(R.id.go_sign_in_button);

        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signin, 101);
                mGoogleSignInClient.signOut();

            }
        });
        /*for google sign in*/

        /*email registration*/
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateForm(v);
                createNewUser(getEmailField(), getPasswordField());
            }
        });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            auth.signInWithCredential(credential);
                            Intent i = new Intent(getApplicationContext(), Three_button.class);

                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                            Toasty.success(getApplicationContext(),"You have successfully logged in", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                e.printStackTrace();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(getApplicationContext(), Three_button.class);

                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();

                            Toasty.success(getApplicationContext(),"You have successfully logged in", Toast.LENGTH_SHORT).show();

                        } else {
                            Toasty.error(getApplicationContext(),"Login in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /*This method registers a new user in the firebase database*/
    protected void createNewUser(final String email, String password) {

        if (!formIsValid) {
            return;
        }
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //if user email is unused an pass is strong, a account will be created
                if(task.isSuccessful()){
                    FirebaseUser user = auth.getCurrentUser();
                    firebaseDBInstance = FirebaseDatabase.getInstance();
                    firebaseReference =  firebaseDBInstance.getReference();

                    String username = getUsernameField();
                    if(username == null){
                        username = "";
                    }

                    addDefaultAlcohol();

                    String status = "Logged Off";
                    String email = getEmailField();
                    String profileId = firebaseReference.child("users").child(user.getUid()).getKey();

                    UserProfile newUser = new UserProfile(profileId, username, email, status);

                    firebaseReference.child("users").child(user.getUid()).child("userprofile").setValue(newUser);

                    //After an account is created, a validation email will be sent to the user
                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toasty.success(RegistrationActivity.this,"You are now registered. Please check your email for verification and login.",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toasty.error(RegistrationActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    Toasty.error(RegistrationActivity.this, "Email is already in use", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void validateForm(View view) {

        formIsValid = true;

        if (!validator.emailIsValid(this.getEmailField())) {
            EditText email = (EditText) findViewById(R.id.email_reg);
            if(this.getEmailField().equals("")){
                email.setError("Please enter your email");
            }
            else {
                email.setError("Please enter valid email");
            }
            email.setText("");
            this.formIsValid = false;
        }
        else if (!validator.passwordIsStrong(this.getPasswordField())) {
            TextView password = findViewById(R.id.password);
            if(this.getPasswordField().equals("")){
                password.setError("Please enter your password");
            }
            else {
                Toasty.warning(this,"Your password should contain:\n1.at least 8 characters\n2.a special character\n3.at least one digit\n4.upper and lowercase letters",Toast.LENGTH_LONG).show();
            }
            password.setText("");
            this.formIsValid = false;
        }
        else if (!validator.passwordsMatch(this.getPasswordField(), this.getConfirmPasswordField())) {
            TextView confirmPass = findViewById(R.id.confirmPassword);
            if(this.getConfirmPasswordField().equals("")){
                confirmPass.setError("Please re-enter your password");
            }
            else {
                confirmPass.setError("Passwords do not match");
            }
            confirmPass.setText("");
            this.formIsValid = false;
        }
    }

    public String getUsernameField(){
        EditText username =(EditText) findViewById(R.id.register_username);
        return username.getText().toString();
    }
    public String getEmailField(){
        EditText email = (EditText) findViewById(R.id.email_reg);
        return email.getText().toString();
    }
    public String getPasswordField(){
        EditText password = (EditText) findViewById(R.id.password);
        return password.getText().toString();
    }
    public String getConfirmPasswordField(){
        EditText confirmPass = (EditText) findViewById(R.id.confirmPassword);
        return confirmPass.getText().toString();
    }
    public void addDefaultAlcohol(){

        FirebaseUser user = auth.getCurrentUser();
        firebaseDBInstance = FirebaseDatabase.getInstance();
        firebaseReference =  firebaseDBInstance.getReference();

        ArrayList<SubstanceInfo> drinks=new ArrayList<>();
        drinks.add(new SubstanceInfo("Alpine","5%", 0));
        drinks.add(new SubstanceInfo("Becks", "5%",0));
        drinks.add(new SubstanceInfo("Blue Moon", "5.4%",0));
        drinks.add(new SubstanceInfo("Bud Light", "4%",0));
        drinks.add(new SubstanceInfo("Budweiser", "5%", 0));
        drinks.add(new SubstanceInfo("Corona", "4.5%", 0));
        drinks.add(new SubstanceInfo("Keith's", "5%", 0));
        drinks.add(new SubstanceInfo("MooseHead", "5%",0));
        drinks.add(new SubstanceInfo("MooseHead Radler","4%",0));
        drinks.add(new SubstanceInfo("Sapporo", "4.9%", 0));


        for(int i = 0; i < drinks.size(); i++)
        firebaseReference.child("users").child(user.getUid()).child("alcohol").child("beer").child(drinks.get(i).getName()).setValue(drinks.get(i));

        drinks.clear();

        drinks.add(new SubstanceInfo("Elephant Island", "14.9%",0));
        drinks.add(new SubstanceInfo("Blue Mountain", "13.5%",0));
        drinks.add(new SubstanceInfo("Reif Estate", "12.6%",0));
        drinks.add(new SubstanceInfo("Rosehall Run", "12.5%",0));
        drinks.add(new SubstanceInfo("Mission Hill", "13.5%",0));


        for(int i = 0; i < drinks.size(); i++)
            firebaseReference.child("users").child(user.getUid()).child("alcohol").child("wines").child(drinks.get(i).getName()).setValue(drinks.get(i));

        drinks.clear();

        drinks.add(new SubstanceInfo("JACK DANIEL", "40%",0));
        drinks.add(new SubstanceInfo("CROWN ROYAL", "40%",0));
        drinks.add(new SubstanceInfo("FIREBALL", "33%",0));
        drinks.add(new SubstanceInfo("JIM BEAM", "35%",0));
        drinks.add(new SubstanceInfo("JAMESON IRISH", "40%",0));
        drinks.add(new SubstanceInfo("JOHNNIE WALKER Blue Label", "40%",0));

        for(int i = 0; i < drinks.size(); i++)
            firebaseReference.child("users").child(user.getUid()).child("alcohol").child("whiskeys").child(drinks.get(i).getName()).setValue(drinks.get(i));

        drinks.clear();

        drinks.add(new SubstanceInfo("Diplomatico", "40%",0));
        drinks.add(new SubstanceInfo("Havana Club", "45%",0));
        drinks.add(new SubstanceInfo("Chairman's Reserve", "40%",0));
        drinks.add(new SubstanceInfo("The Real McCoy", "40%",0));
        drinks.add(new SubstanceInfo("Don Papa Rum", "40%",0));

        for(int i = 0; i < drinks.size(); i++)
            firebaseReference.child("users").child(user.getUid()).child("alcohol").child("rums").child(drinks.get(i).getName()).setValue(drinks.get(i));
    }
}
