package com.levent.cryptocurrencyapi.service;

import com.levent.cryptocurrencyapi.model.CryptoModel;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ICryptoAPI {

    //https://api.nomics.com/v1/prices?key=8e219b5bd2a34c132b30cb70aa81ef978871bbce
    //url base ->

     @GET("prices?key=8e219b5bd2a34c132b30cb70aa81ef978871bbce")
    Observable<List<CryptoModel>> getData();
    //Call<List<CryptoModel>> getData();

}
