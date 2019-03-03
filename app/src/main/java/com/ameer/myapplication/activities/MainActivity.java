package com.ameer.myapplication.activities;

import android.os.Bundle;

import com.ameer.myapplication.R;
import com.ameer.myapplication.adapters.LocationRecyclerViewAdapter;
import com.ameer.myapplication.databinding.ActivityMainBinding;
import com.ameer.myapplication.models.LocationDataModel;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setTitle("Select your location");
        populateData();
    }

    private void populateData() {
        List<LocationDataModel> dataModelList = new ArrayList<>();
//4 locations: UK, England, Scotland, Wales
        dataModelList.add(new LocationDataModel("UK", "1"));
        dataModelList.add(new LocationDataModel("England", "2"));
        dataModelList.add(new LocationDataModel("Scotland", "3"));
        dataModelList.add(new LocationDataModel("Wales", "4"));

        LocationRecyclerViewAdapter locationRecyclerViewAdapter = new LocationRecyclerViewAdapter(dataModelList, this);
        binding.setLocationRecyclerViewAdapter(locationRecyclerViewAdapter);
    }

}
