package com.ameer.myapplication.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.ameer.myapplication.R;
import com.ameer.myapplication.adapters.MetricsRecyclerViewAdapter;
import com.ameer.myapplication.databinding.ActivityShowMetricsBinding;
import com.ameer.myapplication.models.weather_data.WeatherDatum;
import com.ameer.myapplication.models.weather_data.WeatherDatumUpdated;
import com.ameer.myapplication.retrofit.ApiUtils;
import com.ameer.myapplication.retrofit.RetroFitService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
/**
 * Created by Ameer Parappurath
 * this class will display the metrics. Here3 apis are called and results are combined.
 */

public class DisplayTemperatureActivity extends AppCompatActivity {
    List<WeatherDatumUpdated> weatherDatumList;
    MetricsRecyclerViewAdapter metricsRecyclerViewAdapter;
    Activity mContext;
    int whichAPICalled = 0;
    String locationName;
    private ProgressDialog dialog;
    ArrayList<Integer> yearArrayListForTIL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityShowMetricsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_show_metrics);
        mContext = DisplayTemperatureActivity.this;
        weatherDatumList = new ArrayList<>();
        yearArrayListForTIL = new ArrayList<>();
        locationName = getIntent().getStringExtra("locationName");
        setTitle(locationName + " -(Metric Data)");

        binding.setMetricsRecyclerViewAdapter(metricsRecyclerViewAdapter);
        metricsRecyclerViewAdapter = new MetricsRecyclerViewAdapter(weatherDatumList, this);
        binding.setMetricsRecyclerViewAdapter(metricsRecyclerViewAdapter);
        binding.rvShowMetrics.setLayoutManager(new GridLayoutManager(mContext, 2));
        populateData();
    }
// here apis are called using RXJava and retrofitt.
// merge function is used
    private void populateData() {


        RetroFitService mService = ApiUtils.getRetrofitService();


//There are 3 metrics: Tmax (max temperature), Tmin (min temperature) and Rainfall (mm), and 4 locations: UK, England, Scotland, Wales.
        //whichMetrics values,  1- Tmax, 2-Tmin, 3- Rainfall

        dialog = new ProgressDialog(DisplayTemperatureActivity.this);
        dialog.setMessage("Please wait...");
        dialog.show();

        Observable<List<WeatherDatum>> tMaxObservable = mService.getWeatherData("Tmax", locationName)
                .map(Observable::fromIterable)
                .flatMap(x -> x).filter(y -> true).toList().toObservable();

        Observable<List<WeatherDatum>> tMinObservable = mService.getWeatherData("Tmin", locationName)
                .map(Observable::fromIterable)
                .flatMap(x -> x).filter(y -> true).toList().toObservable();

        Observable<List<WeatherDatum>> rainFallObservable = mService.getWeatherData("Rainfall", locationName)
                .map(Observable::fromIterable)
                .flatMap(x -> x).filter(y -> true).toList().toObservable();


        Observable.merge(tMaxObservable, tMinObservable, rainFallObservable)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError);

    }
