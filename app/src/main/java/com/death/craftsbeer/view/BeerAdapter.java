package com.death.craftsbeer.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.death.craftsbeer.R;
import com.death.craftsbeer.network.model.BeerResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BeerAdapter extends RecyclerView.Adapter<BeerAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private List<BeerResponse> beerList;
    private BeerAdapterListener listener;
    private List<BeerResponse> beerListFiltered;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.addToCartButton)
        ImageButton addToCartButton;

        @BindView(R.id.beerName)
        TextView beerName;

        @BindView(R.id.aContent)
        TextView alcoholContent;

        @BindView(R.id.styleName)
        TextView styleName;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            addToCartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onAddToCartClicked(beerListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    public BeerAdapter(Context context, List<BeerResponse> beerList, BeerAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.beerList = beerList;
        beerListFiltered = this.beerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.beer_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final BeerResponse beer = beerListFiltered.get(position);

        holder.beerName.setText(beer.getName());

        holder.styleName.setText(beer.getStyle());
        holder.alcoholContent.setText(String.format("Alcohol Content : %s", beer.getAbv()));

    }

    @Override
    public int getItemCount() {
        return beerListFiltered.size();
    }

    public interface BeerAdapterListener {
        void onAddToCartClicked(BeerResponse beer);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    beerListFiltered = beerList;
                } else {
                    List<BeerResponse> filteredList = new ArrayList<>();
                    for (BeerResponse singleBeer : beerList) {
                        if (singleBeer.getName().toLowerCase().contains(charString.toLowerCase()) || singleBeer.getStyle().contains(charSequence)) {
                            filteredList.add(singleBeer);
                        }
                    }
                    beerListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = beerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                beerListFiltered = (ArrayList<BeerResponse>) filterResults.values;
                Log.e("Filtered list size:",""+beerListFiltered.size());
                notifyDataSetChanged();
            }
        };
    }
}