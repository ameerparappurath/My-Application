package com.ameer.myapplication.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.ameer.myapplication.R;
import com.ameer.myapplication.adapters.MetricsRecyclerViewAdapter;
import com.ameer.myapplication.databinding.ActivityShowMetricsBinding;
import com.ameer.myapplication.models.weather_data.WeatherDatumUpdated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
/**
 * Created by Ameer Parappurath
 * offline metrics data class
 */

public class OfflineDisplayTemperatureActivity extends AppCompatActivity {
    private ActivityShowMetricsBinding binding;
    List<WeatherDatumUpdated> weatherDatumList;
    MetricsRecyclerViewAdapter metricsRecyclerViewAdapter;
    Activity mContext;
    List<WeatherDatumUpdated> weatherDatumListForSelectedYear;
    String locationName;
    ArrayList<Integer> yearArrayListForTIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_metrics);
        mContext = OfflineDisplayTemperatureActivity.this;
        weatherDatumList = new ArrayList<>();
        yearArrayListForTIL = new ArrayList<>();
        weatherDatumListForSelectedYear = new ArrayList<>();

        locationName = getIntent().getStringExtra("locationName");
        setTitle(locationName + " -(Metric Data)");
        populateData();
    }

    private void populateData() {
        try {
            JSONArray metricsJsonArray = new JSONArray(loadJSONFromAsset(locationName + ".json"));
            for (int i = 0; i < metricsJsonArray.length(); i++) {
                JSONObject metricsJsonObject = metricsJsonArray.getJSONObject(i);
                yearArrayListForTIL.add(metricsJsonObject.getInt("year"));
                weatherDatumList.add(new WeatherDatumUpdated(metricsJsonObject.getInt("year"), metricsJsonObject.getInt("monthInt"),
                        metricsJsonObject.getString("month"),
                        metricsJsonObject.getDouble("tMax"), metricsJsonObject.getDouble("tMin"), metricsJsonObject.getDouble("rainFall")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        metricsRecyclerViewAdapter = new MetricsRecyclerViewAdapter(weatherDatumList, this);
        binding.setMetricsRecyclerViewAdapter(metricsRecyclerViewAdapter);
        binding.rvShowMetrics.setLayoutManager(new GridLayoutManager(mContext, 2));
        HashSet<Integer> hashSet = new HashSet<>(yearArrayListForTIL);
        yearArrayListForTIL.clear();
        yearArrayListForTIL.addAll(hashSet);

        Comparator<Integer> comparator = Collections.reverseOrder();
        Collections.sort(yearArrayListForTIL, comparator);
        setUpAutoComepleteTextView();

    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setUpAutoComepleteTextView() {
        AutoCompleteTextView yearCompleteTextView = findViewById(R.id.et_filter_year);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>
                (OfflineDisplayTemperatureActivity.this, android.R.layout.select_dialog_item, yearArrayListForTIL);
        //Getting the instance of AutoCompleteTextView
        yearCompleteTextView.setThreshold(3);//will start working from third character
        yearCompleteTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        yearCompleteTextView.setOnClickListener(arg0 -> yearCompleteTextView.showDropDown());

        yearCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int index = -1;
                int value = 0;
                if (s.length() <= 3) {
                    metricsRecyclerViewAdapter.setData(weatherDatumList);
                } else {

                    for (int i = 0; i < yearArrayListForTIL.size(); i++) {
                        value = yearArrayListForTIL.get(i);
                        int yearFromEditText = Integer.parseInt(yearCompleteTextView.getText().toString());
                        if (value == yearFromEditText) {
                            index = i;
                            break;
                        }
                    }
                    if (index != -1) {
                        hideSoftKeyboard();
                        List<WeatherDatumUpdated> filteredArticleList;
                        int finalValue = value;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            filteredArticleList = new ArrayList<>();
                            filteredArticleList =
                                    weatherDatumList.stream().filter(weatherData -> weatherData.year == finalValue).collect(Collectors.toList());
                        } else {
                            filteredArticleList = new ArrayList<>();

                            for (WeatherDatumUpdated a : weatherDatumList) {
// or equalsIgnoreCase or whatever your conditon is
                                if (a.year == finalValue) {
// do something
                                    filteredArticleList.add(a);
                                }
                            }
                        }
                        metricsRecyclerViewAdapter.setData(filteredArticleList);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

}
