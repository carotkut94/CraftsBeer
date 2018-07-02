package com.death.craftsbeer.network.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class BeerResponse{

	@Id
	long beerId;

	@SerializedName("abv")
	private String abv;

	@SerializedName("ounces")
	private double ounces;

	@SerializedName("name")
	private String name;

	@SerializedName("style")
	private String style;

	@SerializedName("id")
	private long id;

	@SerializedName("ibu")
	private String ibu;

	public void setAbv(String abv){
		this.abv = abv;
	}

	public String getAbv(){
		return abv;
	}

	public void setOunces(double ounces){
		this.ounces = ounces;
	}

	public double getOunces(){
		return ounces;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setStyle(String style){
		this.style = style;
	}

	public String getStyle(){
		return style;
	}

	public void setId(long id){
		this.id = id;
	}

	public long getId(){
		return id;
	}

	public void setIbu(String ibu){
		this.ibu = ibu;
	}

	public String getIbu(){
		return ibu;
	}

	public long getBeerId() {
		return beerId;
	}

	public void setBeerId(long beerId) {
		this.beerId = beerId;
	}

	public BeerResponse(long beerId, String abv, double ounces, String name, String style, long id, String ibu) {
		this.beerId = beerId;
		this.abv = abv;
		this.ounces = ounces;
		this.name = name;
		this.style = style;
		this.id = id;
		this.ibu = ibu;
	}

	@Override
 	public String toString(){
		return 
			"BeerResponse{" + 
			"abv = '" + abv + '\'' + 
			",ounces = '" + ounces + '\'' + 
			",name = '" + name + '\'' + 
			",style = '" + style + '\'' + 
			",id = '" + id + '\'' + 
			",ibu = '" + ibu + '\'' + 
			"}";
		}
}