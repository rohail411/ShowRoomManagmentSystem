package com.rohailbutt411gmail.showroommanagmentsystem;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class Purchase extends AppCompatActivity {

    private Spinner spinner_brand,spinner_color,spinner_document_status,spinner_engine_power,spinner_model_year;
    private EditText edt_bike_no,edt_bike_model,edt_engine_no,edt_owner_name,edt_owner_detail,edt_bike_price,dateField;
    private String brand_val,color_val,document_val,current_user_name,model_val,engine_power_val;
    private View parentLayout;
    private DatabaseReference databaseStock;
    private Button purchase_btn;
    private ProgressBar progressBar;
    final Integer REQUEST_CAMERA=1;
    final Integer SELECT_FILE = 0;
    private DatePickerDialog datePickerDialog;
    int year;
    int month;
    int dayOfMonth;
    private Calendar calendar;
    private Button takePhoto;
    private CircleImageView circleImageView;
    private  Uri imageUri;
    private StorageReference firebaseStorage;
    private static final int PERMISSIONS_REQUEST_CAPTURE_IMAGE = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        getSupportActionBar().setTitle("Purchase");
        current_user_name = getIntent().getStringExtra("name");
        spinner_brand = (Spinner) findViewById(R.id.brand_spinner);
        spinner_color = (Spinner) findViewById(R.id.color_spinner);
        spinner_document_status = (Spinner) findViewById(R.id.document_spinner);
        spinner_engine_power = (Spinner) findViewById(R.id.engine_power_spinner);
        spinner_model_year = (Spinner) findViewById(R.id.model_year_spinner);
        dateField = (EditText) findViewById(R.id.date_Field);
        edt_bike_no = (EditText) findViewById(R.id.bike_no);
        edt_engine_no = (EditText) findViewById(R.id.engine_no);
        edt_owner_name = (EditText) findViewById(R.id.bike_owner_name);
        edt_owner_detail = (EditText) findViewById(R.id.bike_owner_detail);
        edt_bike_price = (EditText) findViewById(R.id.bike_price);
        takePhoto = (Button) findViewById(R.id.take_photo);
        circleImageView = (CircleImageView) findViewById(R.id.product_image);
        parentLayout = findViewById(R.id.purchase_layout);
        purchase_btn = (Button) findViewById(R.id.purchase_btn);
        progressBar = (ProgressBar) findViewById(R.id.purchase_progress);
        progressBar.setVisibility(View.INVISIBLE);
        databaseStock = FirebaseDatabase.getInstance().getReference().child("Stock");
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        final String[] brandArray = {"Honda","Metro","Yamaha","Toyo","Suzuki","United","King Hero","Read Prince","Other"};
        final ArrayAdapter<String> brandAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,brandArray);
        spinner_brand.setAdapter(brandAdapter);

        String[] colorArray = {"Red","Black","Special Edition"};
        ArrayAdapter<String> colorAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,colorArray);
        spinner_color.setAdapter(colorAdapter);

        String[] docArray = {"Success","Pending"};
        ArrayAdapter<String> docAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,docArray);
        spinner_document_status.setAdapter(docAdapter);

        String[] engineArray = {"70","100","125","150"};
        ArrayAdapter<String> engineAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,engineArray);
        spinner_engine_power.setAdapter(engineAdapter);

        final String[] modelYear = {"1991","1992","1993","1994","1995","1996","1997","1998","1999","2000","2001","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024","2025"
        ,"2026","2027","2028","2029","2030"};
        ArrayAdapter<String> modelAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,modelYear);
        spinner_model_year.setAdapter(modelAdapter);

        spinner_engine_power.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        engine_power_val = "70";
                        break;
                    case 1:
                        engine_power_val = "100";
                        break;
                    case 2:
                        engine_power_val = "125";
                        break;
                    case 3:
                        engine_power_val = "150";
                        break;
                     default:
                         engine_power_val = "70";

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_model_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model_val = modelYear[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_brand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        brand_val = "honda";
                        break;
                    case 1:
                        brand_val = "metro";
                        break;
                    case 2:
                        brand_val = "yamaha";
                        break;
                    case 3:
                        brand_val = "toyo";
                        break;
                    case 4:
                        brand_val = "suzuki";
                        break;
                    case 5:
                        brand_val = "united";
                        break;
                    case 6:
                        brand_val = "king hero";
                        break;
                    case 7:
                        brand_val = "road prince";
                        break;
                    case 8:
                        brand_val = "other";
                    default:
                        brand_val = "honda";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_color.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        color_val = "red";
                        break;
                    case 1:
                        color_val = "black";
                        break;
                    case 2:
                        color_val = "special_edition";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_document_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        document_val = "success";
                        break;
                    case 1:
                        document_val = "pending";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Camera","Gallery","Cancel"};
                final AlertDialog.Builder builder = new AlertDialog.Builder(Purchase.this);
                builder.setTitle("Add Image");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(items[which].equals("Camera")){
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent,REQUEST_CAMERA);
                        }
                        else if(items[which].equals("Gallery")){
                            Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Select FIle"),SELECT_FILE);
                        }
                        else if(items[which].equals("Cancel")){
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        });


        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // User may have declined earlier, ask Android if we should show him a reason

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // show an explanation to the user
                // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
            } else {
                // request the permission.
                // CALLBACK_NUMBER is a integer constants
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAPTURE_IMAGE);
                // The callback method gets the result of the request.
            }
        } else {

        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // User may have declined earlier, ask Android if we should show him a reason

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // show an explanation to the user
                // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
            } else {
                // request the permission.
                // CALLBACK_NUMBER is a integer constants
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
                // The callback method gets the result of the request.
            }
        } else {

        }


    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, String[] permissions,
                                             int[] grantResults){
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAPTURE_IMAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    Log.d("", "permission granted success");

                } else {
                    // permission denied

                    Log.d("", "permission denied");
                }
                return;
            }
            case PERMISSION_REQUEST_STORAGE:{
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted

                    Log.d("", "permission granted success");

                } else {
                    // permission denied

                    Log.d("", "permission denied");
                }
                return;
            }






        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            if(requestCode==REQUEST_CAMERA){
                Bundle  bundle = data.getExtras();
                final Bitmap bitmap = (Bitmap) bundle.get("data");
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,new ByteArrayOutputStream());
                String path = MediaStore.Images.Media.insertImage(this.getContentResolver(),bitmap,"Title",null);
                Uri img_uri = Uri.parse(path);
                circleImageView.setImageURI(img_uri);
                imageUri = img_uri;

            }
            else if(requestCode==SELECT_FILE) {
                Uri uri = data.getData();
                circleImageView.setImageURI(uri);
                imageUri = uri;

            }
        }
    }

    public void purchase_btn(View view) {
        if(isOnline()){
            final String b_no = edt_bike_no.getText().toString().trim();
            final String e_no = edt_engine_no.getText().toString().trim();
            String m_no = model_val;
            String o_name = edt_owner_name.getText().toString().trim();
            String o_detail = edt_owner_detail.getText().toString().trim();
            String b_price = edt_bike_price.getText().toString().trim();
            String date = dateField.getText().toString();
            if(TextUtils.isEmpty(b_no)){
                edt_bike_no.setError("Please Fill Field");
                return;
            }
            else if(TextUtils.isEmpty(e_no)){
                edt_engine_no.setError("Please Fill Field");
                return;
            }
            else if(TextUtils.isEmpty(b_no)){
                edt_bike_model.setError("Please Fill Field");
                return;
            }
            else if(TextUtils.isEmpty(o_name)){
                edt_owner_name.setError("Please Fill Field");
                return;
            }
            else if(TextUtils.isEmpty(o_detail)){
                edt_owner_detail.setError("Please Fill Field");
                return;
            }
            else if(TextUtils.isEmpty(b_price)){
                edt_bike_price.setError("Please Fill Field");
                return;
            }
            else{
                purchase_btn.setVisibility(View.INVISIBLE);
                purchase_btn.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                HashMap<String,String> values = new HashMap<>();
                values.put("user",current_user_name);
                values.put("seller_name",o_name);
                values.put("seller_detail",o_detail);
                values.put("reg_no",b_no);
                values.put("chasis_no",e_no);
                values.put("model",m_no);
                values.put("engine_power",engine_power_val);
                values.put("brand",brand_val);
                values.put("color",color_val);
                values.put("document_status",document_val);
                values.put("buy_price",b_price);
                values.put("image","defaut");
                values.put("date",date);

                databaseStock.child(e_no).setValue(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            String id = UUID.randomUUID().toString().replace("-","");
                            final StorageReference filePath = firebaseStorage.child("product_img").child(id+ ".jpg");
                            filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                final String url = uri.toString();
                                                databaseStock.child(e_no).child("image").setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        purchase_btn.setVisibility(View.VISIBLE);
                                                        purchase_btn.setEnabled(true);
                                                        progressBar.setVisibility(View.INVISIBLE);
                                                        if(task.isSuccessful()){
                                                            snackBarMessage("Item Added to Stock");
                                                            edt_bike_price.setText("");
                                                            edt_bike_no.setText("");
                                                            edt_bike_price.setText("");
                                                            edt_engine_no.setText("");
                                                            edt_owner_detail.setText("");
                                                            edt_owner_name.setText("");
                                                        }
                                                        else{

                                                        }
                                                    }
                                                });
                                            }
                                        });

                                    }
                                    else{
                                        snackBarMessage("Error");
                                    }
                                }
                            });


                        }
                        else{
                            snackBarMessage("Item Not Added");
                        }
                    }
                });
            }
        }

    }


    public void date_picker(View view) {

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(Purchase.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dateField.setText(dayOfMonth+"/"+String.valueOf(month+1)+"/"+year);
            }
        },year,month,dayOfMonth);
        datePickerDialog.show();
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

    public void snackBarMessage(String msg){
        Snackbar snackbar = Snackbar.make(parentLayout,msg,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
