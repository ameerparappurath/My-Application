package com.ameer.myapplication.models.weather_data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WeatherDatum implements Parcelable {

    @SerializedName("value")
    @Expose
    private Double value;
    @SerializedName("year")
    @Expose
    private Integer year;
    @SerializedName("month")
    @Expose
    private Integer month;

    public final static Parcelable.Creator<WeatherDatum> CREATOR = new Creator<WeatherDatum>() {


        @SuppressWarnings({
                "unchecked"
        })
        public WeatherDatum createFromParcel(Parcel in) {
            return new WeatherDatum(in);
        }

        public WeatherDatum[] newArray(int size) {
            return (new WeatherDatum[size]);
        }

    };

    protected WeatherDatum(Parcel in) {
        this.value = ((Double) in.readValue((Double.class.getClassLoader())));
        this.year = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.month = ((Integer) in.readValue((Integer.class.getClassLoader())));
    }

    public WeatherDatum() {
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }


    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(value);
        dest.writeValue(year);
        dest.writeValue(month);
    }

    public int describeContents() {
        return 0;
    }

}