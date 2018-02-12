package com.im.yutalker.common.app;

import android.app.*;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.widget.Toast;

import net.qiujuer.genius.kit.handler.Run;
import net.qiujuer.genius.kit.handler.runable.Action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phillip on 2018/1/9.
 * 获取缓存文File
 */

public class Application extends android.app.Application {
    private static Application instance;

    // 所有activity
    private List<Activity> activities = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // activity生命周期的回调
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                // 创建时添加到activity集合中
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                // 销毁时从activity集合中删除
                activities.remove(activity);
            }
        });
    }

    // 关闭所有activity
    public void finishAll() {
        for (Activity activity : activities) {
            activity.finish();
        }
        showAccountActivity(this);
    }

    // 跳转到登录界面
    protected void showAccountActivity(Context context){
    }

    /**
     * 外部获取单例
     *
     * @return Application
     */
    public static Application getInstance() {
        return instance;
    }

    /**
     * 获取缓存文件夹地址
     *
     * @return 当前App的缓存文件夹地址
     */
    public static File getCacheDirFile() {
        return instance.getCacheDir();
    }

    /**
     * 获取头像的临时存储文件的地址
     *
     * @return 临时文件
     */
    public static File getPortraitTmpFile() {
        // 得到头像目录缓存文件夹地址
        File dir = new File(getCacheDirFile(), "portrait");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        // 删除旧的缓存文件
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }

        // 返回一个当前时间戳的目录文件地址
        File path = new File(dir, SystemClock.uptimeMillis() + ".jpg");
        return path.getAbsoluteFile();
    }

    /**
     * 获取声音文件的本地地址
     *
     * @param isTmp 是否为缓存文件 True 每次返回的文件地址是一样的
     * @return 录音文件的地址
     */
    public static File getAudioTmpFile(boolean isTmp) {
        // 得到录音目录缓存文件夹地址
        File dir = new File(getCacheDirFile(), "audio");
        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        // 删除旧的缓存文件
        File[] fiels = dir.listFiles();
        if (fiels != null && fiels.length > 0) {
            for (File fiel : fiels) {
                //noinspection ResultOfMethodCallIgnored
                fiel.delete();
            }
        }

        // 如果isTmp为 Treu 则创建一个tmp.mp3的缓存文件 False 则创建一个时间戳的mp3文件
        File path = new File(dir, isTmp ? "tmp.mp3" : SystemClock.uptimeMillis() + ".mp3");
        return path.getAbsoluteFile();
    }


    /**
     * 显示一个Toast
     *
     * @param msg 字符串
     */
    public static void showToast(final String msg) {
        // Toast 只能在主线程中显示，所有需要进行线程转换，
        // 保证一定是在主线程进行的show操作
        Run.onUiAsync(new Action() {
            @Override
            public void call() {
                // 这里进行回调的时候一定就是主线程状态了
                Toast toast = Toast.makeText(instance, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }

    /**
     * 显示一个Toast
     *
     * @param msgId 传递的是字符串的资源
     */
    public static void showToast(@StringRes int msgId) {
        //instance.getString(msgId)是instance.getResources().getString(msgId)的简化
        showToast(instance.getString(msgId));
    }


}
