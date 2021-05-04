package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fatousrestaurant.MainActivity;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.UserModel;
import com.example.fatousrestaurant.tools.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SignInActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        context=this;

        bind_views();

        UserModel loggedInUser= Utils.get_logged_in();
        if (loggedInUser!=null){
            Toast.makeText(this, "You are Already logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }


    EditText password_view, email_view;
    Button sign_in_button;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final String USERS_TABLE = "USERS";

    private void bind_views() {
        email_view = findViewById(R.id.email_view);
        password_view = findViewById(R.id.password_view);
        sign_in_button = findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign_user();
            }


        });

    }

    String email_value = "";
    String password_value = "";
    ProgressDialog pd;
    Context context;

    private void sign_user() {
        email_value = email_view.getText().toString().trim();
        password_value = password_view.getText().toString().trim();

        if (email_value.isEmpty() || password_value.isEmpty()) {
            Toast.makeText(this, "You Must fill both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        pd = new ProgressDialog(this);
        pd.setTitle("Please Wait....");
        pd.setCancelable(false);
        pd.show();

        db.collection(USERS_TABLE)
                .whereEqualTo("email", email_value)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots==null){

                            Toast.makeText(context, "Email not found on database", Toast.LENGTH_LONG).show();
                            pd.hide();
                            pd.dismiss();

                            return;

                        }
                        if (queryDocumentSnapshots.isEmpty()){

                            Toast.makeText(context, "Email not found on database", Toast.LENGTH_LONG).show();
                            pd.hide();
                            pd.dismiss();

                            return;

                        }

                        List<UserModel> users=queryDocumentSnapshots.toObjects(UserModel.class);

                        if (users.get(0).password.equals(password_value)){
                            Toast.makeText(context, "Wrong Password", Toast.LENGTH_LONG).show();
                            pd.hide();
                            pd.dismiss();

                            return;

                        }

                        if (login_user(users.get(0))){
                            Toast.makeText(context, "Your Account was Logged in Successfully", Toast.LENGTH_LONG).show();
                            Intent intent=new Intent(SignInActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            SignInActivity.this.startActivity(intent);
                            return;
                        }else {
                            Toast.makeText(context, "Failed to Logged in to your Account", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignInActivity.this, "Failed to connect to internet because" + e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.hide();
                pd.dismiss();
            }
        });
    }


    private boolean login_user(UserModel u){
        try {
            UserModel.save(u);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
