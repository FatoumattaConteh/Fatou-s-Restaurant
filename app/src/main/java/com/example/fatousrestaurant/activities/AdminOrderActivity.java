package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fatousrestaurant.Adapter.AdapterFood;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.CardModel;
import com.example.fatousrestaurant.model.FoodModel;
import com.example.fatousrestaurant.model.OrderModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import static com.example.fatousrestaurant.activities.CheckOutActivity.CUSTOMER_ORDERS;

public class AdminOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order);

        context=this;

        order_id=getIntent().getStringExtra("order_id");
        if (order_id==null){
            Toast.makeText(this, "Order Id not found  ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

       // initToolbar();
        get_data();
    }

    FirebaseFirestore db=FirebaseFirestore.getInstance();
    OrderModel order;
    private void get_data() {
        db.collection(CUSTOMER_ORDERS).document(order_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (!documentSnapshot.exists()){
                    Toast.makeText(context, "Order not found", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                order=documentSnapshot.toObject(OrderModel.class);
                feed_data();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to get Order because "+e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        });
    }


    RecyclerView recyclerView;
    private AdapterFood adapterFood;
    TextView  order_id_view;
    EditText customer_name,customer_address,customer_contact;
    Button delete_order;
    private void feed_data() {

        cardModels=order.cart;
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
            recyclerView=findViewById(R.id.card_products);
            order_id_view=findViewById(R.id.order_id);
           customer_name=findViewById(R.id.customer_name);
           customer_address=findViewById(R.id.customer_address);
           customer_contact=findViewById(R.id.customer_contact);
            order_id_view.setText("ORDER #"+order.order_id);
            customer_name.setText(order.customer.first_name+" "+ order.customer.last_name);
            customer_address.setText(order.customer.address);
            customer_contact.setText(order.customer.email);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);

            adapterFood=new AdapterFood(foodModels,this,"1");
            recyclerView.setAdapter(adapterFood);

           delete_order=findViewById(R.id.delete_order);
            delete_order.setOnClickListener(new View.OnClickListener() {
               @Override
                public void onClick(View v) {
                    db.collection(CUSTOMER_ORDERS).document(order_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(context, "Order Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Order failed to be deleted because "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    });
                }
            });

        }



    String order_id=null;
    Context context;
    List<FoodModel> foodModels=null;
    List<CardModel> cardModels=new ArrayList<>();

}