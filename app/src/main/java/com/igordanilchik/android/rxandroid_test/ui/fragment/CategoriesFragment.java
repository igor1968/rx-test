package com.igordanilchik.android.rxandroid_test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.model.Catalogue;
import com.igordanilchik.android.rxandroid_test.model.Category;
import com.igordanilchik.android.rxandroid_test.model.Offer;
import com.igordanilchik.android.rxandroid_test.ui.ViewContract;
import com.igordanilchik.android.rxandroid_test.ui.adapter.CategoriesAdapter;
import com.igordanilchik.android.rxandroid_test.utils.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;


public class CategoriesFragment extends Fragment {
    private static final String LOG_TAG = CategoriesFragment.class.getSimpleName();

    @BindView(R.id.catalogue_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.empty_state_container)
    LinearLayout emptyStateContainer;


    CategoriesAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private Unbinder unbinder;

    @NonNull
    private Subscription subscription = Subscriptions.empty();
    private boolean isLoading;

    @NonNull
    public static CategoriesFragment newInstance() {
        CategoriesFragment f = new CategoriesFragment();
        return f;
    }

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeContainer.setOnRefreshListener(this::refresh);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CategoriesAdapter();
        recyclerView.setAdapter(adapter);

        adapter.getPositionClicks().subscribe(category -> {
            categorySelected(category.getId());
        });

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        requestData(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        subscription.unsubscribe();
        Log.d(LOG_TAG, "Observer unsubscribed");

        recyclerView.setAdapter(null);
        adapter = null;
        unbinder.unbind();
    }

    private void refresh() {
        requestData(true);
    }

    private void requestData(boolean refresh) {
        subscription.unsubscribe();
        Log.d(LOG_TAG, "Observer unsubscribed");

        if (getActivity() instanceof ViewContract) {
            subscription = ((ViewContract) getActivity())
                    .getRepository()
                    .getCatalogue(refresh)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(() -> {
                        showRefresh(true);
                        isLoading = true;
                    })
                    .map(Catalogue::getShop)
                    .filter(shop -> shop != null && shop.getOffers() != null && shop.getCategories() != null)
                    .flatMap(shop -> {
                        List<Category> categories = shop.getCategories();
                        List<Offer> offers = shop.getOffers();
                        for (Category category : categories) {
                            for (Offer offer : offers) {
                                if (offer.getCategoryId() == category.getId() && offer.getPictureUrl() != null) {
                                    category.setPictureUrl(offer.getPictureUrl());
                                    break;
                                }
                            }
                        }
                        return Observable.just(categories);
                    })
                    .subscribe(
                            new Observer<List<Category>>() {
                                @Override
                                public void onCompleted() {
                                    emptyStateContainer.setVisibility(View.GONE);
                                    showRefresh(false);
                                    isLoading = false;
                                }

                                @Override
                                public void onError(Throwable e) {
                                    showRefresh(false);
                                    emptyStateContainer.setVisibility(View.VISIBLE);
                                    Log.e(LOG_TAG, "Error: " + e.getLocalizedMessage());
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG)
                                            .show();
                                }

                                @Override
                                public void onNext(List<Category> categories) {
                                    if (refresh) {
                                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Content downloaded", Snackbar.LENGTH_LONG)
                                                .show();
                                    }
                                    adapter.update(categories);
                                }
                            });
            Log.d(LOG_TAG, "Observer subscribed");
        }
    }

    private void showRefresh(boolean show) {
        swipeContainer.post(() -> swipeContainer.setRefreshing(show));
    }

    private void categorySelected(int categoryId) {
        if (getActivity() instanceof ViewContract) {
            ((ViewContract)getActivity()).showCategory(categoryId);
        }
    }
}
