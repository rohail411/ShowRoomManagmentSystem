package com.rohailbutt411gmail.showroommanagmentsystem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {

    EditText username,email,password,confirm_password;
    CheckBox checkBox;
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String user="";
    private String role="";
    private Spinner spinner;
    private String selected_role="employee";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("SignUp");
        user = getIntent().getStringExtra("name");
        role = getIntent().getStringExtra("role");
        username = (EditText) findViewById(R.id.s_username);
        email = (EditText) findViewById(R.id.s_email);
        password = (EditText) findViewById(R.id.s_password);
        confirm_password = (EditText) findViewById(R.id.s_confirm_password);
        progressDialog = new ProgressDialog(SignUp.this);
        mAuth = FirebaseAuth.getInstance();
        spinner = (Spinner) findViewById(R.   id.signup_spinner);
        checkBox = (CheckBox) findViewById(R.id.checkBox);
        spinner.setVisibility(View.INVISIBLE);
        final String[] user_role = {"Admin","Manager","Employee"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,user_role);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        selected_role = "admin";
                        break;
                    case 1:
                        selected_role = "manager";
                        break;
                    case 2:
                        selected_role = "employee";
                        break;
                     default:
                            selected_role = "employee";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if(role.equals("admin")){
            spinner.setVisibility(View.VISIBLE);
        }
    }



    public void sign_up(View view) {
       final String u = username.getText().toString().trim();
       final String e = email.getText().toString().trim();
       final String p = password.getText().toString().trim();
       final String cp = confirm_password.getText().toString().trim();
        if(TextUtils.isEmpty(u)){
            username.setError("Please Enter Username");
            return;
        }
        else if(TextUtils.isEmpty(e)){
            email.setError("Please Enter Email");
            return;
        }
        else if(TextUtils.isEmpty(p)){
            password.setError("Please Enter Password");
            return;
        }
        else if(TextUtils.isEmpty(cp)){
            confirm_password.setError("Please Enter Password");
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
            email.setError("Invalid Email");
            return;
        }
        else if(p.length()<6){
            password.setError("Minimum 6 characters required");
            return;
        }
        else if(!p.equals(cp)){
            confirm_password.setError("Password Must Match With Previous Entry");
            return;
        }
        else {
            if(isOnline()){
                if(user.equals("") && role.equals("")){
                    selected_role = "employee";
                }
                progressDialog.setMessage("Please Wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(e,p).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String u_id = firebaseUser.getUid();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(u_id);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            Map userMap = new HashMap<>();
                            userMap.put("name",u);
                            userMap.put("role",selected_role.toLowerCase());
                            userMap.put("image","default");
                            userMap.put("device_token",deviceToken);
                            userMap.put("access",String.valueOf(checkBox.isChecked()));
                            databaseReference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        finish();

                                    }

                                }
                            });

                        }
                        else{
                            progressDialog.hide();
                            Toast.makeText(SignUp.this,"Account Not Created",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else{
                Toast.makeText(SignUp.this,"No Internet Access",Toast.LENGTH_LONG).show();
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
}
