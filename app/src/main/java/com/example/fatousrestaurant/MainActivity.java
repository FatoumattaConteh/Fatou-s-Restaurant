package com.example.fatousrestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fatousrestaurant.activities.FoodAddActivity;
import com.example.fatousrestaurant.activities.SignInActivity;
import com.example.fatousrestaurant.activities.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void add_food(View view) {

        Intent i =new Intent(this, FoodAddActivity.class);
        startActivity(i);
    }

    public void openSignup(View view) {

        Intent i =new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    public void openSignIn(View view) {

        Intent i =new Intent(this, SignInActivity.class);
        startActivity(i);
    }
}