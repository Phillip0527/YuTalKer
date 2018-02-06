package com.im.yutalker.factory.data.helper;

import android.text.TextUtils;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.R;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.api.account.AccountRspModel;
import com.im.yutalker.factory.model.api.account.LoginModel;
import com.im.yutalker.factory.model.api.account.RegisterModel;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.net.NetWork;
import com.im.yutalker.factory.net.RemoteService;
import com.im.yutalker.factory.persistence.Account;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Retrofit网络请求
 * Created by Phillip on 2018/1/17.
 */

public class AccountHelper {


    /**
     * 注册，异步的调用
     *
     * @param model    传递一个注册的model
     * @param callBack 成功与失败的接口回送
     */
    public static void register(RegisterModel model, final DataSource.CallBack<User> callBack) {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call<T>
        Call<RspModel<AccountRspModel>> call = service.accountRegister(model);
        // 异步的请求
        call.enqueue(new AccountRspCallback(callBack));
    }

    /**
     * 登录的接口，异步调用
     *
     * @param model    传递一个登录的model
     * @param callBack 成功与失败的接口回送
     */
    public static void login(LoginModel model, final DataSource.CallBack<User> callBack) {
        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        // 得到一个Call<T>
        Call<RspModel<AccountRspModel>> call = service.accountLogin(model);
        // 异步的请求
        call.enqueue(new AccountRspCallback(callBack));
    }

    /**
     * 对设备Id进行绑定操作
     *
     * @param callBack 成功与失败的接口回送
     */
    public static void bindPushId(final DataSource.CallBack<User> callBack) {
        String pushId = Account.getPushId();
        if (TextUtils.isEmpty(pushId))
            return;

        // 调用Retrofit对我们的网络请求做代理
        RemoteService service = NetWork.remote();
        Call<RspModel<AccountRspModel>> call = service.accountBind(pushId);
        // 异步的请求
        call.enqueue(new AccountRspCallback(callBack));
    }


    /**
     * 请求的回调部分封装
     */
    private static class AccountRspCallback implements Callback<RspModel<AccountRspModel>> {
        final DataSource.CallBack<User> callBack;

        private AccountRspCallback(DataSource.CallBack<User> callBack) {
            this.callBack = callBack;
        }

        @Override
        public void onResponse(Call<RspModel<AccountRspModel>> call, Response<RspModel<AccountRspModel>> response) {
            // 网络请求成功
            // 从返回中得到我们的全局model，内部是使用gson进行解析
            RspModel<AccountRspModel> rspModel = response.body();
            if (rspModel.success()) {
                // 拿到实体
                AccountRspModel accountRspModel = rspModel.getResult();
                // 获取我的信息
                User user = accountRspModel.getUser();
                DbHelper.save(User.class, user);
                /*
                // 第一种 直接保存
                user.save();
                // 第二种 FlowManager保存 可以存储一个集合
                FlowManager.getModelAdapter(User.class).save(user);
                // 第三种 事务中保存 可以异步
                DatabaseDefinition definition =FlowManager.getDatabase(AppDataBase.class);
                definition.beginTransactionAsync(new ITransaction() {
                    @Override
                    public void execute(DatabaseWrapper databaseWrapper) {
                        FlowManager.getModelAdapter(User.class).save(user);
                    }
                }).build().execute();
                */

                // 同步到XML持久化中
                Account.login(accountRspModel);

                //判断绑定状态，是否绑定设备
                if (accountRspModel.isBind()) {
                    // 设置绑定状态为True
                    Account.setBind(true);
                    if (callBack != null)
                        // 然后返回
                        callBack.onDataLoaded(user);
                } else {
                    // 绑定pushId操作
                    bindPushId(callBack);
                }
            } else {
                // 错误解析
                Factory.decodeRspCode(rspModel, callBack);
            }
        }

        @Override
        public void onFailure(Call<RspModel<AccountRspModel>> call, Throwable t) {
            if (callBack != null)
                // 网络请求失败
                callBack.onDataNotAvailable(R.string.data_network_error);
        }
    }

}
