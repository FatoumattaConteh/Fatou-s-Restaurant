package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fatousrestaurant.Adapter.AdapterFood;
import com.example.fatousrestaurant.Adapter.AdapterOrders;
import com.example.fatousrestaurant.MainActivity;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.FoodModel;
import com.example.fatousrestaurant.model.OrderModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.fatousrestaurant.MainActivity.setSystemBarColor;
import static com.example.fatousrestaurant.activities.CheckOutActivity.CUSTOMER_ORDERS;

public class AdminOrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_orders);

        initToolbar();
        get_data();
    }



    List<OrderModel> orders=new ArrayList<>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private void get_data() {
        db.collection(CUSTOMER_ORDERS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                orders=queryDocumentSnapshots.toObjects(OrderModel.class);
                initComponents();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                initComponents();
            }
        });
    }


    private AdapterOrders mAdapter;
    private void initComponents() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);


        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        //set data and list adapter
        mAdapter= new AdapterOrders(orders,this,"0");
        recyclerView.setAdapter(mAdapter);



        //an item list clicked
        mAdapter.setOnItemClickListener(new AdapterOrders.OnItemClickListener() {
            @Override
            public void onItemClick(View view, OrderModel obj, int pos) {
                Intent i=new Intent(AdminOrdersActivity.this, AdminOrderActivity.class);
                i.putExtra("order_id", obj.order_id);
                AdminOrdersActivity.this.startActivity(i);


            }
        });


    }




    ProgressBar progressBar;
    private RecyclerView recyclerView;
    private void initToolbar(){
        progressBar=findViewById(R.id.progressBar);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fatou's Restaurant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSystemBarColor(this);
    }


}