// results from each API
    private void handleResults(List<WeatherDatum> weatherDatumListIndiv) {


        if (weatherDatumListIndiv != null && weatherDatumListIndiv.size() != 0) {

            for (int i = 0; i < weatherDatumListIndiv.size(); i++) {
                if (whichAPICalled == 0) {
                    // for the first API all the values are added to arraylist
                    // month name added from number using getMonthValueFromString method
                    yearArrayListForTIL.add(weatherDatumListIndiv.get(i).getYear());
                    weatherDatumList.add(new WeatherDatumUpdated(weatherDatumListIndiv.get(i).getYear(),weatherDatumListIndiv.get(i).getMonth(),
                            getMonthValueFromString(weatherDatumListIndiv.get(i).getMonth()),
                            weatherDatumListIndiv.get(i).getValue(), 0.0, 0.0));

                } else if (whichAPICalled == 1) {
                    // tmin updating after 2nd api call
                    weatherDatumList.get(i).settMin(weatherDatumListIndiv.get(i).getValue());
                } else if (whichAPICalled == 2) {
                    // RainFall updating after 2nd api call
                    weatherDatumList.get(i).setRainFall(weatherDatumListIndiv.get(i).getValue());
                }
            }
            if (whichAPICalled == 2) {
                // sorting array to descending. with year and month
                Collections.sort(weatherDatumList, new Comparator<WeatherDatumUpdated>() {
                    @Override
                    public int compare(WeatherDatumUpdated lhs, WeatherDatumUpdated rhs) {
                        int c;
                        c = rhs.year.compareTo(lhs.year);
                        if (c == 0)
                            c = rhs.monthInt.compareTo(lhs.monthInt);
                        return c;
                        //    return rhs.year.compareTo(lhs.year);
                    }
                });
// remove all duplicates from year array and sorting descending order
                HashSet<Integer> hashSet = new HashSet<>(yearArrayListForTIL);
                yearArrayListForTIL.clear();
                yearArrayListForTIL.addAll(hashSet);
                Comparator<Integer> comparator=Collections.reverseOrder();
                Collections.sort(yearArrayListForTIL,comparator);
                setUpAutoComepleteTextView();
                metricsRecyclerViewAdapter.setData(weatherDatumList);
                dialog.dismiss();
            }
            whichAPICalled++;

        } else {
            dialog.dismiss();
            Toast.makeText(this, "NO RESULTS FOUND",
                    Toast.LENGTH_LONG).show();
        }
    }
// auto complete textview setup
    private void setUpAutoComepleteTextView() {
        AutoCompleteTextView yearCompleteTextView = findViewById(R.id.et_filter_year);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>
                (DisplayTemperatureActivity.this, android.R.layout.select_dialog_item, yearArrayListForTIL);
        //Getting the instance of AutoCompleteTextView
        yearCompleteTextView.setThreshold(3);//will start working from third character
        yearCompleteTextView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        yearCompleteTextView.setOnClickListener(arg0 -> yearCompleteTextView.showDropDown());

        yearCompleteTextView.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int index = -1;
                int value = 0;
                // if one value cleared arraylist will reset.
                if(s.length()<=3)
                {
                metricsRecyclerViewAdapter.setData(weatherDatumList);
                }else
                {

                for (int i = 0; i < yearArrayListForTIL.size(); i++) {
                    value  = yearArrayListForTIL.get(i);
                    int yearFromEditText = Integer.parseInt(yearCompleteTextView.getText().toString());
                    if (value==yearFromEditText) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    hideSoftKeyboard();
                    List<WeatherDatumUpdated> filteredArticleList;
                    int finalValue = value;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        filteredArticleList=new ArrayList<>();
                       filteredArticleList=
                                weatherDatumList.stream().filter(weatherData -> weatherData.year== finalValue).collect(Collectors.toList());
                    }else
                    {
                        filteredArticleList=new ArrayList<>();

                        for( WeatherDatumUpdated a : weatherDatumList) {
// or equalsIgnoreCase or whatever your conditon is
                            if (a.year==finalValue) {
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
// error response method
    private void handleError(Throwable t) {
        dialog.dismiss();
        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again",
                Toast.LENGTH_LONG).show();
    }

    // hide keyboard on selection
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
    // get month value from month id
    private String getMonthValueFromString(int monthValueInt) {
        String monthValue = null;
        switch (monthValueInt) {
            case 1:
                monthValue = "Jan";
                break;
            case 2:
                monthValue = "Feb";
                break;
            case 3:
                monthValue = "Mar";
                break;
            case 4:
                monthValue = "Apr";
                break;
            case 5:
                monthValue = "May";
                break;
            case 6:
                monthValue = "Jun";
                break;
            case 7:
                monthValue = "Jul";
                break;
            case 8:
                monthValue = "Aug";
                break;
            case 9:
                monthValue = "Sep";
                break;
            case 10:
                monthValue = "Oct";
                break;
            case 11:
                monthValue = "Nov";
                break;
            case 12:
                monthValue = "Dec";
                break;
        }
        return monthValue;
    }

}
