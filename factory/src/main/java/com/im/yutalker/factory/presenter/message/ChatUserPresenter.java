package com.im.yutalker.factory.presenter.message;

import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.data.message.MessageDataSource;
import com.im.yutalker.factory.data.message.MessageRepository;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.User;

/**
 * Created by Phillip on 2018/2/11.
 */

public class ChatUserPresenter extends ChatPresenter<ChatContract.UserView>
        implements ChatContract.Presenter {

    public ChatUserPresenter(ChatContract.UserView view, String receiverId) {
        // 数据源，view，接收者，接收者类型
        super(new MessageRepository(receiverId), view, receiverId, Message.RECEIVER_TYPE_NONE);
    }

    @Override
    public void start() {
        super.start();

        // 从本地拿这个人的信息
        User receiver = UserHelper.findFromLocal(mReceiverId);
        getView().onInit(receiver);
    }
}
