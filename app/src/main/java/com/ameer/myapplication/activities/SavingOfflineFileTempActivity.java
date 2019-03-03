package com.ameer.myapplication.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ameer.myapplication.R;
import com.ameer.myapplication.adapters.MetricsRecyclerViewAdapter;
import com.ameer.myapplication.databinding.ActivityShowMetricsBinding;
import com.ameer.myapplication.models.weather_data.WeatherDatum;
import com.ameer.myapplication.models.weather_data.WeatherDatumUpdated;
import com.ameer.myapplication.retrofit.ApiUtils;
import com.ameer.myapplication.retrofit.RetroFitService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
// this is justa temporary class. For creating the json file.
//I have saved the json file programitically to my local memory and I copied those files to asset folder
// this class not using

public class SavingOfflineFileTempActivity extends AppCompatActivity {
    private ActivityShowMetricsBinding binding;
    List<WeatherDatumUpdated> weatherDatumList;
    MetricsRecyclerViewAdapter metricsRecyclerViewAdapter;
    Activity mContext;
    int whichAPICalled = 0;
    String locationName;
    private transient JSONArray jsonArray=new JSONArray();
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_metrics);
        mContext = SavingOfflineFileTempActivity.this;
        weatherDatumList = new ArrayList<>();
        locationName=getIntent().getStringExtra("locationName");

        binding.setMetricsRecyclerViewAdapter(metricsRecyclerViewAdapter);
        metricsRecyclerViewAdapter = new MetricsRecyclerViewAdapter(weatherDatumList, this);
        binding.setMetricsRecyclerViewAdapter(metricsRecyclerViewAdapter);
        binding.rvShowMetrics.setLayoutManager(new GridLayoutManager(mContext, 2));
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                populateData();
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            populateData();
            // Code for Below 23 API Oriented Device
        }

    }

    private void populateData() {


        RetroFitService mService = ApiUtils.getRetrofitService();

        dialog = new ProgressDialog(SavingOfflineFileTempActivity.this);
        dialog.setMessage("Saving file. Please wait...");
        dialog.show();

//There are 3 metrics: Tmax (max temperature), Tmin (min temperature) and Rainfall (mm), and 4 locations: UK, England, Scotland, Wales.
        //whichMetrics values,  1- Tmax, 2-Tmin, 3- Rainfall
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

    private void handleResults(List<WeatherDatum> weatherDatumListIndiv) {


        if (weatherDatumListIndiv != null && weatherDatumListIndiv.size() != 0) {


            for (int i = 0; i < weatherDatumListIndiv.size(); i++) {
                if (whichAPICalled == 0) {
                    weatherDatumList.add(new WeatherDatumUpdated(weatherDatumListIndiv.get(i).getYear(),
                            weatherDatumListIndiv.get(i).getMonth(),   getMonthValueFromString(weatherDatumListIndiv.get(i).getMonth()),
                            weatherDatumListIndiv.get(i).getValue(), 0.0, 0.0));

                } else if (whichAPICalled == 1) {
                    weatherDatumList.get(i).settMin(weatherDatumListIndiv.get(i).getValue());
                } else if (whichAPICalled == 2) {
                    // saveArrayList();
                    weatherDatumList.get(i).setRainFall(weatherDatumListIndiv.get(i).getValue());
                }
            }

            if(whichAPICalled==2)
            {
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
                saveValues();
                dialog.dismiss();            Toast.makeText(this, "File Saved",
                    Toast.LENGTH_LONG).show();

                SavingOfflineFileTempActivity.this.finish();
            }

//            metricsRecyclerViewAdapter.setData(weatherDatumList);
            whichAPICalled++;

        } else {
            dialog.dismiss();
            SavingOfflineFileTempActivity.this.finish();
            Toast.makeText(this, "NO RESULTS FOUND",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void handleError(Throwable t) {

        Toast.makeText(this, "ERROR IN FETCHING API RESPONSE. Try again",
                Toast.LENGTH_LONG).show();
        dialog.dismiss();
        SavingOfflineFileTempActivity.this.finish();
    }

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

    private void saveValues() {
        try {


            for (int i = 0; i <weatherDatumList.size() ; i++) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put("year",weatherDatumList.get(i).year);
                jsonObject.put("monthInt",weatherDatumList.get(i).monthInt);
                jsonObject.put("month",weatherDatumList.get(i).month);
                jsonObject.put("tMax",weatherDatumList.get(i).tMax);
                jsonObject.put("tMin",weatherDatumList.get(i).tMin);
                jsonObject.put("rainFall",weatherDatumList.get(i).rainFall);
                jsonArray.put(jsonObject);
            }
            Log.e("jsonArray","="+jsonArray);
            try {
                File file = new File(Environment.getExternalStorageDirectory(), locationName+".json");
                if(!file.exists())
                    file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.append(jsonArray+"");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(SavingOfflineFileTempActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            populateData();
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(SavingOfflineFileTempActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(SavingOfflineFileTempActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(SavingOfflineFileTempActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    populateData();
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
    }