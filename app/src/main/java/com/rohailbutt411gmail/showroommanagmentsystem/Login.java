package com.rohailbutt411gmail.showroommanagmentsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText email,password;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login");
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressDialog = new ProgressDialog(Login.this);
        sharedPreferences = getSharedPreferences("loginPref",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        checksession();
    }

    private void checksession() {
       int c = sharedPreferences.getInt("login",0);
       if(c==1){
           startActivity(new Intent(Login.this,MainActivity.class));
       }
    }

    public void login(View v){
        String e = email.getText().toString().trim();
        String p = password.getText().toString().trim();
        if(TextUtils.isEmpty(e)){
            email.setError("Please Enter Email");
            return;
        }
        else if(TextUtils.isEmpty(p)){
            password.setError("Please Enter Password");
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
            email.setError("Invalid Email Address");
            return;
        }
        else if(p.length()<6){
            email.setError("Minimum 6 characters required");
            return;
        }
        else{
            if(isOnline()){
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent main = new Intent(Login.this,MainActivity.class);
                            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            sharedPreferences.edit().putInt("login",0).commit();
                            startActivity(main);
                            finish();
                        }
                        else{
                            progressDialog.hide();
                            Toast.makeText(Login.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(Login.this,"No Internt Access ",Toast.LENGTH_LONG).show();
            }
        }

    }

    public  boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }


    public void reset_password_btn(View view) {
        startActivity(new Intent(Login.this,ResetPassword.class));
    }
}
