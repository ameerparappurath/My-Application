package com.ameer.myapplication.retrofit;


import com.ameer.myapplication.models.weather_data.WeatherDatum;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface RetroFitService {


    @GET("interview-question-data/metoffice/{metrics}-{location}.json")
    Observable<List<WeatherDatum>> getWeatherData(@Path("metrics") String metrics, @Path("location") String location);
}