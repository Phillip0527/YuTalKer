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
