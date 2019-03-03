package com.ameer.myapplication.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ameer.myapplication.R;
import com.ameer.myapplication.databinding.ItemMetricsRowBinding;
import com.ameer.myapplication.models.weather_data.WeatherDatumUpdated;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.RecyclerView;


public class MetricsRecyclerViewAdapter extends RecyclerView.Adapter<MetricsRecyclerViewAdapter.ViewHolder> {

    private List<WeatherDatumUpdated> dataModelList;
    private Context context;

    public MetricsRecyclerViewAdapter(List<WeatherDatumUpdated> dataModelList, Context ctx) {
        this.dataModelList = dataModelList;
        context = ctx;
    }

    @NonNull
    @Override
    public MetricsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {
        ItemMetricsRowBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_metrics_row, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherDatumUpdated dataModel = dataModelList.get(position);
        holder.bind(dataModel);
    }

    public void setData(List<WeatherDatumUpdated> data) {

        this.dataModelList=new ArrayList<>();
        this.dataModelList.addAll(data);
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return dataModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemMetricsRowBinding itemMetricsBinding;

        ViewHolder(ItemMetricsRowBinding itemRowBinding) {
            super(itemRowBinding.getRoot());
            this.itemMetricsBinding = itemRowBinding;
        }

        public void bind(Object obj) {
            itemMetricsBinding.setVariable(BR.model, obj);
            itemMetricsBinding.executePendingBindings();
        }
    }

}
