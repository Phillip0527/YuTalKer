package com.im.yutalker.factory.utils;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * 数据对比工具类
 * Created by Phillip on 2018/1/30.
 */

public class DiffUiDataCallback<T extends DiffUiDataCallback.UiDataDiffer<T>> extends DiffUtil.Callback {
    private List<T> mOldList, mNewList;

    public DiffUiDataCallback(List<T> mOldList, List<T> mNewList) {
        this.mOldList = mOldList;
        this.mNewList = mNewList;
    }

    @Override
    public int getOldListSize() {
        // 旧的数据大小
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        // 新的数据大小
        return mNewList.size();
    }

    // 两个类是否是同一个东西，比如id相等的user
    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldList.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);
        return beanNew.isSame(beanOld);
    }

    // 在经过相等判断后进一步判断是否有数据更改
    // 比如 同一个用户的，两个不同实例，其中name字段不同
    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        T beanOld = mOldList.get(oldItemPosition);
        T beanNew = mNewList.get(newItemPosition);
        return beanNew.isUiContentSame(beanOld);
    }

    // 进行比较的数据类型
    // 目的就是两个一样数据类型的数据进行比较
    public interface UiDataDiffer<T> {
        // 传递一个旧数据，是否和你表示的是同一个数据
        boolean isSame(T old);

        // 和旧的数据对比，内容是否相同
        boolean isUiContentSame(T old);

    }
}
