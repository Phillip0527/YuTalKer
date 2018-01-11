package com.im.yutalker.factory;

import com.im.yutalker.common.app.Application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Phillip on 2018/1/11.
 */

public class Factory {
    // 单例模式
    private static Factory instance;
    // 线程池
    private final Executor executor;

    // 恶汉模式的单例，只要调用这个类的任何一个方法都会实例化该类
    static {
        instance = new Factory();
    }

    public Factory() {
        // 新建4个线程的线程池
        executor = Executors.newFixedThreadPool(4);
    }

    public static Application app() {
        return Application.getInstance();
    }

    /**
     * 异步运行的方法
     * @param runnable Runnable
     */
    public static void runOnAsync(Runnable runnable){
        // 拿到单例，拿到线程池，然后异步执行
        instance.executor.execute(runnable);
    }

}
