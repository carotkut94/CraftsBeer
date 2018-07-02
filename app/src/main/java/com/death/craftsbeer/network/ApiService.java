package com.death.craftsbeer.network;

import com.death.craftsbeer.network.model.BeerResponse;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface ApiService {

    @GET("beercraft")
    Single<List<BeerResponse>> getAllBeers();

}