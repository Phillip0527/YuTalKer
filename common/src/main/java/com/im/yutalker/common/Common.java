package com.im.yutalker.common;

/**
 *
 * Created by Phillip on 2017/12/23.
 */

public class Common {
    /**
     * 一些不可变的永恒的参数
     * 通常用于设置
     */
    public interface Constance{
        // 手机号正则表达式,11位手机号
        String REGEX_MOBILE="[1][3,4,5,7,8][0-9]{9}$";

        // 基础的网络请求地址
//        String API_URL="http://39.107.78.44/api/";
        String API_URL="http://39.107.78.44/api/";
    }
}
