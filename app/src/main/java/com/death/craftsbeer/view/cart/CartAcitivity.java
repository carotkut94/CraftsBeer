package com.death.craftsbeer.view.cart;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.death.craftsbeer.R;
import com.death.craftsbeer.db.dao.BeersDAO;
import com.death.craftsbeer.db.dao.CartDAO;
import com.death.craftsbeer.db.model.Cart;
import com.death.craftsbeer.db.repository.BeerRepository;
import com.death.craftsbeer.db.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAcitivity extends AppCompatActivity implements CartAdapter.CartActionListener{

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.noBeers)
    TextView noBeers;

    List<Cart> carts;
    CartAdapter cartAdapter;

    CartDAO cartDAO = new CartDAO();
    BeersDAO beersDAO = new BeersDAO();
    private CartRepository cartRepository;
    private BeerRepository beerRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_acitivity);

        carts = new ArrayList<>();
        ButterKnife.bind(this);

        toggleNoCartItems(View.GONE);

        cartRepository = new CartRepository(cartDAO);
        beerRepository = new BeerRepository(beersDAO);
        carts.addAll(cartRepository.getCartItems());
        cartAdapter = new CartAdapter(this, carts, this);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
    }

    @Override
    public void onRemoveItem(Cart cartItem) {
        carts.remove(cartItem);
        cartAdapter.notifyDataSetChanged();
        cartDAO.getBoxForCart().remove(cartItem);
        if(carts.size()==0){
            toggleNoCartItems(View.VISIBLE);
        }
    }

    @Override
    public void onCheckOutClicked(Cart cartItem) {
        Toast.makeText(this,"Beer "+beerRepository.getBeerById(cartItem.getBeerId()).getName()+" Will be delivered", Toast.LENGTH_LONG).show();
    }

    private void toggleNoCartItems(int visiblity){
        noBeers.setVisibility(visiblity);
    }
}
