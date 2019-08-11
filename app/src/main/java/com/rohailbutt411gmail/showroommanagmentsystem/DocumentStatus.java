package com.rohailbutt411gmail.showroommanagmentsystem;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DocumentStatus extends AppCompatActivity {

    private Spinner spinner;
    private ListView listView;
    private String statusSelect="";
    private DatabaseReference databaseReference;
    private DatabaseReference databasePurchase;
    private ImageButton imageButton;
    private ArrayList<String> list;
    private View parentLayout;
    private String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_status);
        getSupportActionBar().setTitle("Doc Status");
        currentUser = getIntent().getStringExtra("name");
        spinner = (Spinner) findViewById(R.id.spinner_doc_status);
        listView = (ListView) findViewById(R.id.doc_status_listview);
        parentLayout = findViewById(R.id.document_activity_layout);
        list = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databasePurchase = FirebaseDatabase.getInstance().getReference();
        final String[] items = {"select","pending","excise"};
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,items);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        break;
                    case 1:
                        statusSelect = "pending";
                        fetchData();
                        break;
                    case 2:
                        statusSelect = "excise";
                        fetchData();
                        break;
                    default:
                         statusSelect = "pending";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        fetchData();

    }


    public void sreachBtn(View view) {
        if(list.size()==0){
            snackBarMessage("No Data Found");
        }
        else {
            final ArrayAdapter arrayAdapter1 = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            listView.setAdapter(arrayAdapter1);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(DocumentStatus.this);
                    builder.setTitle(list.get(position));
                    builder.setMessage("If You Click Ok the Document Status Should be Updated To (Completed)");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            Map values = new HashMap();
                            values.put("document_status","completed");
                            values.put("updated_user",currentUser);
                            databasePurchase.child("Stock").child(list.get(position)).updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    dialog.dismiss();
                                    if (task.isSuccessful()) {
                                        list.remove(position);
                                        arrayAdapter1.notifyDataSetChanged();
                                        snackBarMessage("Document Status Updated");
                                    } else {
                                        snackBarMessage("Error");
                                    }
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }
    }
    public void fetchData(){
        if(!statusSelect.equals("")){

            databaseReference.child("Stock").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Product p = dataSnapshot.getValue(Product.class);
                    if(statusSelect.equals(p.getDocument_status())){
                        list.add(p.getChasis_no());
                        Log.e("Doc",p.getDocument_status());
                    }
                    Log.e("Error",p.getDocument_status());
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }
    public void snackBarMessage(String msg){
        Snackbar snackbar = Snackbar.make(parentLayout,msg,Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
