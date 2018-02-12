package com.im.yutalker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.im.yutalker.common.app.Activity;
import com.im.yutalker.common.widget.PortraitView;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.main.ActiveFragment;
import com.im.yutalker.push.fragments.main.ContactFragment;
import com.im.yutalker.push.fragments.main.GroupFragment;
import com.im.yutalker.push.helper.NavHelper;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.widget.FloatActionButton;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener,
        NavHelper.OnTabChangedListener<Integer> {

    @BindView(R.id.appbar)
    View mLayAppbar;

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @BindView(R.id.tv_title)
    TextView mTitle;

    @BindView(R.id.lay_container)
    FrameLayout mContainer;

    @BindView(R.id.navigation)
    BottomNavigationView mNavigation;

    @BindView(R.id.btn_action)
    FloatActionButton mAction;

    private NavHelper<Integer> mNavHelper;

    /**
     * MainActivity显示的入口
     *
     * @param context 上下文
     */
    public static void show(Context context) {
//        Pair pair = new Pair<>(view, "iv");
//        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                (Activity)context, pair);
//        context.startActivity(new Intent(context, MainActivity.class),activityOptions.toBundle());
        Activity activity = (Activity) context;
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        if (Account.isComplete()) {
            // 用户资料完全，则走正常程序
            return super.initArgs(bundle);
        } else {
            UserActivity.show(this);
            return false;
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        // 初始化底部辅助工具类
        mNavHelper = new NavHelper<>(this, R.id.lay_container,
                getSupportFragmentManager(), this);
        mNavHelper
                .add(R.id.action_home, new NavHelper.Tab<>(ActiveFragment.class, R.string.title_home))
                .add(R.id.action_group, new NavHelper.Tab<>(GroupFragment.class, R.string.title_group))
                .add(R.id.action_contact, new NavHelper.Tab<>(ContactFragment.class, R.string.title_contact));

        // 添加对底部导航按钮点击的监听
        mNavigation.setOnNavigationItemSelectedListener(this);

        Glide.with(this)
                .load(R.drawable.bg_appbar)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new ViewTarget<View, GlideDrawable>(mLayAppbar) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        this.view.setBackground(resource.getCurrent());
                    }
                });
    }

    @Override
    protected void initData() {
        super.initData();
        // 从底部导航中接管我的Menu，然后进行手动触发第一次点击
        Menu menu = mNavigation.getMenu();
        // 触发首次选中Home
        menu.performIdentifierAction(R.id.action_home, 0);

        // 初始化头像
        mPortrait.setup(Glide.with(this), Account.getUser());
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        PersonalActivity.show(this, Account.getUserId());
    }

    @OnClick(R.id.im_search)
    void onSearchMenuClick() {
        // 判断是群还是人的按钮
        int type = Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)
                ? SearchActivity.TYPE_GROUP : SearchActivity.TYPE_USER;
        // 打开搜索界面
        SearchActivity.show(this, type);
    }

    @OnClick(R.id.btn_action)
    void onActionClick() {
        // 判断是群还是人的按钮
        if (Objects.equals(mNavHelper.getCurrentTab().extra, R.string.title_group)) {
            // TODO 打开群搜索界面
        } else {
            // 打开用户搜索界面
            SearchActivity.show(this, SearchActivity.TYPE_USER);
        }

    }

    /**
     * 当我们的底部导航被点击的时候触发
     *
     * @param item MenuItem
     * @return True 代表我们能够处理这个点击
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // 转接事件流到工具类中
        return mNavHelper.performClickMenu(item.getItemId());
    }


    /**
     * NavHelper 处理后回调的方法
     *
     * @param newTab 新的Tab
     * @param oldTab 旧的Tab
     */
    @Override
    public void onTabChanged(NavHelper.Tab<Integer> newTab, NavHelper.Tab<Integer> oldTab) {
        // 从额外字段中取出我们的Title资源Id
        mTitle.setText(newTab.extra);

        // 对浮动按钮进行隐藏与显示的动画
        float transY = 0;
        float rotation = 0;
        int duration = 480;
        if (Objects.equals(newTab.extra, R.string.title_home)) {
            // 主界面时隐藏
            transY = Ui.dipToPx(getResources(), 76);
            rotation = -360;
        } else {
            // transY 默认为0 则显示
            if (Objects.equals(newTab.extra, R.string.title_group)) {
                // 群
                mAction.setImageResource(R.drawable.ic_contact_add_fill);
                rotation = 360;
            } else {
                // 联系人
                mAction.setImageResource(R.drawable.ic_contact_add_fill);
                rotation = 0;
            }
        }

        mAction.animate()
                .rotation(rotation)
                .translationY(transY)
                .setInterpolator(new AnticipateOvershootInterpolator(1))
                .setDuration(duration)
                .start();
    }

    // 程序退出
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                exitBy2Click();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private static Boolean isExit = false;

    private void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
        }
    }


}
