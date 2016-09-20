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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    private static final String LOG_TAG = CategoriesAdapter.class.getSimpleName();
    @NonNull
    private List<Category> categories;
    @NonNull
    private Context context;

    @NonNull
    private final PublishSubject<Category> onClickSubject = PublishSubject.create();

    @NonNull
    public Observable<Category> getPositionClicks() {
        return onClickSubject.asObservable();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.category_title)
        TextView title;
        @BindView(R.id.category_image)
        ImageView icon;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public CategoriesAdapter(@NonNull Context ctx, @NonNull List<Category> myDataset) {
        categories = myDataset;
        context = ctx;
    }

    @Override
    public CategoriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> onClickSubject.onNext(categories.get(position)));

        holder.title.setText(categories.get(position).getTitle());
        String url = categories.get(position).getPictureUrl();
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
        return categories.size();
    }
}