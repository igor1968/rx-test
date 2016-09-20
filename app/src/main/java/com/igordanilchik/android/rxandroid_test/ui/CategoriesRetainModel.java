package com.igordanilchik.android.rxandroid_test.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.igordanilchik.android.rxandroid_test.model.Catalogue;
import com.igordanilchik.android.rxandroid_test.model.Category;
import com.igordanilchik.android.rxandroid_test.model.Shop;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class CategoriesRetainModel extends Fragment {

    @Nullable
    public Shop content;
    @NonNull
    public List<Category> categories = new ArrayList<>();
    @Nullable
    public Observable<Catalogue> observable;
    public boolean isLoading;

    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}

