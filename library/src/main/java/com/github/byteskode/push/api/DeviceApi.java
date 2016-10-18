package com.github.byteskode.push.api;

import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author lally elias
 * @email lallyelias87@gmail.com, lally.elias@byteskode.com
 * @date 10/17/16
 */
public interface DeviceApi {
    //TODO handle token auth

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
    })
    @POST("devices")
    Call<Device> create(@Header("Authorization") String authorization, @Body Device device);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json",
    })
    @PUT("devices")
    Call<Device> update(@Header("Authorization") String authorization, @Body Device device);

}
