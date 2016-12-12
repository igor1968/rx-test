package com.igordanilchik.android.rxandroid_test.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.igordanilchik.android.rxandroid_test.R;
import com.igordanilchik.android.rxandroid_test.model.Offer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {

    private static final String LOG_TAG = CategoriesAdapter.class.getSimpleName();
    @NonNull
    private List<Offer> dataset = new ArrayList<>();
    @NonNull
    private final PublishSubject<Offer> onClickSubject = PublishSubject.create();
    @NonNull
    public Observable<Offer> getPositionClicks() {
        return onClickSubject.asObservable();
    }

    @Override
    public OffersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // If a context is needed, it can be retrieved
        // from the ViewHolder's root view.
        Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(dataset.get(position)));

        holder.name.setText(dataset.get(position).getName());
        holder.price.setText(context.getString(R.string.offer_price, dataset.get(position).getPrice()));

        if (dataset.get(position).getParam() != null) {
            String weight = dataset.get(position).getParam().get(context.getString(R.string.param_name_weight));
            if (weight != null) {
                holder.weight.setText(context.getString(R.string.offer_weight, weight));
            }
        }

        String url = dataset.get(position).getPictureUrl();
        Glide.with(context)
                .load(url)
                .fitCenter()
                .centerCrop()
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_black_24dp))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void update(@NonNull List<Offer> offers) {
        dataset.clear();
        dataset.addAll(offers);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.offer_name)
        TextView name;
        @BindView(R.id.offer_image)
        ImageView image;
        @BindView(R.id.offer_weight)
        TextView weight;
        @BindView(R.id.offer_price)
        TextView price;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
