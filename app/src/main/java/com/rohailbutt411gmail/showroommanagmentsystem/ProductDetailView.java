package com.rohailbutt411gmail.showroommanagmentsystem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailView extends AppCompatActivity {

    private String key,title;
    private DatabaseReference databaseProduct,databasesell,databasedelete;
    private ProgressBar progressBar;
    private Button button;
    private TextView reg_text,chasis_text,feature_text,price_text;
    private EditText buyer_edit,detail_edt,amount_edt,date_edt,remaining_amount_edt;
    private Boolean aBoolean;
    private ImageView imageView;
    private DatePickerDialog datePickerDialog;
    private View parentLayout;
    int year;
    int month;
    int dayOfMonth;
    private Calendar calendar;
    private String current_user;
    private static String mode = "sell";
    String reg_no_firebase ;
    String chasis_no_firebase ;
    String brand_firebase ;
    String engine_power_firebase ;
    String image_firebase;
    String model_firebase ;
    String color_firebase;
    String buy_price_firebase;
    String purchase_user;
    String owner_name;
    String updated_user;
    Date dt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail_view);
        Bundle bundle = getIntent().getExtras();
        if(bundle.getString("caller_activity_name").equals(Sell.class.toString())){
            key = bundle.getString("key");
            title = bundle.getString("title");
            current_user = bundle.getString("user");
            aBoolean = bundle.getBoolean("access",false);
            getSupportActionBar().setTitle(title.substring(0,1).toUpperCase()+""+title.substring(1));
            mode = "sell";
        }
        else if(bundle.getString("caller_activity_name").equals(Report.class.toString())){
            mode = "edit";
            getSupportActionBar().setTitle("Edit");
            key = bundle.getString("key");
        }

        reg_text = (TextView) findViewById(R.id.reg_no_detail);
        chasis_text = (TextView) findViewById(R.id.chasis_no_detail);
        feature_text = (TextView) findViewById(R.id.feature_detail);
        price_text = (TextView) findViewById(R.id.price_detail);
        imageView = (ImageView) findViewById(R.id.product_image_detail);
        buyer_edit = (EditText) findViewById(R.id.buyer_customer_name);
        detail_edt = (EditText) findViewById(R.id.buyer_customer_detail);
        amount_edt = (EditText) findViewById(R.id.bike_selling_amount);
        remaining_amount_edt = (EditText) findViewById(R.id.bike_selling_remaining_amount);
        date_edt = (EditText) findViewById(R.id.date_detail);
        progressBar = (ProgressBar) findViewById(R.id.sell_progress_bar);
        parentLayout = findViewById(R.id.sell_parent);
        button = (Button) findViewById(R.id.sell_button);
        progressBar.setVisibility(View.INVISIBLE);
        databaseProduct = FirebaseDatabase.getInstance().getReference().child("Stock");
        databasedelete = FirebaseDatabase.getInstance().getReference().child("Stock");
        databasesell = FirebaseDatabase.getInstance().getReference().child("Sold");
        if(mode.equals("sell")){
            fetchProductForSell();
        }
        else if(mode.equals("edit")){
            fetchProductForEdit();
        }

    }
    public void fetchProductForEdit(){
        databasesell.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    reg_no_firebase = dataSnapshot.child("reg_no").getValue().toString();
                    chasis_no_firebase = dataSnapshot.child("chasis_no").getValue().toString();
                    brand_firebase = dataSnapshot.child("brand").getValue().toString();
                    engine_power_firebase = dataSnapshot.child("engine_power").getValue().toString();
                    image_firebase = dataSnapshot.child("image").getValue().toString();
                    model_firebase = dataSnapshot.child("model").getValue().toString();
                    color_firebase = dataSnapshot.child("color").getValue().toString();
                    buy_price_firebase = dataSnapshot.child("buy_amount").getValue().toString();
                    owner_name = dataSnapshot.child("seller_name").getValue().toString();
                    purchase_user = dataSnapshot.child("user").getValue().toString();
                    updated_user = dataSnapshot.child("updated_by").getValue().toString();
                    Picasso.get().load(image_firebase).placeholder(R.drawable.motoicon).into(imageView);
                    reg_text.setText(reg_no_firebase);
                    chasis_text.setText(chasis_no_firebase);
                    price_text.setText(buy_price_firebase);
                    feature_text.setText(brand_firebase+"/"+engine_power_firebase+"/"+model_firebase+"/"+color_firebase);
                    buyer_edit.setText(dataSnapshot.child("buyer_name").getValue().toString());
                    detail_edt.setText(dataSnapshot.child("buyer_detail").getValue().toString());
                    int sell = Integer.parseInt(dataSnapshot.child("sell_amount").getValue().toString());
                    int remaining = Integer.parseInt(dataSnapshot.child("remaining_amount").getValue().toString());
                    amount_edt.setText(String.valueOf(sell-remaining));
                    remaining_amount_edt.setText(dataSnapshot.child("remaining_amount").getValue().toString());
                    Object date = dataSnapshot.child("date").getValue();
                    date_edt.setText(date.toString());
                    button.setText("Update");
                    date_edt.setVisibility(View.INVISIBLE);
                    date_edt.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void fetchProductForSell(){
        databaseProduct.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){
                    reg_no_firebase = dataSnapshot.child("reg_no").getValue().toString();
                    chasis_no_firebase = dataSnapshot.child("chasis_no").getValue().toString();
                    brand_firebase = dataSnapshot.child("brand").getValue().toString();
                    engine_power_firebase = dataSnapshot.child("engine_power").getValue().toString();
                    image_firebase = dataSnapshot.child("image").getValue().toString();
                    model_firebase = dataSnapshot.child("model").getValue().toString();
                    color_firebase = dataSnapshot.child("color").getValue().toString();
                    buy_price_firebase = dataSnapshot.child("buy_price").getValue().toString();
                    owner_name = dataSnapshot.child("seller_name").getValue().toString();
                    purchase_user = dataSnapshot.child("user").getValue().toString();
                    updated_user = dataSnapshot.child("updated_user").getValue().toString();
                    Picasso.get().load(image_firebase).placeholder(R.drawable.motoicon).into(imageView);
                    reg_text.setText(reg_no_firebase);
                    chasis_text.setText(chasis_no_firebase);
                    price_text.setText(buy_price_firebase);
                    feature_text.setText(brand_firebase+"/"+engine_power_firebase+"/"+model_firebase+"/"+color_firebase);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void date_picker_detail(View view) {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(ProductDetailView.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date_edt.setText(dayOfMonth+"/"+String.valueOf(month+1)+"/"+year);
            }
        },year,month,dayOfMonth);
        datePickerDialog.show();
    }

    public void submit(View view) {
        String buyer_name = buyer_edit.getText().toString().trim();
        String buyer_detail = detail_edt.getText().toString().trim();
        String sell_amount = amount_edt.getText().toString().trim();
        String sell_date = date_edt.getText().toString().trim();
        String remaining_amount = remaining_amount_edt.getText().toString().trim();
        if(TextUtils.isEmpty(buyer_name)){
            buyer_edit.setError("Please Fill Field");
            return;
        }
        else if(TextUtils.isEmpty(buyer_detail)){
            detail_edt.setError("Please Fill Field");
            return;
        }
        else if(TextUtils.isEmpty(sell_amount)){
            amount_edt.setError("Please Fill Field");
            return;
        }
        else if(TextUtils.isEmpty(sell_date)){
            date_edt.setError("Please Fill Field");
            return;
        }
        else if(mode.equals("sell") && (!current_user.toLowerCase().equals(purchase_user.toLowerCase()) && aBoolean==false) ){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You Are not able to sell those Products that are Purchased by other user");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.show();
            return;
        }
        else if(button.getText().toString().toLowerCase().equals("sell")){
            if(TextUtils.isEmpty(remaining_amount)){
                remaining_amount  = "0";
            }else{
                sell_amount = String.valueOf(Integer.parseInt(sell_amount)+Integer.parseInt(remaining_amount));
            }
            String[] dd = date_edt.getText().toString().split("/");
            try{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                dt = sdf.parse(String.valueOf(Integer.parseInt(dd[2])+1900)+"/"+String.valueOf((Integer.parseInt(dd[1])+1))+"/"+dd[0]);

            }catch(Exception e){
                Log.e("Date Error",e.getMessage());
            }
            Integer profit = Integer.parseInt(sell_amount)-Integer.parseInt(buy_price_firebase);
            Map values = new HashMap<>();
            values.put("brand",brand_firebase);
            values.put("engine_power",engine_power_firebase);
            values.put("color",color_firebase);
            values.put("model",model_firebase);
            values.put("reg_no",reg_no_firebase);
            values.put("chasis_no",chasis_no_firebase);
            values.put("image",image_firebase);
            values.put("seller_name",owner_name);
            values.put("buyer_name",buyer_name);
            values.put("buyer_detail",buyer_detail);
            values.put("buy_amount",Integer.parseInt(buy_price_firebase));
            values.put("sell_amount",Integer.parseInt(sell_amount));
            values.put("remaining_amount",Integer.parseInt(remaining_amount));
            values.put("date",dt);
            values.put("profit",profit);
            values.put("purchase_user",purchase_user);
            values.put("user",current_user.toLowerCase());
            values.put("updated_by",updated_user);

            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            databasesell.child(chasis_no_firebase).setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        databasedelete.child(chasis_no_firebase).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                button.setVisibility(View.VISIBLE);
                                button.setEnabled(true);
                                progressBar.setVisibility(View.INVISIBLE);

                                if(task.isSuccessful()){
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailView.this);
                                    builder.setMessage("Item Sold Successfully");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    builder.show();
                                }
                                else{
                                    snackBarMessage("Error");
                                }
                            }
                        });
                    }
                    else{
                        snackBarMessage("Error");
                    }
                }
            });
        }
        else if(button.getText().toString().toLowerCase().equals("update")){
            if(TextUtils.isEmpty(remaining_amount)){
                remaining_amount  = "0";
            }else{
                sell_amount = String.valueOf(Integer.parseInt(sell_amount)+Integer.parseInt(remaining_amount));
            }
            Integer profit = Integer.parseInt(sell_amount)-Integer.parseInt(buy_price_firebase);
            Map values = new HashMap();
            values.put("buyer_name",buyer_name);
            values.put("buyer_detail",buyer_detail);
            values.put("sell_amount",Integer.parseInt(sell_amount));
            values.put("remaining_amount",Integer.parseInt(remaining_amount));
            values.put("profit",profit);
            button.setVisibility(View.INVISIBLE);
            button.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            databasesell.child(chasis_no_firebase).updateChildren(values).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    button.setVisibility(View.VISIBLE);
                    button.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailView.this);
                    builder.setMessage("Item Updated");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    button.setVisibility(View.VISIBLE);
                    button.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    snackBarMessage("Item Not Updated");
                }
            });

        }
    }
    public void snackBarMessage(String msg){
        Snackbar snackbar = Snackbar.make(parentLayout,msg,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}