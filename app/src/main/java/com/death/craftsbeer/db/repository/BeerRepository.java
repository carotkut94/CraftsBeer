package com.death.craftsbeer.db.repository;

import com.death.craftsbeer.db.dao.BeersDAO;
import com.death.craftsbeer.network.model.BeerResponse;
import com.death.craftsbeer.network.model.BeerResponse_;

import java.util.List;

import io.objectbox.Box;

public class BeerRepository {
    BeersDAO beersDAO;
    Box<BeerResponse> beersBox;

    public BeerRepository(BeersDAO beersDAO) {
        this.beersDAO = beersDAO;
        this.beersBox = beersDAO.getBoxForBeers();
    }

    public List<BeerResponse> getAllBeers(){
        return beersBox.getAll();
    }

    public List<BeerResponse> getBeersByRange(int startIndex, int endIndex){
        if(getAllBeers().size()<endIndex && startIndex<endIndex) {
            return beersBox.getAll().subList(startIndex, endIndex);
        }else{
            return getAllBeers();
        }
    }

    public BeerResponse getBeerById(long id){
        return beersBox.query().equal(BeerResponse_.id, id).build().findFirst();
    }

    public void insertSingleBeer(BeerResponse beer){
        beersBox.put(beer);
    }

    public void insertAllBeers(List<BeerResponse> beers){
        beersBox.put(beers);
    }

    public List<BeerResponse> orderByAlcoholContent(boolean isAscending){
        if(isAscending){
            return beersBox.query().order(BeerResponse_.abv).build().find();
        }
        return beersBox.query().orderDesc(BeerResponse_.abv).build().find();
    }


    public List<BeerResponse> orderByBitterness(){
        return beersBox.query().orderDesc(BeerResponse_.ibu).build().find();
    }
    public List<BeerResponse> getBeersByStyle(String stylePattern){
        return beersBox.query().contains(BeerResponse_.style, stylePattern).build().find();
    }

}
