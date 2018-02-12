package com.im.yutalker.factory.data;

import android.support.annotation.NonNull;

import com.im.yutalker.factory.data.helper.DbHelper;
import com.im.yutalker.factory.model.dp.BaseDbModel;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.utils.CollectionUtil;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import net.qiujuer.genius.kit.reflect.Reflector;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础的数据库仓库
 * 实现对数据库的基本的监听操作
 * Created by Phillip on 2018/2/6.
 */

public abstract class BaseDbRepository<Data extends BaseDbModel<Data>> implements DbDataSource<Data>
        , DbHelper.ChangedListener<Data>
        , QueryTransaction.QueryResultListCallback<Data> {
    // 和presenter交互的回调
    private SuccessCallBack<List<Data>> callback;
    // 缓存起来
    private final List<Data> dataList = new LinkedList<>();
    // 当前泛型对应的真实的class信息
    private Class<Data> dataClass;

    @SuppressWarnings("unchecked")
    public BaseDbRepository() {
        // 拿当前类的泛型数组信息
        Type[] types = Reflector.getActualTypeArguments(BaseDbRepository.class, this.getClass());
        dataClass = (Class<Data>) types[0];
    }

    @Override
    public void load(SuccessCallBack<List<Data>> callback) {
        this.callback = callback;
        // 进行数据库监听
        registerDbChangedListener();
    }

    @Override
    public void dispose() {
        // 取消监听，销毁数据
        this.callback = null;
        DbHelper.removeChangedListener(dataClass, this);
        dataList.clear();
    }

    // 数据库统一通知的操作：增加/修改
    @Override
    public void onDataSave(Data[] list) {
        // 是否需要通知变更
        boolean isChanged = false;
        // 当数据库保存的操作
        for (Data data : list) {
            // 是关注的人，同时不是自己
            if (isRequired(data)) {
                insertOrUpdate(data);
                isChanged = true;
            }
        }
        if (isChanged)
            // 通知变更
            notifyDataChange();
    }

    // 数据库统一通知的操作：删除
    @Override
    public void onDataDelete(Data[] list) {
        // 是否需要通知变更
        boolean isChanged = false;
        // 当数据库删除的操作
        for (Data data : list) {
            if (dataList.remove(data)) {
                isChanged = true;
            }
        }
        if (isChanged)
            // 通知变更
            notifyDataChange();
    }

    // DbFlow 框架通知的回调
    @Override
    public void onListQueryResult(QueryTransaction transaction, @NonNull List<Data> tResult) {
        // 数据库加载数据成功
        if (tResult.size() == 0) {
            dataList.clear();
            notifyDataChange();
            return;
        }
        Data[] users = CollectionUtil.toArray(tResult, dataClass);
        // 回到数据集更新的操作
        onDataSave(users);
    }

    /**
     * 插入或者替换
     *
     * @param data 泛型
     */
    private void insertOrUpdate(Data data) {
        int index = indexOf(data);
        if (index >= 0) {
            replace(index, data);
        } else {
            insert(data);
        }
    }

    /**
     * 替换方法
     *
     * @param index 集合下标
     * @param data  泛型
     */
    protected void replace(int index, Data data) {
        dataList.remove(index);
        dataList.add(index, data);
    }

    /**
     * 添加的方法
     *
     * @param data 泛型
     */
    protected void insert(Data data) {
        dataList.add(data);
    }

    /**
     * 获取下标
     *
     * @param newData 泛型
     * @return 下标
     */
    protected int indexOf(Data newData) {
        int index = -1;
        for (Data oldData : dataList) {
            index++;
            if (oldData.isSame(newData)) {
                return index;
            }
        }
        return -1;
    }

    /**
     * 检查一个User是否是我需要关注的数据
     *
     * @param data 泛型
     * @return True表示是需要关注的
     */
    protected abstract boolean isRequired(Data data);

    /**
     * 添加数据库的监听操作
     */
    protected void registerDbChangedListener() {
        DbHelper.addChangedListener(dataClass, this);
    }

    /**
     * 通知有数据变更
     * 通知界面刷新的方法
     */
    private void notifyDataChange() {
        SuccessCallBack<List<Data>> callback = this.callback;
        if (callback != null) {
            callback.onDataLoaded(dataList);
        }
    }
}
