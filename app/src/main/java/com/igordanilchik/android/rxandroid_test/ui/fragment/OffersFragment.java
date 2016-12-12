package com.igordanilchik.android.rxandroid_test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.model.Catalogue;
import com.igordanilchik.android.rxandroid_test.ui.ViewContract;
import com.igordanilchik.android.rxandroid_test.ui.activity.MainActivity;
import com.igordanilchik.android.rxandroid_test.ui.adapter.OffersAdapter;
import com.igordanilchik.android.rxandroid_test.utils.DividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class OffersFragment extends Fragment {

    private static final String LOG_TAG = OffersFragment.class.getSimpleName();

    @BindView(R.id.offers_recycler_view)
    RecyclerView recyclerView;

    OffersAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private Unbinder unbinder;

    @NonNull
    private Subscription subscription = Subscriptions.empty();

    private int categoryId;

    @NonNull
    public static OffersFragment newInstance(int categoryId) {
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_CATEGORY_ID, categoryId);

        OffersFragment f = new OffersFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_offers, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OffersAdapter();
        recyclerView.setAdapter(adapter);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        adapter.getPositionClicks().subscribe(offer -> {
            offerSelected(offer.getId());
        });

        requestData();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MainActivity.ARG_CATEGORY_ID)) {
            categoryId = bundle.getInt(MainActivity.ARG_CATEGORY_ID);
        }
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

    private void requestData() {
        subscription.unsubscribe();
        Log.d(LOG_TAG, "Observer unsubscribed");

        if (getActivity() instanceof ViewContract) {
            subscription = ((ViewContract) getActivity())
                    .getRepository()
                    .getCatalogue(false)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(Catalogue::getShop)
                    .filter(shop -> shop != null && shop.getOffers() != null)
                    .flatMap(shop -> Observable.just(shop.getOffers()))
                    .flatMap(Observable::from)
                    .filter(offer -> offer.getCategoryId() == categoryId)
                    .toList()
                    .subscribe(offers -> adapter.update(offers));

            Log.d(LOG_TAG, "Observer subscribed");
        }
    }

    private void offerSelected(int offerId) {
        if (getActivity() instanceof ViewContract) {
            ((ViewContract)getActivity()).showOffer(offerId);
        }
    }
}
