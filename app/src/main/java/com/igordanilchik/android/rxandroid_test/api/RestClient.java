package com.igordanilchik.android.rxandroid_test.api;

import android.support.annotation.NonNull;

import com.igordanilchik.android.rxandroid_test.model.Catalogue;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import rx.Observable;

public class RestClient {
    private static final String LOG_TAG = RestClient.class.getSimpleName();
    private static final String API_BASE_URL = "http://ufa.farfor.ru";
    private static final String API_KEY = "ukAXxeJYZN";

    @NonNull
    public Observable<Catalogue> loadCatalogue() {
        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .baseUrl(API_BASE_URL)
                .build();

        ClientApi client = retrofit.create(ClientApi.class);
        return client.loadCatalogue(API_KEY);
    }
}
