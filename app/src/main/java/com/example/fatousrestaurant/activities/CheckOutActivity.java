package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fatousrestaurant.Adapter.AdapterFood;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.CardModel;
import com.example.fatousrestaurant.model.FoodModel;
import com.example.fatousrestaurant.model.OrderModel;
import com.example.fatousrestaurant.model.UserModel;
import com.example.fatousrestaurant.tools.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;

import static com.example.fatousrestaurant.MainActivity.setSystemBarColor;

public class CheckOutActivity extends AppCompatActivity {

    UserModel loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

       loggedInUser= Utils.get_logged_in();
        if (loggedInUser==null){
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(this,SignUpActivity.class);
            this.startActivity(i);
            finish();
            return;
        }

        //initToolbar();
        
        get_card_data();


    }

    List<CardModel> cardModels=null;
    List<FoodModel> foodModels=new ArrayList<>();

    private void get_card_data() {


        try {

            cardModels=(List<CardModel>)CardModel.listAll(CardModel.class);

        }catch (Exception e){
            Toast.makeText(this, "Failed because " +e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (cardModels==null){
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (CardModel c:cardModels){
            FoodModel f=new FoodModel();
            f.title=c.product_name;
            f.category="";
            f.description="";
            try {
                f.price= Integer.valueOf(c.product_price);
            }catch (Exception e){

            }
            f.food_id=c.product_id;
            f.photo=c.product_photo;
            foodModels.add(f);


        }

       feed_cart_data();
    }


    RecyclerView recyclerView;
    private AdapterFood adapterFood;
    Button submit_data;
    private void feed_cart_data() {
        recyclerView=findViewById(R.id.card_products);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        adapterFood=new AdapterFood(foodModels,this,"1");
        recyclerView.setAdapter(adapterFood);

        submit_data=findViewById(R.id.submit_order);
        submit_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_data();
            }
        });

    }


    public static String CUSTOMER_ORDERS="CUSTOMER_ORDERS";
    FirebaseFirestore db=FirebaseFirestore.getInstance();
    ProgressDialog progressDialog;
    private void submit_data() {
        OrderModel orderModel=new OrderModel();
        orderModel.order_id=db.collection(CUSTOMER_ORDERS).document().getId();
        orderModel.customer=loggedInUser;
        orderModel.cart=cardModels;
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Please wait.....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        db.collection(CUSTOMER_ORDERS).document(orderModel.order_id).set(orderModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(CheckOutActivity.this, "Order Submitted Successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        progressDialog.dismiss();

                        try {
                            CardModel.deleteAll(CardModel.class);
                        }catch (Exception e){
                            Toast.makeText(CheckOutActivity.this, "Failed to clear the cart", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CheckOutActivity.this, "Failed to submit order because "+e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }


    private void initToolbar(){
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_40), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSystemBarColor(this);
    }
}