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

    @POST("devices")
    Call<Device> create(@Body Device device);

    @PUT("devices")
    Call<Device> update(@Body Device device);

}
