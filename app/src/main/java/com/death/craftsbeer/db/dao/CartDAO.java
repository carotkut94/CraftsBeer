package com.death.craftsbeer.db.dao;

import com.death.craftsbeer.AppController;
import com.death.craftsbeer.db.model.Cart;

import io.objectbox.Box;

public class CartDAO {
    public Box<Cart> getBoxForCart(){
        return AppController.getBoxStore().boxFor(Cart.class);
    }
}
