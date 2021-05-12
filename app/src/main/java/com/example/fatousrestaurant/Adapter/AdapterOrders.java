package com.example.fatousrestaurant.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.CardModel;
import com.example.fatousrestaurant.model.FoodModel;
import com.example.fatousrestaurant.model.OrderModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterOrders extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<OrderModel> items=new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    Context context;
    String layout_style="0";

    public void setOnItemClickListener(final OnItemClickListener itemClickListener){this.onItemClickListener=itemClickListener;}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v;
        if (layout_style.equals("0")){

           v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);

        }
        else if (layout_style.equals("1")){
            v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);
        } else{
           v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);
        }

        vh=new OriginalViewHolder(v);
        return vh;

    }

    int total=0;
    //replaces the contents of the view

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder view=(OriginalViewHolder) holder;

            final OrderModel o=items.get(position);
            view.customer_name.setText(o.customer.first_name +" "+o.customer.last_name);
            view.customer_address.setText(o.customer.address);



            total=0;
            for (CardModel c:o.cart){

                try {

                    total+= Integer.valueOf(c.product_price);

                }catch (Exception e){

                }


            }
            view.total_price.setText("$" +total);



            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null){
                        onItemClickListener.onItemClick(v,items.get(position),position);
                    }
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public  interface OnItemClickListener{
        void onItemClick(View view, OrderModel obj, int pos);
    }

    public AdapterOrders(List<OrderModel> items, Context context, String layout_style){
        this.items=items;
        this.context=context;
        this.layout_style=layout_style;

    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public TextView customer_name;
        public  TextView customer_address;
        public  TextView total_price;
        public View lyt_parent;

        public  OriginalViewHolder(View v){
            super(v);
            customer_name=(TextView)v.findViewById(R.id.customer_name);
            customer_address=(TextView)v.findViewById(R.id.customer_address);
            total_price=(TextView)v.findViewById(R.id.total_price);
            lyt_parent=(View)v.findViewById(R.id.lyt_parent);

        }


    }
}
