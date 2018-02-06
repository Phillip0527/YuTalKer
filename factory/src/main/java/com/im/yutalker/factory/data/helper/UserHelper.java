package com.im.yutalker.factory.data.helper;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.R;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.api.account.AccountRspModel;
import com.im.yutalker.factory.model.api.user.UserUpdateModel;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.model.dp.User_Table;
import com.im.yutalker.factory.net.NetWork;
import com.im.yutalker.factory.net.RemoteService;
import com.im.yutalker.factory.presenter.contact.FollowPresenter;
import com.im.yutalker.utils.CollectionUtil;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Phillip on 2018/1/22.
 */

public class UserHelper {

    /**
     * 更新用户，异步的
     *
     * @param model    用户更新的model
     * @param callBack 网络请求结果回调
     */
    public static void update(UserUpdateModel model, final DataSource.CallBack<UserCard> callBack) {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call<T>
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        // 异步的网络请求
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard userCard = rspModel.getResult();
                    // 唤起进行保存操作
                    Factory.getUserCenter().dispatch(userCard);
                    callBack.onDataLoaded(userCard);
                } else {
                    // 错误的情况下
                    Factory.decodeRspCode(rspModel, callBack);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callBack.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 搜索用户，异步的
     *
     * @param name     搜索的文字
     * @param callBack 网络请求结果回调
     */
    public static Call search(String name, final DataSource.CallBack<List<UserCard>> callBack) {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call<T>
        Call<RspModel<List<UserCard>>> call = service.userSearch(name);
        // 异步的网络请求
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    // 直接返回
                    callBack.onDataLoaded(rspModel.getResult());
                } else {
                    // 错误的情况下
                    Factory.decodeRspCode(rspModel, callBack);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                callBack.onDataNotAvailable(R.string.data_network_error);
            }
        });

        // 把当前的调度者返回
        return call;
    }

    /**
     * 关注的网络请求
     *
     * @param userId   用户id
     * @param callBack 网络请求结果回调
     */
    public static void follow(final String userId, final DataSource.CallBack<UserCard> callBack) {
        RemoteService service = NetWork.remote();
        Call<RspModel<UserCard>> call = service.userFollow(userId);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard card = rspModel.getResult();
                    // 唤起进行保存操作
                    Factory.getUserCenter().dispatch(card);
                    // 返回数据
                    callBack.onDataLoaded(card);
                } else {
                    Factory.decodeRspCode(rspModel, callBack);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callBack.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 刷新联系人的操作，异步的
     * 不需要Callback，直接存储到数据库
     * 并通过数据库观察者进行通知界面更新
     * 界面更新的时候进行对比，差异更新
     */
    public static void refreshContacts() {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call<T>
        Call<RspModel<List<UserCard>>> call = service.userContacts();
        // 异步的网络请求
        call.enqueue(new Callback<RspModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RspModel<List<UserCard>>> call, Response<RspModel<List<UserCard>>> response) {
                RspModel<List<UserCard>> rspModel = response.body();
                if (rspModel.success()) {
                    // 拿到集合
                    List<UserCard> cards = rspModel.getResult();
                    if (cards == null || cards.size() == 0)
                        return;
//                    UserCard[] cards1 = CollectionUtil.toArray(cards, UserCard.class);
                    UserCard[] cards1 = cards.toArray(new UserCard[0]);
                    Factory.getUserCenter().dispatch(cards1);
                } else {
                    // 错误的情况下
                    Factory.decodeRspCode(rspModel, null);
                }
            }

            @Override
            public void onFailure(Call<RspModel<List<UserCard>>> call, Throwable t) {
                // nothing
            }
        });
    }

    // 从本地查询一个用户的信息
    public static User findFromLocal(String id) {
        return SQLite.select()
                .from(User.class)
                .where(User_Table.id.eq(id))
                .querySingle();
    }

    // 从网络查询一个用户的信息
    public static User findFromNet(String id) {
        RemoteService service = NetWork.remote();
        try {
            Response<RspModel<UserCard>> response = service.userFind(id).execute();
            UserCard card = response.body().getResult();
            if (card != null) {
                User user = card.build();
                Factory.getUserCenter().dispatch(card);
                return user;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 搜索一个用户，优先从本地缓存取
     * 没有再从网络拉取
     */
    public static User search(String id) {
        User user = findFromLocal(id);
        if (user == null) {
            return findFromNet(id);
        }
        return user;
    }

    /**
     * 搜索一个用户，优先从网络取
     * 没有再从本地缓存取
     */
    public static User searchFirstOfNet(String id) {
        User user = findFromNet(id);
        if (user == null) {
            return findFromLocal(id);
        }
        return user;
    }


}
