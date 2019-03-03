package com.ameer.myapplication.retrofit;

/**
 * Created by Ameer Parappurath
 *
 */


public class ApiUtils {


    public static final String BASE_URL = "https://s3.eu-west-2.amazonaws.com/";


    public static RetroFitService getRetrofitService() {
        return RetrofitClient.getClient(BASE_URL).create(RetroFitService.class);
    }


}
