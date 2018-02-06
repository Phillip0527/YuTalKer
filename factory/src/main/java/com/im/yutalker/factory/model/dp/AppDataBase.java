package com.im.yutalker.factory.model.dp;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * 数据库的基本信息
 * Created by Phillip on 2018/1/19.
 */

@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {
    public static final String NAME = "AppDataBase";
    public static final int VERSION = 2;
}
