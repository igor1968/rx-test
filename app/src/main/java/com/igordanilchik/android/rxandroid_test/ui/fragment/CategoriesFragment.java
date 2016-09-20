package com.igordanilchik.android.rxandroid_test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.api.RestClient;
import com.igordanilchik.android.rxandroid_test.model.Catalogue;
import com.igordanilchik.android.rxandroid_test.model.Category;
import com.igordanilchik.android.rxandroid_test.model.Offer;
import com.igordanilchik.android.rxandroid_test.ui.CategoriesRetainModel;
import com.igordanilchik.android.rxandroid_test.ui.activity.MainActivity;
import com.igordanilchik.android.rxandroid_test.ui.adapter.CategoriesAdapter;
import com.igordanilchik.android.rxandroid_test.utils.DividerItemDecoration;
import com.igordanilchik.android.rxandroid_test.utils.FragmentUtils;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    @Nullable
    private Subscription subscription;
    private CategoriesRetainModel model;

    @NonNull
    public static CategoriesFragment newInstance() {
        CategoriesFragment f = new CategoriesFragment();
        return f;
    }

    @Override
    public void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        model = (CategoriesRetainModel) fm.findFragmentByTag(CategoriesRetainModel.class.getName());
        if (model == null) {
            model = new CategoriesRetainModel();
            fm.beginTransaction().add(model, CategoriesRetainModel.class.getName()).commit();
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_catalogue, container, false);
        unbinder = ButterKnife.bind(this, view);

        swipeContainer.setOnRefreshListener(this::subscribe);
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        if (model.observable == null) {
            model.observable = createCachedObservable();
            subscribe();
        } else if (model.isLoading) {
            subscribe();
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CategoriesAdapter(this.getContext(), model.categories);
        recyclerView.setAdapter(adapter);

        adapter.getPositionClicks().subscribe(category -> {
            categorySelected(category.getId());
        });

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unsubscribe();
        unbinder.unbind();
    }


    public void updateContent(@Nullable List<Category> categories) {
        if (categories != null) {
            model.categories.clear();
            model.categories.addAll(categories);
            adapter.notifyDataSetChanged();
        }
    }

    private void categorySelected(int categoryId) {
        Observable.just(model.content)
                .filter(shop -> shop != null && shop.getOffers() != null)
                .flatMap(shop -> Observable.just(shop.getOffers()))
                .flatMap(Observable::from)
                .filter(offer -> offer.getCategoryId() == categoryId)
                .toList()
                .subscribe(offers ->  {
                    Bundle args = new Bundle();
                    args.putParcelable(MainActivity.ARG_DATA, Parcels.wrap(offers));

                    OffersFragment fragment = OffersFragment.newInstance();
                    fragment.setArguments(args);
                    FragmentUtils.replaceFragment(getActivity(), R.id.frame_content, fragment, true);
                });

    }

    @NonNull
    private static Observable<Catalogue> observableFactory() {
        return new RestClient().loadCatalogue();
    }

    @NonNull
    private static Observable<Catalogue> createCachedObservable() {
        return observableFactory()
                .observeOn(AndroidSchedulers.mainThread());
                //.replay(1)
                //.autoConnect();
    }

    @NonNull
    private Observable<Catalogue> getOrCreateObservable() {
        Observable<Catalogue> observable = model.observable;
        if (observable == null) {
            observable = createCachedObservable();
            model.observable = observable;
            Log.d(LOG_TAG, "Observable created");
        }
        return observable;
    }

    private void subscribe() {
        if (subscription == null || subscription.isUnsubscribed()) {
            subscription = getOrCreateObservable()
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe(() -> {
                        showRefresh();
                        model.isLoading = true;
                    })
                    .map(Catalogue::getShop)
                    .filter(shop -> shop != null && shop.getOffers() != null && shop.getCategories() != null)
                    .flatMap(shop -> {
                        model.content = shop;
                        List<Category> categories = model.content.getCategories();
                        List<Offer> offers = model.content.getOffers();
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
                                    swipeContainer.setRefreshing(false);
                                    model.isLoading = false;
                                    unsubscribe();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    swipeContainer.setRefreshing(false);
                                    emptyStateContainer.setVisibility(View.VISIBLE);
                                    Log.e(LOG_TAG, "Error: " + e.getLocalizedMessage());
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Error: " + e.getMessage(), Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }

                                @Override
                                public void onNext(List<Category> categories) {
                                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Content downloaded", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    updateContent(categories);
                                }
                            });
            Log.d(LOG_TAG, "Observer subscribed");
        }
    }

    private void unsubscribe() {
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
        Log.d(LOG_TAG, "Observer unsubscribed");
    }

    private void showRefresh() {
        swipeContainer.post(() -> swipeContainer.setRefreshing(true));
    }
}
