package com.im.yutalker.factory.presenter.message;

import android.support.v7.util.DiffUtil;

import com.im.yutalker.factory.data.helper.MessageHelper;
import com.im.yutalker.factory.data.message.MessageDataSource;
import com.im.yutalker.factory.model.api.message.MsgCreateModel;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.factory.presenter.BaseSourcePresenter;
import com.im.yutalker.factory.utils.DiffUiDataCallback;

import java.util.List;

/**
 * 聊天Presenter基础类
 * Created by Phillip on 2018/2/11.
 */

@SuppressWarnings("WeakerAccess")
public class ChatPresenter<View extends ChatContract.View>
        extends BaseSourcePresenter<Message, Message, MessageDataSource, View>
        implements ChatContract.Presenter {
    // 接收者id，可以是人和群
    protected String mReceiverId;
    // 区分是人还是群
    protected int mReceiverType;

    public ChatPresenter(MessageDataSource source, View view, String receiverId, int receiverType) {
        super(source, view);
        this.mReceiverId = receiverId;
        this.mReceiverType = receiverType;
    }

    @Override
    public void pushText(String content) {
        // 构建一个新的消息，建造者模式
        MsgCreateModel model = new MsgCreateModel.Builder()
                .receiver(mReceiverId, mReceiverType)
                .content(content, Message.TYPE_STR)
                .build();
        // 进行网络发送
        MessageHelper.push(model);
    }

    @Override
    public void pushAudio(String path) {
        // TODO 发送语音
    }

    @Override
    public void pushImages(String[] paths) {
        // TODO 发送图片
    }

    @Override
    public boolean rePush(Message message) {
        // 确定消息是否可以重复发送
        if (Account.getUserId().equalsIgnoreCase(message.getSender().getId())
                && message.getStatus() == Message.STATUS_FAILED) {
            // 更改状态
            message.setStatus(Message.STATUS_CREATED);
            // 构建发送model
            MsgCreateModel model = MsgCreateModel.buildWithMessage(message);
            MessageHelper.push(model);
            return true;
        }
        return false;
    }

    @Override
    public void onDataLoaded(List<Message> messages) {
        ChatContract.View view = getView();
        if (view == null)
            return;

        // 拿到老数据
        @SuppressWarnings("unchecked")
        List<Message> old = view.getRecyclerAdapter().getItems();

        // 进行差异计算
        DiffUiDataCallback<Message> callback = new DiffUiDataCallback<>(old, messages);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        // 进行刷新界面
        refreshData(result, messages);
    }
}
