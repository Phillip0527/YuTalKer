package com.im.yutalker.factory.model.dp;

import com.im.yutalker.factory.utils.DiffUiDataCallback;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * app中基础的一个BaseDbModel,继承了数据库框架DBFlow中的基础类
 * 同时定义了我们需要的方法
 * Created by Phillip on 2018/2/6.
 */

public abstract class BaseDbModel<Model> extends BaseModel
        implements DiffUiDataCallback.UiDataDiffer<Model> {
}
