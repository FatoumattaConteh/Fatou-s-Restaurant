package com.example.fatousrestaurant.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.Calendar;
import java.util.List;

import static com.example.fatousrestaurant.tools.Utils.get_logged_in;

public class SignUpActivity extends AppCompatActivity {

    UserModel loggedInUser=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        bind_views();
       loggedInUser= Utils.get_logged_in();
        if (loggedInUser!=null){
            Toast.makeText(this, "You are Alreadylogged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }


    EditText first_name_view,last_name_view,email_view,password_view,phone_number_view,address_view;
    Button sign_up_button;
    private void bind_views() {

        first_name_view=findViewById(R.id.first_name_view);
        last_name_view=findViewById(R.id.last_name_view);
        email_view=findViewById(R.id.email_view);
        password_view=findViewById(R.id.password_view);
        phone_number_view=findViewById(R.id.phone_number_view);
        address_view=findViewById(R.id.address_view);
        sign_up_button=findViewById(R.id.sign_up_button);
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate_data();
            }
        });




    }

    UserModel new_user=new UserModel();
    private void validate_data(){

        new_user.first_name=first_name_view.getText().toString();
        if (new_user.first_name.length()<2){
            Toast.makeText(this, "First name is too short", Toast.LENGTH_SHORT).show();
           first_name_view.requestFocus();
            return;
        }


        new_user.last_name=last_name_view.getText().toString();
        if (new_user.last_name.length()<2){
            Toast.makeText(this, "Last name is too short", Toast.LENGTH_SHORT).show();
            last_name_view.requestFocus();
            return;
        }



        if (email_view.getText().toString().isEmpty()){
            email_view.requestFocus();
            email_view.setError("Email cannot be empty");
            return;
        }else {

            email_view.setError(null);
        }


        new_user.email =email_view.getText().toString();
        if (!Patterns.EMAIL_ADDRESS.matcher(new_user.email).matches()){
            email_view.setError("Invalid Email address");
            email_view.requestFocus();
            return;
        }
        else {

            email_view.setError(null);
        }





        new_user.password=password_view.getText().toString();
        if (new_user.password.length()<2){
            Toast.makeText(this, "Password is too short", Toast.LENGTH_SHORT).show();
            password_view.requestFocus();
            return;
        }



        new_user.address=address_view.getText().toString();
        new_user.phone_number=phone_number_view.getText().toString();
        new_user.user_type="customer";
        new_user.gender="";
        new_user.profile_photo="";


        submit_data();



        }

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        public static final String USERS_TABLE="USERS";
        ProgressDialog pd;
        private void submit_data() {

            pd=new ProgressDialog(this);
            pd.setTitle("Please Wait....");
            pd.setCancelable(false);
            pd.show();

            db.collection(USERS_TABLE)
                    .whereEqualTo("email",new_user.email)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()){

                                Toast.makeText(SignUpActivity.this, "A user with same email already exist", Toast.LENGTH_SHORT).show();
                                pd.hide();
                                pd.dismiss();

                                return;

                            }
                            new_user.user_id=db.collection(USERS_TABLE).document().getId();
                            new_user.reg_date= String.valueOf(Calendar.getInstance().getTimeInMillis()+ "");

                            db.collection(USERS_TABLE).document(new_user.user_id).set(new_user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                        Toast.makeText(SignUpActivity.this, "User Account was created successfully", Toast.LENGTH_SHORT).show();
                                            pd.hide();
                                            pd.dismiss();

                                            if (login_user()){
                                                Toast.makeText(SignUpActivity.this, "Your Account was Logged in Successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(SignUpActivity.this, MainActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                SignUpActivity.this.startActivity(intent);
                                                return;
                                            }else {
                                                Toast.makeText(SignUpActivity.this, "Failed to Logged in to your Account", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignUpActivity.this, "Failed to connect to internet because"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    pd.hide();
                                    pd.dismiss();
                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignUpActivity.this, "Failed to connect to internet because"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.hide();
                    pd.dismiss();
                }
            });
         }

         private static final String TAG="__SignUpActivity";


         private boolean login_user(){
             try {
                 UserModel.save(new_user);
                return true;
             }catch (Exception e){
                 Log.d(TAG,"login_user: sfailed to save user because " +e.getMessage());
                 return false;
             }
         }
}