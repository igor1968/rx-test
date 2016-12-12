package com.igordanilchik.android.rxandroid_test.api;


import com.igordanilchik.android.rxandroid_test.model.Catalogue;

import io.rx_cache.EvictProvider;
import rx.Observable;


public interface CacheProviders {
    Observable<Catalogue> getCatalogueEvictProvider(Observable<Catalogue> observable, EvictProvider evictProvider);
}
