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

import com.eee.taxibooking.R;
import com.eee.taxibooking.databases.Address;
import com.eee.taxibooking.databases.Database;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Context context;
    private List<Address> addressList;

    public AddressAdapter(Context context) {
        this.context = context;
    }

    public void setAddressList(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressAdapter.ViewHolder holder, int position) {

        final Address address = addressList.get(position);

        holder.name.setText(addressList.get(position).name);
        holder.address.setText(addressList.get(position).address);
        holder.city.setText(addressList.get(position).city);
        holder.delete.setOnClickListener(v -> {
            Database db  = Database.getDbInstance(context.getApplicationContext());

            db.addressDao().delete(address);
        });
    }


    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView city;
        CardView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            address = itemView.findViewById(R.id.tvAddress);
            city = itemView.findViewById(R.id.tvCity);
            delete = itemView.findViewById(R.id.deleteAddress);
        }
    }
}
