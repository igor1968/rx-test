package com.igordanilchik.android.rxandroid_test.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.model.Offer;
import com.igordanilchik.android.rxandroid_test.ui.activity.MainActivity;
import com.igordanilchik.android.rxandroid_test.ui.adapter.OffersAdapter;
import com.igordanilchik.android.rxandroid_test.utils.DividerItemDecoration;
import com.igordanilchik.android.rxandroid_test.utils.FragmentUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OffersFragment extends Fragment {

    private static final String LOG_TAG = OffersFragment.class.getSimpleName();

    @BindView(R.id.offers_recycler_view)
    RecyclerView recyclerView;
    OffersAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private Unbinder unbinder;

    @NonNull
    private List<Offer> offers = new ArrayList<>();

    @NonNull
    public static OffersFragment newInstance() {
        OffersFragment f = new OffersFragment();
        return f;
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final @Nullable ViewGroup container, final @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_offers, container, false);
        this.unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        offers = Parcels.unwrap(bundle.getParcelable(MainActivity.ARG_DATA));

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OffersAdapter(this.getContext(), offers);
        recyclerView.setAdapter(adapter);

        adapter.getPositionClicks().subscribe(offer -> {
            Bundle args = new Bundle();
            args.putParcelable(MainActivity.ARG_DATA, Parcels.wrap(offer));

            OfferFragment fragment = OfferFragment.newInstance();
            fragment.setArguments(args);
            FragmentUtils.replaceFragment(getActivity(), R.id.frame_content, fragment, true);
        });

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
