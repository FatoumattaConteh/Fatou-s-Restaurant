package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.FoodModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    String id=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        context=this;

        Intent i=getIntent();
        id=i.getStringExtra("id");
        if (id==null|| id.length()<1){
            Toast.makeText(this, "ID not found", Toast.LENGTH_SHORT).show();
            finish();
        }
        bind_views();
        get_data();
    }

    FoodModel food=new FoodModel();
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    Context context;
    private void get_data() {
        db.collection("FOODS").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    Toast.makeText(context, "Food not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }


                food=documentSnapshot.toObject(FoodModel.class);
                feed_data();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed because" +e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }


    ImageView product_photo;
    TextView product_title;
    TextView product_price;
    TextView product_details;
    Button add_to_cart;
    private void bind_views() {
        product_photo=findViewById(R.id.product_photo);
        product_title=findViewById(R.id.product_title);
        product_price=findViewById(R.id.product_price);
        product_details=findViewById(R.id.product_details);
        add_to_cart=findViewById(R.id.add_to_cart);

    }

    private void feed_data() {

        Glide.with(context)
                .load(food.photo)
                .centerCrop()
                .into(product_photo);

        product_title.setText(food.title);
        product_price.setText(food.price+" ");
        product_details.setText(food.description+" ");
        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Adding to Cart", Toast.LENGTH_SHORT).show();
            }
        });


    }
}