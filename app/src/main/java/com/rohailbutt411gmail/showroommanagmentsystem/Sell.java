package com.rohailbutt411gmail.showroommanagmentsystem;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class Sell extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SearchView searchView;
    private DatabaseReference productDatabase;
    private String current_user;
    Boolean access;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell);
        current_user = getIntent().getStringExtra("name");
        access = getIntent().getBooleanExtra("access",false);
        getSupportActionBar().setTitle("Sell");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_sell);
        searchView = (SearchView) findViewById(R.id.sell_search_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productDatabase = FirebaseDatabase.getInstance().getReference().child("Stock");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String text  = query;
                products(true,text);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                products(false,"");
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        products(false,"");
    }
    public void products(boolean check,String text){
        Query query = productDatabase;
        if(check==true){
            query = query.orderByChild("chasis_no").equalTo(text);
            Log.e("SELL",text);
        }
        else if(access==false){
            query = query.orderByChild("user").equalTo(current_user);

        }


        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
                .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Product,ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {

                holder.setTitle(model.getChasis_no());
                holder.setImage(model.getImage(),getApplicationContext());
                holder.setDetail(model.getBrand(),model.getEngine_power(),model.getModel(),model.getColor());

                final String product_id = getRef(position).getKey();
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detailView = new Intent(Sell.this,ProductDetailView.class);
                        detailView.putExtra("key",product_id);
                        detailView.putExtra("title",model.getBrand());
                        detailView.putExtra("user",current_user);
                        detailView.putExtra("access",access);
                        detailView.putExtra("caller_activity_name",Sell.class.toString());
                        startActivity(detailView);
                    }
                });


            }


            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_product_layout,viewGroup,false);

                return new ProductViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);

        adapter.startListening();

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String mtitle){
            TextView textView = mView.findViewById(R.id.product_title);
            textView.setText(mtitle);

        }
        public void setDetail(String brand,String engine,String model,String color){
            TextView textView = mView.findViewById(R.id.product_detail);
            textView.setText(brand.toUpperCase()+"/"+engine+"/"+model+"/"+color.toUpperCase());
        }

        public void setImage(String img,Context ctx){
            CircleImageView circleImageView = mView.findViewById(R.id.product_image);
            Picasso.get().load(img).placeholder(R.drawable.motoicon).into(circleImageView);

        }
    }
}
