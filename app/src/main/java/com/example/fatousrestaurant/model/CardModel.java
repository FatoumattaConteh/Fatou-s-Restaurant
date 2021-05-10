package com.example.fatousrestaurant.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class CardModel extends SugarRecord {
   public String product_name="";

    @Unique
    public String product_id="";
    public String product_photo="";
    public int quantity=1;
    public String product_price="";
}
