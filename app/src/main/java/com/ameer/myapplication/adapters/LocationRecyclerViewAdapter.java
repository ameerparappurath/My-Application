package com.ameer.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ameer.myapplication.R;
import com.ameer.myapplication.activities.DisplayTemperatureActivity;
import com.ameer.myapplication.activities.OfflineDisplayTemperatureActivity;
import com.ameer.myapplication.databinding.LocationItemRowBinding;
import com.ameer.myapplication.interfaces.CustomClickListenerForLocationInterface;
import com.ameer.myapplication.models.LocationDataModel;
import com.ameer.myapplication.utils.ConnectionDetector;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;


public class LocationRecyclerViewAdapter extends RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder> implements CustomClickListenerForLocationInterface {

    private List<LocationDataModel> dataModelList;
    private Context context;

    public LocationRecyclerViewAdapter(List<LocationDataModel> dataModelList, Context ctx) {
        this.dataModelList = dataModelList;
        context = ctx;
    }

    @NonNull
    @Override
    public LocationRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType) {
        LocationItemRowBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.location_item_row, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationDataModel dataModel = dataModelList.get(position);
        holder.bind(dataModel);
        // holder.locationItemRowBinding.setModel(dataModel);

        holder.locationItemRowBinding.setItemClickListener(this);
    }


    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LocationItemRowBinding locationItemRowBinding;

        ViewHolder(LocationItemRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.locationItemRowBinding = itemRowBinding;
        }

        void bind(Object obj) {
            locationItemRowBinding.setVariable(BR.model, obj);
            locationItemRowBinding.executePendingBindings();
        }
    }

    public void cardClicked(LocationDataModel locationDataModel) {
        ConnectionDetector _connectionDetector = new ConnectionDetector(context);
        Intent intent;
        if (_connectionDetector.isConnectingToInternet()) {
            intent = new Intent(context, DisplayTemperatureActivity.class);
        } else {
            intent = new Intent(context, OfflineDisplayTemperatureActivity.class);
        }

        intent.putExtra("locationName", locationDataModel.getLocationName());

        context.startActivity(intent);

    }
}
