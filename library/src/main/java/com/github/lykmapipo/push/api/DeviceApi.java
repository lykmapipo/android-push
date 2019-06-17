package com.github.lykmapipo.push.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * @author lally elias <lallyelias87@gmail.com>
 * @date 10/17/16
 */
public interface DeviceApi {
    //TODO handle token auth

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @POST("devices")
    Call<Device> create(@Body Device device);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"
    })
    @PUT("devices")
    Call<Device> update(@Body Device device);

}
