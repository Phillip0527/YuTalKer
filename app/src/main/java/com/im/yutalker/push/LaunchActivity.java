package com.im.yutalker.push;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.util.Property;
import android.view.View;

import com.im.yutalker.common.app.Activity;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.push.activities.AccountActivity;
import com.im.yutalker.push.activities.MainActivity;
import com.im.yutalker.push.fragments.assist.PermissionsFragment;

public class LaunchActivity extends Activity {

    // Drawable
    private ColorDrawable mBgDrawable;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_launch;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        // 拿到跟布局
        View root = findViewById(R.id.activity_launch);
        // 得到一个颜色
        int color = root.getResources().getColor(R.color.colorPrimary, null);
        // 创建颜色Drawable
        ColorDrawable colorDrawable = new ColorDrawable(color);
        // 设置背景
        root.setBackground(colorDrawable);
        mBgDrawable = colorDrawable;
    }

    @Override
    protected void initData() {
        super.initData();
        // 动画进入到%50等待PushId获取到
        startAnim(0.5f, new Runnable() {
            @Override
            public void run() {
                // 检查等待状态
                waitPushReceiverId();
            }
        });
    }

    /**
     * 等待个推框架对我们的pushId设置好值
     */
    private void waitPushReceiverId() {
        if (Account.isLogin()) {
            // 已经登录的情况下判断是否绑定
            // 没绑定则等待广播接收器进行绑定
            if (Account.isBind()) {
                skip();
                return;
            }
        } else {
            // 没有登录
            // 拿到了PushId,但是没有登录情况下是不能绑定pushId的
            if (!TextUtils.isEmpty(Account.getPushId())) {
                // 跳转
                skip();
                return;
            }
        }


        // 没拿到pushId，循环等待
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                waitPushReceiverId();
            }
        }, 500);
    }


    private void skip() {
        startAnim(1f, new Runnable() {
            @Override
            public void run() {
                reallySkip();
            }
        });

    }

    private void reallySkip() {
        // 检查权限
        if (PermissionsFragment.haveAllPerms(this, getSupportFragmentManager())) {
            // 检查跳转到主页还是登录页
            if (Account.isLogin()) {
                MainActivity.show(this);
            } else {
                AccountActivity.show(this);
            }
            finish();
        }
    }

    /**
     * 给背景设置一个动画
     *
     * @param endProgress 动画的结束进度
     * @param enCallBack  动画结束时触发
     */
    private void startAnim(float endProgress, final Runnable enCallBack) {
        // 获取一个最终的颜色
        int finalColor = getResources().getColor(R.color.white, null);

        ArgbEvaluator evaluator = new ArgbEvaluator();

        // 运算当前进度的颜色
        int endColor = (int) evaluator.evaluate(endProgress, mBgDrawable.getColor(), finalColor);

        // 构建一个属性动画
        ValueAnimator valueAnimator = ObjectAnimator.ofObject(this, property, evaluator, endColor);
        valueAnimator.setDuration(2000);// 时间
        valueAnimator.setIntValues(mBgDrawable.getColor(), endColor);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                // 结束时触发
                enCallBack.run();
            }
        });
        valueAnimator.start();
    }

    private final Property<LaunchActivity, Object> property = new Property<LaunchActivity, Object>(Object.class, "color") {
        @Override
        public void set(LaunchActivity object, Object value) {
            object.mBgDrawable.setColor((Integer) value);
        }

        @Override
        public Object get(LaunchActivity object) {
            return object.mBgDrawable.getColor();
        }
    };

}
