package com.im.yutalker.factory.data.user;

import android.text.TextUtils;

import com.im.yutalker.factory.data.helper.DbHelper;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.model.dp.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 用户中心的实现类
 * Created by Phillip on 2018/1/31.
 */

public class UserDispatcher implements UserCenter {

    private static UserCenter instance;

    // 单线程池,处理卡片，一个个的消息进行处理
    private Executor executor = Executors.newSingleThreadExecutor();

    public static UserCenter getInstance() {
        if (instance == null) {
            synchronized (UserDispatcher.class) {
                if (instance == null)
                    instance = new UserDispatcher();
            }
        }
        return instance;
    }

    @Override
    public void dispatch(UserCard... cards) {
        if (cards == null || cards.length == 0)
            return;
        // 丢到线程池中
        executor.execute(new UserCardHandler(cards));
    }

    /**
     * 线程调度时触发run()方法
     */
    private class UserCardHandler implements Runnable {
        private final UserCard[] cards;

        UserCardHandler(UserCard[] cards) {
            this.cards = cards;
        }

        @Override
        public void run() {
            // 当被线程调度时触发
            List<User> users = new ArrayList<>();
            for (UserCard card : cards) {
                if (card == null || TextUtils.isEmpty(card.getId()))
                    continue;
                // 添加操作
                users.add(card.build());
            }
            // 进行数据库存储并分发通知，异步的
            DbHelper.save(User.class, users.toArray(new User[0]));
        }
    }
}
