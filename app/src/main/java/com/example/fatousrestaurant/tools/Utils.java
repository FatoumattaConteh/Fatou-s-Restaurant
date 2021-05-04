package com.example.fatousrestaurant.tools;

import android.util.Log;

import com.example.fatousrestaurant.model.UserModel;

import java.util.List;

public class Utils {

    public static final UserModel get_logged_in(){
        try {
            List<UserModel> users=UserModel.listAll(UserModel.class);
            if (users==null){
                return null;

            }

            if (users.isEmpty()){
                return null;
            }
            return  users.get(0);
        }catch (Exception e){
            return null;

        }

    }
}
