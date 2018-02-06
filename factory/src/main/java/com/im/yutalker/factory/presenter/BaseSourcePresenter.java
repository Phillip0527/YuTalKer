package com.im.yutalker.factory.presenter;

import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.DbDataSource;

import java.util.List;

/**
 * 基础的仓库源的Presenter
 * Created by Phillip on 2018/2/6.
 */

public abstract class BaseSourcePresenter<Data, ViewModel, Source extends DbDataSource<Data>, View extends BaseContract.RecyclerView>
        extends BaseRecyclerPresenter<ViewModel, View>
        implements DataSource.SuccessCallBack<List<Data>> {
    protected Source mSource;

    public BaseSourcePresenter(Source source, View view) {
        super(view);
        this.mSource = source;
    }

    @Override
    public void start() {
        super.start();
        if (mSource != null)
            mSource.load(this);
    }

    @Override
    public void destroy() {
        super.destroy();
        // 当界面销毁的时候，我们应该吧数据监听销毁
        mSource.dispose();
        mSource = null;
    }
}
