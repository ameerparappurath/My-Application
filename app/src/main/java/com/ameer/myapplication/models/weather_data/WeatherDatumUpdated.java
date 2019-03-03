package com.ameer.myapplication.models.weather_data;

public class WeatherDatumUpdated {

    public Integer year;
    public Integer monthInt;
    public String month;
    public Double tMin;
    public Double rainFall;
    public Double tMax;


    public WeatherDatumUpdated( Integer year,Integer monthInt, String month,
                               Double tMax, Double tMin, Double rainFall) {
        this.year = year;
        this.monthInt = monthInt;
        this.month = month;
        this.tMin = tMin;
        this.rainFall = rainFall;
        this.tMax = tMax;
    }

    public void settMin(Double tMin) {
        this.tMin = tMin;
    }
    public void setRainFall(Double rainFall) {
        this.rainFall = rainFall;
    }
}