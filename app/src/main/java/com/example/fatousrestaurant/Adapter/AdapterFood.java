package com.example.fatousrestaurant.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fatousrestaurant.R;
import com.example.fatousrestaurant.model.FoodModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterFood extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<FoodModel> items=new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    Context context;

    public void setOnItemClickListener(final OnItemClickListener itemClickListener){this.onItemClickListener=itemClickListener;}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food_card,parent,false);
        vh=new OriginalViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof OriginalViewHolder){
            OriginalViewHolder view=(OriginalViewHolder) holder;

            final FoodModel f=items.get(position);
            view.title.setText(f.title);
            view.price.setText(f.price);

            Glide.with(context)
                    .load(f.photo)
                    .centerCrop()
                    .into(view.image);

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
        void onItemClick(View view, FoodModel obj, int pos);
    }

    public AdapterFood(List<FoodModel> items,Context context){
        this.items=items;
        this.context=context;

    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public ImageView image;
        public TextView title;
        public  TextView price;
        public View lyt_parent;

        public  OriginalViewHolder(View v){
            super(v);
            image=(ImageView)v.findViewById(R.id.image);
            title=(TextView)v.findViewById(R.id.title);
            price=(TextView)v.findViewById(R.id.price);
            lyt_parent=(View)v.findViewById(R.id.lyt_parent);

        }


    }
}
