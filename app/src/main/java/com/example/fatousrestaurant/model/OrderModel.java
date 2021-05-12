package com.example.fatousrestaurant.model;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {
    public String order_id="";
    public UserModel customer=new UserModel();
    public List<CardModel> cart=new ArrayList<>();
}
