package com.im.yutalker.push.fragments.user;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.im.yutalker.common.app.Application;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.common.widget.PortraitView;
import com.im.yutalker.factory.Factory;
import com.im.yutalker.factory.net.UploadHelper;
import com.im.yutalker.factory.presenter.user.UpdateInfoContract;
import com.im.yutalker.factory.presenter.user.UpdateInfoPresenter;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.MainActivity;
import com.im.yutalker.push.fragments.media.GalleryFragment;
import com.yalantis.ucrop.UCrop;

import net.qiujuer.genius.ui.widget.Button;
import net.qiujuer.genius.ui.widget.Loading;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * 用户更新信息的界面
 */
public class UpdateInfoFragment extends PresenterFragment<UpdateInfoContract.Presenter> implements UpdateInfoContract.View {
    @BindView(R.id.im_sex)
    ImageView mSex;

    @BindView(R.id.edit_desc)
    EditText mDesc;

    @BindView(R.id.im_portrait)
    PortraitView portraitView;

    @BindView(R.id.loading)
    Loading mLoading;

    @BindView(R.id.btn_submit)
    Button mSubmit;

    // 头像的本地路径
    private String mPortraitPath;
    // 是否是男人
    private boolean isMan = true;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    public void onPortraitClick() {
        new GalleryFragment().setListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelectedImage(String path) {
                UCrop.Options options = new UCrop.Options();
                // 设置图片处理格式JPEG
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                // 设置压缩后的图片精度
                options.setCompressionQuality(96);
                // 设置图片在切换比例时的动画
                options.setImageToCropBoundsAnimDuration(666);
                // 设置状态栏颜色
                options.setStatusBarColor(getResources().getColor(R.color.colorAccentAlpha, null));
                // 设置标题栏颜色
                options.setToolbarColor(getResources().getColor(R.color.colorAccentAlpha, null));
                // 设置控件点击颜色
                options.setActiveWidgetColor(getResources().getColor(R.color.colorAccent, null));
                // 设置裁剪外边框颜色
//                options.setCropFrameColor(getResources().getColor(R.color.white));
                // 设置裁剪网格颜色
//                options.setCropGridColor(getResources().getColor(R.color.white));
                // 设置裁剪背景颜色
//                options.setDimmedLayerColor(getResources().getColor(R.color.white));


                // 得到头像缓存地址
                File dPath = Application.getPortraitTmpFile();

                UCrop.of(Uri.fromFile(new File(path)), Uri.fromFile(dPath))
                        .useSourceImageAspectRatio()// 图片原始宽高比
//                        .withAspectRatio(1, 1) // 1比1比例
                        .withMaxResultSize(520, 520) // 返回最大的尺寸
                        .withOptions(options) // 相关参数
                        .start(getActivity());
            }
            // show() 是BottomSheetDialogFragment自带的方法，建议用getChildFragmentManager
            // tag GalleryFragment.class 名
        }).show(getChildFragmentManager(), GalleryFragment.class.getName());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 收到从Activity中传递过来的回调，然后取出值进行加载图片
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            //得到对应的uri
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                LoadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Application.showToast(R.string.data_rsp_error_unknown);
        }
    }

    @OnClick(R.id.im_sex)
    void onSexClick() {
        // 性别图标点击触发
        isMan = !isMan;// 反向性别

        Drawable drawable = getResources().getDrawable(isMan ? R.drawable.ic_sex_man : R.drawable.ic_sex_woman, null);
        mSex.setImageDrawable(drawable);
        // 设置背景层级切换颜色
        mSex.getBackground().setLevel(isMan ? 0 : 1);
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        String desc = mDesc.getText().toString();
        // 调用P层进行注册
        mPresenter.update(mPortraitPath, desc, isMan);
    }

    /**
     * 加载到当前的头像中
     *
     * @param uri Uri
     */
    private void LoadPortrait(Uri uri) {
        // 得到头像地址
        mPortraitPath = uri.getPath();

        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(portraitView);
    }

    @Override
    public void showError(int str) {
        super.showError(str);
        // 当需要显示错误的时候触发，一定是结束了

        // 停止loading
        mLoading.stop();
        // 让控件可以输入
        portraitView.setEnabled(true);
        mDesc.setEnabled(true);
        mSex.setEnabled(true);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(true);

    }

    @Override
    public void showLoading() {
        super.showLoading();
        // 正在进行时
        // 开始loading
        mLoading.start();
        // 让控件可以输入
        portraitView.setEnabled(false);
        mDesc.setEnabled(false);
        mSex.setEnabled(false);
        // 提交按钮可以继续点击
        mSubmit.setEnabled(false);
    }

    @Override
    public void updateSucceed() {
        // 更新成功
        // 跳转到MainActivity界面
        MainActivity.show(getActivity());
        // 关闭当前界面
        getActivity().finish();
    }

    @Override
    protected UpdateInfoContract.Presenter initPresenter() {
        return new UpdateInfoPresenter(this);
    }


}
