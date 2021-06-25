package com.eee.taxibooking.adapters;

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

    private Context mContext;
    private List<Taxi> mData;

    public TaxiAdapter(Context mContext, List<Taxi> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    public static class ViewAdapter extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView photo;
        TextView name;
        TextView number_1;
        TextView number_2;
        TextView freeCall;

        public ViewAdapter(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recycleView_item);
            photo = itemView.findViewById(R.id.taxiImage);
            name = itemView.findViewById(R.id.taxiTitleDesc);
//            number_1 = itemView.findViewById(R.id.call_one);
//            number_2 = itemView.findViewById(R.id.call_two);
//            freeCall = itemView.findViewById(R.id.freeCall);
        }

    }

    @NonNull
    @Override
    public ViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(R.layout.row, parent, false);

        final ViewAdapter viewAdapter = new ViewAdapter(view);

        return viewAdapter;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapter holder, int position) {
        Glide.with(mContext)
                .load(mData.get(position).getPhoto())
                .centerCrop()
                .into(holder.photo);
        holder.name.setText(mData.get(position).getName());
//        holder.number_1.setText(mData.get(position).getNumber1());
//        holder.number_2.setText(mData.get(position).getNumber2());
//        holder.freeCall.setText(mData.get(position).getNoCallPayment());

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


}
