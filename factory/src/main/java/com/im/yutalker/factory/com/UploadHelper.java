package com.im.yutalker.factory.com;

import android.text.format.DateFormat;
import android.util.Log;

import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.im.yutalker.factory.Factory;
import com.im.yutalker.utils.HashUtil;

import java.io.File;
import java.util.Date;


/**
 * 上传工具类，用于上传任意文件到阿里OSS存储
 * Created by Phillip on 2018/1/11.
 */

public class UploadHelper {
    private static final String TAG = UploadHelper.class.getSimpleName();
    // 与存储区域有关的 对象存储中概览中的访问域名
    private static final String ENDPOINT = "http://oss-cn-beijing.aliyuncs.com";

    // 上传的仓库名称yu-im
    private static final String BUCKET_NAME = "yu-im";

    private static OSS getClient() {
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考访问控制章节
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider("LTAIDMmevMDjHfko"
                , "w2RmFfbgrycSMR8xRs73QVzWclc6eu");
        return new OSSClient(Factory.app(), ENDPOINT, credentialProvider);
    }


    /**
     * 上传的最终方法，成功则返回一个路径
     *
     * @param objKey 上传上去后在服务器独立的一个KEY
     * @param path   需要上传的文件的路径
     * @return 存储地址
     */
    private static String upload(String objKey, String path) {
        // 构造上传请求
        PutObjectRequest putRequest = new PutObjectRequest(BUCKET_NAME, objKey, path);
        try {
            // 初始化上传的client
            OSS oss = getClient();
            // 开始同步上传
            PutObjectResult putResult = oss.putObject(putRequest);
            // 得到一个外网可访问的地址
            String url = oss.presignPublicObjectURL(BUCKET_NAME, objKey);
            // 格式打印输出
            Log.d(TAG, String.format("PublicObjectURL:%s", url));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传普通图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadImg(String path) {
        String key = getImageObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传头像图片
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadPortrait(String path) {
        String key = getPortraitObjKey(path);
        return upload(key, path);
    }

    /**
     * 上传音频
     *
     * @param path 本地地址
     * @return 服务器地址
     */
    public static String uploadAudio(String path) {
        String key = getAudioObjKey(path);
        return upload(key, path);
    }

    /**
     * 分月存储的文件夹名
     *
     * @return yyyyMM
     */
    private static String getDateString() {
        return DateFormat.format("yyyyMM", new Date()).toString();
    }

    // 路径:Image/201801/fwefaefaef.jpg
    private static String getImageObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("Image/%1$s/%2$s.jpg", dateString, fileMd5);
    }

    // 路径:Portrait/201801/fwefaefaef.jpg
    private static String getPortraitObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("Portrait/%1$s/%2$s.jpg", dateString, fileMd5);
    }

    // 路径:Audio/201801/fwefaefaef.jpg
    private static String getAudioObjKey(String path) {
        String fileMd5 = HashUtil.getMD5String(new File(path));
        String dateString = getDateString();
        return String.format("Audio/%1$s/%2$s.mp3", dateString, fileMd5);
    }

}
