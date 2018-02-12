package com.im.yutalker.factory.presenter.message;

import com.im.yutalker.factory.model.dp.Group;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.presenter.BaseContract;

/**
 * 聊天契约
 * Created by Phillip on 2018/2/10.
 */

public interface ChatContract {
    interface Presenter extends BaseContract.Presenter {
        // 发送文字
        void pushText(String content);

        // 发送语音
        void pushAudio(String path);

        // 发送多张图片
        void pushImages(String[] paths);

        // 重新发送一个消息，返回是否调度成功
        boolean rePush(Message message);

    }

    // 界面的基类
    interface View<InitModel> extends BaseContract.RecyclerView<Presenter,Message> {
        // 初始化的Model
        void onInit(InitModel model);
    }

    // 人聊天界面
    interface UserView extends View<User>{

    }

    // 群聊天界面
    interface GroupView extends View<Group>{

    }
}
