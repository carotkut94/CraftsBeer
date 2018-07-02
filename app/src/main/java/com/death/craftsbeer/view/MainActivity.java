package com.death.craftsbeer.view;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.death.craftsbeer.R;
import com.death.craftsbeer.db.dao.BeersDAO;
import com.death.craftsbeer.db.dao.CartDAO;
import com.death.craftsbeer.db.model.Cart;
import com.death.craftsbeer.db.repository.BeerRepository;
import com.death.craftsbeer.db.repository.CartRepository;
import com.death.craftsbeer.network.ApiClient;
import com.death.craftsbeer.network.ApiService;
import com.death.craftsbeer.network.model.BeerResponse;
import com.death.craftsbeer.view.cart.CartAcitivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements BeerAdapter.BeerAdapterListener, PopupMenu.OnMenuItemClickListener {

    private CompositeDisposable disposable = new CompositeDisposable();
    private ApiService apiService;

    private BeersDAO beersDAO = new BeersDAO();
    private CartDAO cartDAO = new CartDAO();

    private BeerRepository beerRepository;
    private CartRepository cartRepository;

    private List<BeerResponse> beerResponse = new ArrayList<>();

    BeerAdapter beerAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.noBeers)
    TextView noBeers;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.search_view)
    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView(R.layout.activity_main);

        //View binding
        ButterKnife.bind(this);

        //Repository creation
        beerRepository = new BeerRepository(beersDAO);
        cartRepository = new CartRepository(cartDAO);

        // Setting up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Crafts Beer");
        setSupportActionBar(toolbar);

        //Setting adapter for beer recycler view
        beerAdapter = new BeerAdapter(this, beerResponse,this);
        recyclerView.setAdapter(beerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Initializing retrofit and getting service
        apiService = ApiClient.getRetrofit().create(ApiService.class);


        // Checking if data is already in database, if yes then no need to call service.
        if(beerRepository.getAllBeers().size()>0){
            beerResponse.clear();
            beerResponse.addAll(beerRepository.getAllBeers());
            beerAdapter.notifyDataSetChanged();
            toggleNoBeersMessage();
        }else{
            // if data is not available in database, query and insert it into database.
            fetchAllBeers();
        }

        // Search view query processor and will filter data in recycler view
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                beerAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    // fetches all data from service using rxJava
    private void fetchAllBeers() {
        disposable.add(
                apiService.getAllBeers()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<BeerResponse>>() {
                            @Override
                            public void onSuccess(List<BeerResponse> beers) {
                                beerResponse.addAll(beers);
                                beerAdapter.notifyDataSetChanged();
                                Log.e("Beers", String.valueOf(beerResponse.size()));
                                beerRepository.insertAllBeers(beers);
                                toggleNoBeersMessage();
                            }
                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                showMessage(e.getLocalizedMessage());
                                if(beerRepository.getAllBeers().size()>0){
                                    beerResponse.clear();
                                    beerResponse.addAll(beerRepository.getAllBeers());
                                    beerAdapter.notifyDataSetChanged();
                                    toggleNoBeersMessage();
                                }else{
                                    noBeers.setVisibility(View.VISIBLE);
                                    noBeers.setText("Ohh! snap, internet is broken");
                                }

                            }
                        })
        );
    }

    private void showMessage(String message) {
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void toggleNoBeersMessage(){
        noBeers.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);

        MenuItem item = menu.findItem(R.id.navigation_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_cart:
                if(cartRepository.getCartItems().size()>0){
                    startActivity(new Intent(this, CartAcitivity.class));
                }else{
                    showMessage("No items in cart");
                }
                break;
            case R.id.navigation_filter:
                showPopup(findViewById(R.id.navigation_filter));
                break;
            case R.id.navigation_profile:
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://github.com/carotkut94?tab=repositories"));
                startActivity(intent);
                return true;
        }
        return true;
    }

    @Override
    public void onAddToCartClicked(BeerResponse beer) {
        Cart cart = cartRepository.getCartItemByBeerId(beer.getId());
        if(cart!=null){
            cart.setCount(cart.getCount()+1);
            cartRepository.insertSingleBeer(cart);
            showMessage("One more "+beer.getName()+" added to cart");
        }else {
            Cart newCartItem = new Cart();
            newCartItem.setBeerId(beer.getId());
            newCartItem.setCount(1);
            cartRepository.insertSingleBeer(newCartItem);
            showMessage(beer.getName()+" added to cart");
        }
    }

    @Override
    public void onBackPressed() {
        if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        disposable.dispose();
        super.onDestroy();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_filter, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ale:
                filterByStyle("ale");
                return true;
            case R.id.stout:
                filterByStyle("stout");
                return true;
            case R.id.ipa:
                filterByStyle("ipa");
                return true;
            case R.id.lager:
                filterByStyle("lager");
                return true;
            case R.id.default_listing:
                beerResponse.clear();
                beerResponse.addAll(beerRepository.getAllBeers());
                beerAdapter.notifyDataSetChanged();
                showMessage("All beers");
                return true;
            case R.id.alcohol:
                beerResponse.clear();
                beerResponse.addAll(beerRepository.orderByAlcoholContent(false));
                beerAdapter.notifyDataSetChanged();
                showMessage("Sorted by alcohol content");
                return true;
            case R.id.alcohol_asc:
                beerResponse.clear();
                beerResponse.addAll(beerRepository.orderByAlcoholContent(true));
                beerAdapter.notifyDataSetChanged();
                showMessage("Sorted by alcohol content(Ascending)");
                return true;
            case R.id.bitterness:
                beerResponse.clear();
                beerResponse.addAll(beerRepository.orderByBitterness());
                beerAdapter.notifyDataSetChanged();
                showMessage("Sorted by bitterness content");
                return true;
            default:
                return false;
        }
    }

    private void filterByStyle(String style){
        beerResponse.clear();
        beerResponse.addAll(beerRepository.getBeersByStyle(style));
        beerAdapter.notifyDataSetChanged();
        showMessage(beerResponse.size()+" beers processed");
    }
}
