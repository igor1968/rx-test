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
import com.igordanilchik.android.rxandroid_test.model.Category;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private static final String LOG_TAG = CategoriesAdapter.class.getSimpleName();
    @NonNull
    private List<Category> dataset = new ArrayList<>();
    @NonNull
    private final PublishSubject<Category> onClickSubject = PublishSubject.create();
    @NonNull
    public Observable<Category> getPositionClicks() {
        return onClickSubject.asObservable();
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // If a context is needed, it can be retrieved
        // from the ViewHolder's root view.
        Context context = holder.itemView.getContext();

        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(dataset.get(position)));

        holder.title.setText(dataset.get(position).getTitle());
        String url = dataset.get(position).getPictureUrl();
        Glide.with(context)
                .load(url)
                .fitCenter()
                .centerCrop()
                .placeholder(ContextCompat.getDrawable(context, R.drawable.ic_image_black_24dp))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void update(@NonNull List<Category> categories) {
        dataset.clear();
        dataset.addAll(categories);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.category_title)
        TextView title;
        @BindView(R.id.category_image)
        ImageView icon;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}