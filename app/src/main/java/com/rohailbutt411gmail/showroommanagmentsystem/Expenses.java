package com.rohailbutt411gmail.showroommanagmentsystem;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Expenses extends AppCompatActivity {

    private Spinner spinner;
    private EditText editText;
    private EditText date;
    private EditText exp_detail;
    String expense_type;
    private DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    private Calendar calendar;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    private Button button_expense;
    private View parentLayout;
    String name;
    Date dat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        getSupportActionBar().setTitle("Expenses");
        name = getIntent().getStringExtra("name");
        parentLayout = findViewById(R.id.content);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Expenses");
        editText = (EditText) findViewById(R.id.expense_amount);
        date = (EditText) findViewById(R.id.date);
        exp_detail = (EditText) findViewById(R.id.expense_detail);
        button_expense = (Button) findViewById(R.id.expense_btn);
        progressBar = (ProgressBar) findViewById(R.id.expense_progress);
        progressBar.setVisibility(View.INVISIBLE);
        spinner = (Spinner) findViewById(R.id.expense_spinner);
        String[] options = {"Select Expense Type","Utility Bill","Rent","Others"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,options);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        expense_type = "utility bill";
                        break;
                    case 2:
                        expense_type = "rent";
                        break;
                    case 3:
                        expense_type = "others";
                        break;
                    default:
                        expense_type = "others";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void btn_expenses_add(View view) {
        if(!isOnline()){
            Snackbar snackbar = Snackbar.make(parentLayout,"Check Internet Connection",Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else if(TextUtils.isEmpty(editText.getText().toString().trim())){
            editText.setError("Please Fill Field");
            return;
        }
        else if(TextUtils.isEmpty(exp_detail.toString().trim())){
            exp_detail.setError("Please Fill Field");
            return;
        }
        else if(TextUtils.isEmpty(date.getText().toString().trim())){
            date.setError("Please Fill Field");
            return;
        }
        else {
            button_expense.setVisibility(View.INVISIBLE);
            button_expense.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            String[] dd = date.getText().toString().split("/");
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                dat = sdf.parse(String.valueOf(Integer.parseInt(dd[2])+1900)+"/"+String.valueOf((Integer.parseInt(dd[1])+1))+"/"+dd[0]);

            }catch(Exception e){
                Log.e("Date Error",e.getMessage());
            }
            Map expense = new HashMap<>();
            expense.put("expense_type",expense_type);
            expense.put("amount",Integer.parseInt(editText.getText().toString()));
            expense.put("detail",exp_detail.getText().toString().trim());
            expense.put("user",name);
            expense.put("date",dat);
            databaseReference.push().setValue(expense).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    button_expense.setVisibility(View.VISIBLE);
                    button_expense.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    if(task.isSuccessful()){
                        exp_detail.setText("");
                        editText.setText("");
                        Snackbar snackbar = Snackbar.make(parentLayout,"Expense Added",Snackbar.LENGTH_LONG);
                                 snackbar.show();
                    }
                    else{
                        Snackbar snackbar = Snackbar.make(parentLayout,"Expense Not Added",Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
            });

        }

    }

    public void date_picker(View view) {

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(Expenses.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        },year,month,dayOfMonth);
        datePickerDialog.show();

    }
    public boolean isOnline(){
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
