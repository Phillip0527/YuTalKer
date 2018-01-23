package com.im.yutalker.push.activities;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.im.yutalker.common.app.Activity;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.account.LoginFragment;
import com.im.yutalker.push.fragments.user.UpdateInfoFragment;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.igexin.sdk.PushService.context;

/**
 * 用户信息界面
 * 可以提供用户修改
 */
public class UserActivity extends Activity {

    private Fragment mFragment;

    @BindView(R.id.im_bg)
    ImageView imBg;

    public static void start(Context context) {
        context.startActivity(new Intent(context, UserActivity.class), ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        mFragment = new UpdateInfoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, mFragment)
                .commit();

        // 初始化背景
        Glide.with(this)
                .load(R.drawable.bg_material_design3)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .bitmapTransform(new BlurTransformation(this, 15)) // 设置图片模糊
                .into(new ViewTarget<ImageView, GlideDrawable>(imBg) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        // 拿到glide的Drawable
                        Drawable drawable = resource.getCurrent();
                        // 使用适配类进行包装
                        drawable = DrawableCompat.wrap(drawable);
                        // 设置着色器效果和颜色,蒙板模式
                        drawable.setColorFilter(getResources().getColor(R.color.trans, null), PorterDuff.Mode.SCREEN);
                        // 设置ImageView
                        this.view.setImageDrawable(drawable);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFragment.onActivityResult(requestCode, resultCode, data);
    }

}
