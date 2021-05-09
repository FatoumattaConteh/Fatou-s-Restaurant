package com.example.fatousrestaurant;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.fatousrestaurant.Adapter.AdapterFood;
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


    List<FoodModel> foods=new ArrayList<>();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    private void get_data() {
        db.collection("FOOD").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                Toast.makeText(MainActivity.this,"You Clicked On ==> "+obj.title,Toast.LENGTH_SHORT).show();
            }
        });


            }




    private void initToolbar(){
        progressBar=findViewById(R.id.progressBar);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.GONE);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
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