package com.death.craftsbeer.view.cart;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.death.craftsbeer.R;
import com.death.craftsbeer.db.dao.BeersDAO;
import com.death.craftsbeer.db.model.Cart;
import com.death.craftsbeer.db.repository.BeerRepository;
import com.death.craftsbeer.network.model.BeerResponse;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder>{

    private Context context;
    private List<Cart> cartItems;
    private CartAdapter.CartActionListener cartActionListener;

    private BeersDAO beersDAO = new BeersDAO();
    private BeerRepository repository = new BeerRepository(beersDAO);

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.remove)
        Button remove;

        @BindView(R.id.beerName)
        TextView beerName;

        @BindView(R.id.beerStyle)
        TextView styleName;

        @BindView(R.id.quantity)
        TextView quantityView;

        @BindView(R.id.checkout)
        Button checkout;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cartActionListener.onRemoveItem(cartItems.get(getAdapterPosition()));
                }
            });

            checkout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cartActionListener.onCheckOutClicked(cartItems.get(getAdapterPosition()));
                }
            });
        }
    }

    public CartAdapter(Context context, List<Cart> cartList, CartAdapter.CartActionListener listener) {
        this.context = context;
        this.cartItems = cartList;
        cartActionListener = listener;
    }

    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.cart_row, parent, false);

        return new CartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CartAdapter.MyViewHolder holder, final int position) {
        final Cart cart = cartItems.get(position);
        BeerResponse beer = repository.getBeerById(cart.getBeerId());
        if(beer!=null) {
            holder.beerName.setText(beer.getName());
            holder.styleName.setText(beer.getStyle());
        }
        holder.quantityView.setText(String.valueOf(cart.getCount()));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface CartActionListener {
        void onRemoveItem(Cart cartItem);
        void onCheckOutClicked(Cart cartItem);
    }



}