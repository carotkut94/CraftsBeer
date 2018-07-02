package com.death.craftsbeer.db.repository;

import com.death.craftsbeer.db.dao.CartDAO;
import com.death.craftsbeer.db.model.Cart;
import com.death.craftsbeer.db.model.Cart_;

import java.util.List;

import io.objectbox.Box;

public class CartRepository {
    CartDAO cartDAO;
    Box<Cart> cartBox;

    public CartRepository(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
        this.cartBox = cartDAO.getBoxForCart();
    }

    public List<Cart> getCartItems(){
        return cartBox.getAll();
    }

    public Cart getCartItemByBeerId(long id){
        return cartBox.query().equal(Cart_.beerId, id).build().findFirst();
    }

    public void insertSingleBeer(Cart cart){
        cartBox.put(cart);
    }

    public void insertAllBeers(List<Cart> carts){
        cartBox.put(carts);
    }

}
