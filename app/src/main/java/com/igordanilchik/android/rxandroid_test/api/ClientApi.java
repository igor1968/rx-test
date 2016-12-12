package com.igordanilchik.android.rxandroid_test.api;


import com.igordanilchik.android.rxandroid_test.model.Catalogue;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface ClientApi {

    String API_BASE_URL = "http://ufa.farfor.ru";
    String API_KEY = "ukAXxeJYZN";

    @GET("/getyml")
    Observable<Catalogue> loadCatalogue(@Query("key") String key);
}
