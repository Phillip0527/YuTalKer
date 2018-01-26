package com.im.yutalker.factory.data.helper;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.R;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.api.account.AccountRspModel;
import com.im.yutalker.factory.model.api.user.UserUpdateModel;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.net.NetWork;
import com.im.yutalker.factory.net.RemoteService;
import com.im.yutalker.factory.presenter.contact.FollowPresenter;

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
    public static void update(UserUpdateModel model, DataSource.CallBack<UserCard> callBack) {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call<T>
        Call<RspModel<UserCard>> call = service.userUpdate(model);
        // 异步的网络请求
        call.enqueue(new UserCardRspCallBack(callBack));
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

    public static void follow(final String userId, final DataSource.CallBack<UserCard> callBack) {
        RemoteService service = NetWork.remote();
        Call<RspModel<UserCard>> call = service.userFollow(userId);
        call.enqueue(new Callback<RspModel<UserCard>>() {
            @Override
            public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
                RspModel<UserCard> rspModel = response.body();
                if (rspModel.success()) {
                    UserCard card = rspModel.getResult();
                    // 保存到本地数据库
                    User user = card.build();
                    user.save();
                    // TODO 通知联系人列表刷新

                    // 返回数据
                    callBack.onDataLoaded(card);
                }else {
                    Factory.decodeRspCode(rspModel,callBack);
                }
            }

            @Override
            public void onFailure(Call<RspModel<UserCard>> call, Throwable t) {
                callBack.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    private static class UserCardRspCallBack implements Callback<RspModel<UserCard>> {
        final DataSource.CallBack<UserCard> callBack;

        public UserCardRspCallBack(DataSource.CallBack<UserCard> callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onResponse(Call<RspModel<UserCard>> call, Response<RspModel<UserCard>> response) {
            RspModel<UserCard> rspModel = response.body();
            if (rspModel.success()) {
                UserCard userCard = rspModel.getResult();
                // 进行数据库存储，UserCard转换成User
                User user = userCard.build();
                user.save();
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
    }
}
