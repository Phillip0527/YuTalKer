package com.im.yutalker.common.tools;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Window;


/**
 * Created by Phillip on 2018/1/9.
 */

public class UiTool {
    private static int STATUS_BAR_HEIGHT = -1; //状态栏高度变量

    /**
     * 得到状态栏高度
     *
     * @param activity Activity
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && STATUS_BAR_HEIGHT == -1) {
            try {
                final Resources res = activity.getResources();
                // 尝试获取status_bar_height这个属性Id对应的资源int值
                // 第一种获取资源Id方式
                int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");

                // 第二种反射获取资源Id方式（第一种没拿到）
                if (resourceId <= 0) {
                    Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                    Object obj = clazz.newInstance();
                    resourceId = Integer.parseInt(clazz.getField("status_bar_height").get(obj).toString());
                }

                // 如果拿到Id了就直接调用获取值
                if (resourceId > 0) {
                    STATUS_BAR_HEIGHT = res.getDimensionPixelSize(resourceId);
                }

                // 第三种获取资源Id方式（第一、二种都没拿到）不过不推荐使用，因为这个方法依赖于WMS(窗口管理服务的回调)。
                if (resourceId <= 0) {
                    // 通过window拿取
                    Rect rectangle = new Rect();
                    Window window = activity.getWindow();
                    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                    STATUS_BAR_HEIGHT = rectangle.top;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenHeight(Activity activity) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }
}
