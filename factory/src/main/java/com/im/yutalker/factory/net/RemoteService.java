package com.im.yutalker.factory.net;

import com.im.yutalker.factory.model.api.RspModel;
import com.im.yutalker.factory.model.api.account.AccountRspModel;
import com.im.yutalker.factory.model.api.account.LoginModel;
import com.im.yutalker.factory.model.api.account.RegisterModel;
import com.im.yutalker.factory.model.api.user.UserUpdateModel;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.persistence.Account;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Phillip on 2018/1/17.
 */

public interface RemoteService {
    /**
     * 网络请求一个注册接口
     *
     * @param model RegisterModel
     * @return RspModel<AccountRspModel>
     */
    @POST("account/register")
    Call<RspModel<AccountRspModel>> accountRegister(@Body RegisterModel model);

    /**
     * 网络请求一个登录接口
     *
     * @param model LoginModel
     * @return RspModel<AccountRspModel>
     */
    @POST("account/login")
    Call<RspModel<AccountRspModel>> accountLogin(@Body LoginModel model);

    /**
     * 网络请求一个绑定设备Id接口
     *
     * @param pushId 设备Id
     * @return RspModel<AccountRspModel>
     */
    @POST("account/bind/{pushId}")
    Call<RspModel<AccountRspModel>> accountBind(@Path(value = "pushId", encoded = true) String pushId);

    /**
     * 网络请求一个更新用户接口
     *
     * @param model UserUpdateModel
     * @return RspModel<UserCard>
     */
    @PUT("user")
    Call<RspModel<UserCard>> userUpdate(@Body UserUpdateModel model);
}
