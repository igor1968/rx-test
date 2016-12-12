package com.igordanilchik.android.rxandroid_test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.model.Catalogue;
import com.igordanilchik.android.rxandroid_test.model.Offer;
import com.igordanilchik.android.rxandroid_test.ui.ViewContract;
import com.igordanilchik.android.rxandroid_test.ui.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class OfferFragment extends Fragment {
    private static final String LOG_TAG = OfferFragment.class.getSimpleName();

    @BindView(R.id.card_image)
    ImageView image;
    @BindView(R.id.card_title)
    TextView title;
    @BindView(R.id.card_price)
    TextView price;
    @BindView(R.id.card_weight)
    TextView weight;
    @BindView(R.id.card_description)
    TextView description;

    private Unbinder unbinder;

    @NonNull
    Subscription subscription = Subscriptions.empty();

    private int offerId;

    @NonNull
    public static OfferFragment newInstance(int offerId) {
        Bundle args = new Bundle();
        args.putInt(MainActivity.ARG_OFFER_ID, offerId);

        OfferFragment f = new OfferFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(MainActivity.ARG_OFFER_ID)) {
            offerId = bundle.getInt(MainActivity.ARG_OFFER_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_offer, container, false);
        this.unbinder = ButterKnife.bind(this, view);

        requestData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        subscription.unsubscribe();
        Log.d(LOG_TAG, "Observer unsubscribed");
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
                    .filter(offer -> offer.getId() == offerId)
                    .subscribe(this::updateContent);

            Log.d(LOG_TAG, "Observer subscribed");
        }
    }

    private void updateContent(@Nullable Offer offer) {
        if (offer != null) {
            title.setText(offer.getName());
            price.setText(getString(R.string.offer_price, offer.getPrice()));

            if (offer.getParam() != null) {
                String weightStr = offer.getParam().get(this.getString(R.string.param_name_weight));
                if (weightStr != null) {
                    weight.setText(getString(R.string.offer_weight, weightStr));
                }
            }

            String url = offer.getPictureUrl();
            if (url != null && !url.isEmpty()) {
                Glide.with(this)
                        .load(url)
                        .fitCenter()
                        .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.ic_image_black_24dp))
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(image);
            } else {
                image.setVisibility(View.GONE);
            }

            String descriptionText = offer.getDescription();
            description.setText(descriptionText);
        }
    }
}
