package com.example.fatousrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.fatousrestaurant.Adapter.AdapterFood;
import com.example.fatousrestaurant.activities.CheckOutActivity;
import com.example.fatousrestaurant.activities.FoodAddActivity;
import com.example.fatousrestaurant.activities.ProductActivity;
import com.example.fatousrestaurant.model.FoodModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private AdapterFood mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        get_data();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        
        if (item.getItemId()== R.id.action_cart){
           Intent i=new Intent(this, CheckOutActivity.class);
           this.startActivity(i);
        }
        else if (item.getItemId()== R.id.action_add_product) {
            Toast.makeText(this, "You clicked on ADD New Product", Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }

    List<FoodModel> foods=new ArrayList<>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private void get_data() {
        db.collection("FOODS").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                foods=queryDocumentSnapshots.toObjects(FoodModel.class);
                initComponents();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                initComponents();
            }
        });
    }

    ProgressBar progressBar;
    private void initComponents() {

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);


        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

        //set data and list adapter
        mAdapter=new AdapterFood(foods,this);
        recyclerView.setAdapter(mAdapter);



        //an item Click Listener
        mAdapter.setOnItemClickListener(new AdapterFood.OnItemClickListener() {
            @Override
            public void onItemClick(View view, FoodModel obj, int pos) {
                Intent i=new Intent(MainActivity.this, ProductActivity.class);
                i.putExtra("id", obj.food_id);
                MainActivity.this.startActivity(i);

            }
        });


            }




    private void initToolbar(){
        progressBar=findViewById(R.id.progressBar);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Fatou's Restaurant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSystemBarColor(this);
    }

    public  static void setSystemBarColor(Activity act){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Window window=act.getWindow();
            window.addFlags((WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS));
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }


}