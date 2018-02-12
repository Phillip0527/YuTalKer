package com.im.yutalker.push.fragments.message;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.im.yutalker.factory.model.dp.Group;
import com.im.yutalker.factory.presenter.message.ChatContract;
import com.im.yutalker.push.R;

/**
 * 群聊天界面
 */
public class ChatGroupFragment extends ChatFragment<Group> implements ChatContract.GroupView {


    public ChatGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_chat_group;
    }

    @Override
    protected ChatContract.Presenter initPresenter() {
        return null;
    }

    @Override
    public void onInit(Group group) {
        // 对和你聊天的群的信息进行初始化操作

    }
}
