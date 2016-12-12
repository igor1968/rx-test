package com.igordanilchik.android.rxandroid_test.ui;

import com.igordanilchik.android.rxandroid_test.data.Repository;

public interface ViewContract {

    void showCategory(int categoryId);

    void showOffer(int offerId);

    public Repository getRepository();
}
