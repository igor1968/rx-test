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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {

    private static final String LOG_TAG = CategoriesAdapter.class.getSimpleName();
    @NonNull
    private List<Offer> offers;
    @NonNull
    private Context context;

    @NonNull
    private final PublishSubject<Offer> onClickSubject = PublishSubject.create();

    @NonNull
    public Observable<Offer> getPositionClicks() {
        return onClickSubject.asObservable();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.offer_name)
        TextView name;
        @BindView(R.id.offer_image)
        ImageView image;
        @BindView(R.id.offer_weight)
        TextView weight;
        @BindView(R.id.offer_price)
        TextView price;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public OffersAdapter(@NonNull Context ctx, @NonNull List<Offer> myDataset) {
        offers = myDataset;
        context = ctx;
    }

    @Override
    public OffersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(offers.get(position)));

        holder.name.setText(offers.get(position).getName());
        holder.price.setText(context.getString(R.string.offer_price, offers.get(position).getPrice()));

        if (offers.get(position).getParam() != null) {
            String weight = offers.get(position).getParam().get("Вес");
            if (weight != null) {
                holder.weight.setText(context.getString(R.string.offer_weight, weight));
            }
        }

        String url = offers.get(position).getPictureUrl();
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
        return offers.size();
    }
}
