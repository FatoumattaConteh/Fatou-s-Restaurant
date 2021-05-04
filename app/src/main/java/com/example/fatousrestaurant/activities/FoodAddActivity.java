package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.FoodModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FoodAddActivity extends AppCompatActivity {


    ImageButton btn_done;
    ImageView  food_photo;
    private final int PICK_IMAGE_REQUEST=1;
    private final String FOOD_TABLE="FOODS";
    TextInputEditText Category_view;
    FoodModel new_food=new FoodModel();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_add);
        bind_views();

    }
    TextInputEditText food_name,food_description,food_price;

    private void bind_views() {
        new_food.food_id=db.collection(FOOD_TABLE).document().getId();
        btn_done=findViewById(R.id.btn_done);
        food_photo=findViewById(R.id.food_photo);
        food_name=findViewById(R.id.food_name);
        food_price=findViewById(R.id.food_price);
        food_description=findViewById(R.id.food_description);
        progressDialog=new ProgressDialog(this);
        Category_view=findViewById(R.id.Category_view);
        Category_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select_category();
            }
        });
        food_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseImage();

            }

        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                submit_food();
            }


        });
    }

            private void chooseImage() {

                Intent intent=new Intent();
                intent.setType("image/");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select food image"),PICK_IMAGE_REQUEST);


            }

            private static final String[] categories=new String[]{"Dessert","Beverage","Continental Dish","Refreshment"};

            int selected_category=-1;
            private void select_category(){

                AlertDialog.Builder builder =new AlertDialog.Builder(this);
                builder.setTitle("Select a category");
                builder.setSingleChoiceItems(categories, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category_view.setText(categories[which]);
                        selected_category=which;
                    }
                });
                builder.setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Category_view.setText(categories[which]);
                    }
                });
                builder.setNegativeButton("OK",null);
                builder.show();
            }

            private Uri imagepath;

            @Override
            protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);

                if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null){
                    imagepath=data.getData();

                    try {
                        Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                        food_photo.setImageBitmap(bitmap);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }


            StorageReference ref_main;
            ProgressDialog progressDialog;

            private void upload_to_firestore(){
                Toast.makeText(this, "Time to upload to firestore", Toast.LENGTH_SHORT).show();
                db.collection(FOOD_TABLE).document(new_food.food_id).set(new_food).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        progressDialog.hide();
                        Toast.makeText(FoodAddActivity.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hide();
                        Toast.makeText(FoodAddActivity.this, "Failed to be uploaded"+e.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

            }
            private void submit_food() {


                new_food.title=food_name.getText().toString();
                if (new_food.title.isEmpty()){
                    Toast.makeText(this,"Food name cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (food_price.getText().toString().isEmpty()){
                    Toast.makeText(this,"Food price cannot be empty",Toast.LENGTH_SHORT).show();
                return;
                }


                try {
                    new_food.price=Integer.valueOf(food_price.getText().toString());
                }catch (Exception e){

                }

                if (new_food.price>0){
                    Toast.makeText(this,"Food price cannot be zero",Toast.LENGTH_SHORT).show();
                return;
                }



                new_food.category=Category_view.getText().toString();
                if (new_food.category.isEmpty()){
                    Toast.makeText(this,"You must select a Food Category",Toast.LENGTH_SHORT).show();
                    select_category();
                    return;
                }


                new_food.description=food_description.getText().toString();
                if (new_food.description.isEmpty()){
                    Toast.makeText(this,"Food description cannot br empty",Toast.LENGTH_SHORT).show();
                }


                if (imagepath==null){
                    Toast.makeText(this, "You must select an image", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog=new ProgressDialog(this);
                progressDialog.setTitle("Uploading......");
                progressDialog.show();
                ref_main= FirebaseStorage.getInstance().getReference();
                ref_main.child("food/"+new_food.food_id).putFile(imagepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(FoodAddActivity.this, "Picture uploaded successfully", Toast.LENGTH_SHORT).show();

                        ref_main.child("food/"+new_food.food_id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                new_food.photo=uri.toString();
                                upload_to_firestore();
                                return;

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new_food.photo= "https://previews.123rf.com/images/boule13/boule131405/boule13140500164/27975461-wooden-dummy-waiter-with-tray-full-of-alcoholic-drinks-and-food-beer-martini-grapes-cheese-bread-bur.jpg";
                                upload_to_firestore();
                                return;
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FoodAddActivity.this, "Failed to upload photo", Toast.LENGTH_SHORT).show();
                        new_food.photo= "https://previews.123rf.com/images/boule13/boule131405/boule13140500164/27975461-wooden-dummy-waiter-with-tray-full-of-alcoholic-drinks-and-food-beer-martini-grapes-cheese-bread-bur.jpg";
                        upload_to_firestore();
                        return;
                    }
                });


            }


}