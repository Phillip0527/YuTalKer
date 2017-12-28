package com.im.yutalker.common.widget.recycler;

/**
 * Created by Phillip on 2017/12/28.
 */

public interface AdapterCallback<Data> {
        void update(Data data,RecyclerAdapter.ViewHolder<Data> holder);
}
