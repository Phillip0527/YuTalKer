package com.im.yutalker.factory.presenter.user;

import android.text.TextUtils;

import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.R;
import com.im.yutalker.factory.data.DataSource;
import com.im.yutalker.factory.data.helper.AccountHelper;
import com.im.yutalker.factory.data.helper.UserHelper;
import com.im.yutalker.factory.model.api.account.LoginModel;
import com.im.yutalker.factory.model.api.user.UserUpdateModel;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.net.UploadHelper;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.factory.presenter.BasePresenter;
import com.im.yutalker.factory.presenter.account.LoginContract;
import com.im.yutalker.factory.presenter.account.RegisterContract;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import org.w3c.dom.Text;

/**
 * 更新用户的presenter
 * Created by Phillip on 2018/1/18.
 */

public class UpdateInfoPresenter extends BasePresenter<UpdateInfoContract.View> implements UpdateInfoContract.Presenter, DataSource.CallBack<UserCard> {


    public UpdateInfoPresenter(UpdateInfoContract.View view) {
        super(view);
    }

    @Override
    public void update(final String photoFilePath, final String desc, final boolean isMan) {
        start();
        final UpdateInfoContract.View view = getView();
        if (TextUtils.isEmpty(photoFilePath) || TextUtils.isEmpty(desc)) {
            view.showError(R.string.data_account_update_invalid_parameter);
        } else {
            // 上传头像
            Factory.runOnAsync(new Runnable() {
                @Override
                public void run() {
                    String url = UploadHelper.uploadPortrait(photoFilePath);
                    if (TextUtils.isEmpty(url)) {
                        // 上传失败
                        view.showError(R.string.data_upload_error);
                    } else {
                        UserUpdateModel model = new UserUpdateModel("", url, desc, isMan ? User.SEX_MAN : User.SEX_WOMAN);
                        // 网络请求，更新用户
                        UserHelper.update(model, UpdateInfoPresenter.this);
                    }
                }
            });
        }
    }

    @Override
    public void onDataLoaded(UserCard userCard) {
        final UpdateInfoContract.View view = getView();
        if (view == null)
            return;
        // 此时是从网络回送回来的 不保证是主线程
        // 调用主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用主界面更新成功
                view.updateSucceed();
            }
        });
    }

    @Override
    public void onDataNotAvailable(final int strRes) {
        final UpdateInfoContract.View view = getView();
        if (view == null)
            return;
        // 此时是从网络回送回来的 不保证是主线程
        // 调用主线程
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 调用主界面注册成功
                view.showError(strRes);
            }
        });
    }

}
