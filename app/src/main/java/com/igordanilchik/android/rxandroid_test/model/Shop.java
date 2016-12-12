package com.igordanilchik.android.rxandroid_test.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "shop", strict = false)
public class Shop {

    @ElementList(name = "categories")
    List<Category> categories;

    @ElementList(name = "offers")
    List<Offer> offers;

    public Shop() {
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
