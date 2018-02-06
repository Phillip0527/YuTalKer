package com.im.yutalker.factory.data;

import java.util.List;

/**
 * 基础的数据库数据源定义的接口
 * Created by Phillip on 2018/2/6.
 */

public interface DbDataSource<Data> extends DataSource {
    /**
     * 一个基本的数据源加载方法
     *
     * @param callback 传递一个callback回调，一般回调到presenter中
     */
    void load(SuccessCallBack<List<Data>> callback);

}
