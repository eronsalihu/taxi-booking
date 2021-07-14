package com.eee.taxibooking.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eee.taxibooking.R;
import com.eee.taxibooking.models.Taxi;

import java.util.List;

public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.ViewAdapter> {

    private final Context mContext;
    private final List<Taxi> mData;
    private final ItemClick itemClick;

    public TaxiAdapter(Context mContext, List<Taxi> mData, ItemClick itemClick) {
        this.mContext = mContext;
        this.mData = mData;
        this.itemClick = itemClick;
    }

    public static class ViewAdapter extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView photo;
        TextView name;

        public ViewAdapter(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recycleView_item);
            photo = itemView.findViewById(R.id.taxiImage);
            name = itemView.findViewById(R.id.taxiTitleDesc);

        }

    }

    @NonNull
    @Override
    public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.row, parent, false);

        return new ViewAdapter(view);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onBindViewHolder(@NonNull ViewAdapter holder, int position) {
        Glide.with(mContext)
                .load(mData.get(position).getPhoto())
                .centerCrop()
                .into(holder.photo);
        holder.name.setText(mData.get(position).getName());
        holder.itemView.setOnClickListener(v -> itemClick.onItemClick(mData.get(position)));

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public interface ItemClick {
        void onItemClick(Taxi taxi);
    }

}
