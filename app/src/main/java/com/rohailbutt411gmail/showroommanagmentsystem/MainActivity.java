package com.rohailbutt411gmail.showroommanagmentsystem;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    String current_user_name="",role="";
    boolean access;
    View parentLayout;
    private ImageButton user_btn,report_btn,purchaseBtn,sellBtn,expenseBtn,docStatusBtn;
    private TextView addUserTxt,reportTxt,purchaseTxt,sellTxt,expenseTxt,docStatusTxt;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLayout = findViewById(R.id.main_layout);
        purchaseBtn = (ImageButton) findViewById(R.id.main_purchase_btn);
        sellBtn = (ImageButton) findViewById(R.id.main_sell_btn);
        expenseBtn = (ImageButton) findViewById(R.id.main_expense_btn);
        docStatusBtn = (ImageButton) findViewById(R.id.doc_update);
        purchaseTxt = (TextView) findViewById(R.id.main_purchase_txt);
        sellTxt = (TextView) findViewById(R.id.main_sell_txt);
        expenseTxt = (TextView) findViewById(R.id.main_expense_txt);
        docStatusTxt = (TextView) findViewById(R.id.main_docStatus_txt);
        progressBar = (ProgressBar) findViewById(R.id.main_layout_progressbar);
        progressBar.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        user_btn = (ImageButton) findViewById(R.id.add_user_main);
        report_btn = (ImageButton) findViewById(R.id.report_main);
        addUserTxt = (TextView) findViewById(R.id.add_user_txt);
        reportTxt = (TextView) findViewById(R.id.report_txt);
        purchaseBtn.setVisibility(View.INVISIBLE);
        purchaseBtn.setEnabled(false);
        purchaseTxt.setVisibility(View.INVISIBLE);
        sellBtn.setVisibility(View.INVISIBLE);
        sellBtn.setEnabled(false);
        sellTxt.setVisibility(View.INVISIBLE);
        expenseBtn.setVisibility(View.INVISIBLE);
        expenseBtn.setEnabled(false);
        expenseTxt.setVisibility(View.INVISIBLE);
        docStatusBtn.setVisibility(View.INVISIBLE);
        docStatusBtn.setEnabled(false);
        docStatusTxt.setVisibility(View.INVISIBLE);
        user_btn.setVisibility(View.INVISIBLE);
        report_btn.setVisibility(View.INVISIBLE);
        addUserTxt.setVisibility(View.INVISIBLE);
        reportTxt.setVisibility(View.INVISIBLE);
        user_btn.setEnabled(false);
        report_btn.setEnabled(false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        getSupportActionBar().setTitle("Welcome");
        if(!isOnline()){
            snackBarMessage();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser1 = mAuth.getCurrentUser();
        if(!isOnline()){
            snackBarMessage();
        }
        if(firebaseUser1 == null){
            sendToStart();
        }else{
            String current_user = firebaseUser1.getUid();
            databaseReference.child("Users").child(current_user).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    current_user_name = dataSnapshot.child("name").getValue().toString();
                    role = dataSnapshot.child("role").getValue().toString();
                    access =Boolean.valueOf(dataSnapshot.child("access").getValue().toString());
                    getSupportActionBar().setTitle("Welcome "+current_user_name.substring(0,1).toUpperCase()+current_user_name.substring(1));
                    purchaseBtn.setVisibility(View.VISIBLE);
                    purchaseBtn.setEnabled(true);
                    purchaseTxt.setVisibility(View.VISIBLE);
                    sellBtn.setVisibility(View.VISIBLE);
                    sellBtn.setEnabled(true);
                    sellTxt.setVisibility(View.VISIBLE);
                    expenseBtn.setVisibility(View.VISIBLE);
                    expenseBtn.setEnabled(true);
                    expenseTxt.setVisibility(View.VISIBLE);
                    docStatusBtn.setVisibility(View.VISIBLE);
                    docStatusBtn.setEnabled(true);
                    docStatusTxt.setVisibility(View.VISIBLE);
                    if(  role.equals("admin")){
                        user_btn.setVisibility(View.VISIBLE);
                        report_btn.setVisibility(View.VISIBLE);
                        addUserTxt.setVisibility(View.VISIBLE);
                        reportTxt.setVisibility(View.VISIBLE);
                        user_btn.setEnabled(true);
                        report_btn.setEnabled(true);
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


    }

    private void sendToStart() {
        startActivity(new Intent(MainActivity.this,Login.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);
         switch (item.getItemId()){
             case R.id.about:
                 Intent about = new Intent(MainActivity.this,About.class);
                 startActivity(about);
                 break;
             case R.id.logout:
                 FirebaseAuth.getInstance().signOut();
                 sendToStart();
                 break;
         }
         return true;
    }

    public void btn_expenses(View view) {
        if(isOnline()){
            Intent expense = new Intent(MainActivity.this,Expenses.class);
            expense.putExtra("name",current_user_name);
            startActivity(expense);
        }
        else{
            snackBarMessage();
        }
    }

    public void btn_purchase(View view) {
        if(isOnline()){
            Intent purchase = new Intent(MainActivity.this,Purchase.class);
            purchase.putExtra("name",current_user_name);
            startActivity(purchase);
        }
        else{
            snackBarMessage();
        }
    }
    public void btn_sell(View view) {
        if(isOnline()){
            Intent sell = new Intent(MainActivity.this,Sell.class);
            sell.putExtra("name",current_user_name);
            sell.putExtra("access",access);
            startActivity(sell);
        }
        else{
            snackBarMessage();
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

    public void snackBarMessage(){
        Snackbar snackbar = Snackbar.make(parentLayout,"No Internet Connection",Snackbar.LENGTH_LONG);
        snackbar.show();
    }


    public void report_btn(View view) {
        startActivity(new Intent(MainActivity.this,Report.class));
    }

    public void add_user_btn(View view) {
        Intent a = new Intent(MainActivity.this,SignUp.class);
        a.putExtra("name",current_user_name);
        a.putExtra("role",role);
        startActivity(a);
    }

    public void doc_btn(View view) {
        if(isOnline()){
            Intent a = new Intent(MainActivity.this,DocumentStatus.class);
            a.putExtra("name",current_user_name);
            startActivity(a);
        }
    }
}

