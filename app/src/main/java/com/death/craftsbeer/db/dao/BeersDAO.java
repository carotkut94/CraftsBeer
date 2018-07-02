package com.death.craftsbeer.db.dao;

import com.death.craftsbeer.AppController;
import com.death.craftsbeer.network.model.BeerResponse;

import io.objectbox.Box;

public class BeersDAO {
    public Box<BeerResponse> getBoxForBeers(){
        return AppController.getBoxStore().boxFor(BeerResponse.class);
    }
}
