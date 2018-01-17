package com.im.yutalker.factory.data.helper;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.R;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.api.account.AccountRspModel;
import com.im.yutalker.factory.model.api.account.RegisterModel;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.net.NetWork;
import com.im.yutalker.factory.net.RemoteService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Phillip on 2018/1/17.
 */

public class AccountHelper {


    /**
     * 注册的接口，异步的调用
     *
     * @param model    传递一个注册的model
     * @param callBack 成功与失败的接口回送
     */
    public static void register(RegisterModel model, final DataSource.CallBack<User> callBack) {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.getRetrofit().create(RemoteService.class);
        // 得到一个Call<T>
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        // 异步的请求
        call.enqueue(new Callback<RspModel<AccountRspModel>>() {
            @Override
            public void onResponse(Call<RspModel<AccountRspModel>> call,
                                   Response<RspModel<AccountRspModel>> response) {
                // 网络请求成功
                // 从返回中得到我们的全局model，内部是使用gson进行解析
                RspModel<AccountRspModel> rspModel = response.body();
                if (rspModel.success()) {
                    // 拿到实体
                    AccountRspModel accountRspModel = rspModel.getResult();
                    if (accountRspModel.isBind()) {
                        User user = accountRspModel.getUser();
                        // TODO 进行的是数据库写入和缓存绑定
                        // 然后返回
                        callBack.onDataLoaded(user);
                    } else {
                        // 绑定pushId操作
                        bindPushId(callBack);
                    }
                } else {
                    // 错误解析
                    Factory.decodeRspCode(rspModel,callBack);
                }
            }

            @Override
            public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
                // 网络请求失败
                callBack.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    /**
     * 对设备Id进行绑定操作
     *
     * @param callBack CallBack
     */
    public static void bindPushId(final DataSource.CallBack<User> callBack) {
        // TODO 先抛出一个错误，代表我们没有进行绑定
        callBack.onDataNotAvailable(R.string.app_name);
    }

}
