package com.im.yutalker.factory.data;

import android.support.annotation.StringRes;

/**
 * 数据源接口定义
 * Created by Phillip on 2018/1/17.
 */

public interface DataSource {

    /**
     * 同时包括成功和失败的接口
     * @param <T> 任意类型
     */
    interface CallBack<T> extends SuccessCallBack<T>, FailedCallBack {

    }

    /**
     * 成功的接口
     *
     * @param <T> 任意类型
     */
    interface SuccessCallBack<T> {
        // 数据加载成功，网络请求成功
        void onDataLoaded(T t);

    }

    /**
     * 失败的接口
     */
    interface FailedCallBack {
        // 数据加载失败，网络请求失败
        void onDataNotAvailable(@StringRes int strRes);

    }
}
