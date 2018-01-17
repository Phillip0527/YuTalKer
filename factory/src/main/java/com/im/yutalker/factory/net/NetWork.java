package com.im.yutalker.factory.net;

import com.im.yutalker.common.Common;
import com.im.yutalker.factory.Factory;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit网络请求的封装
 * Created by Phillip on 2018/1/17.
 */

public class NetWork {

    /**
     * 构建一个Retrofit
     *
     * @return
     */
    public static Retrofit getRetrofit() {
        // 得到一个OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit.Builder builder = new Retrofit.Builder();
        // 设置电脑链接
        return builder.baseUrl(Common.Constance.API_URL)
                // 设置client
                .client(client)
                // 设置json解析器
                .addConverterFactory(GsonConverterFactory.create(Factory.getGson()))
                .build();
    }
}
