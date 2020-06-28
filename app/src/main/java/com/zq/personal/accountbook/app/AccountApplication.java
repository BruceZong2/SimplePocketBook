package com.zq.personal.accountbook.app;

import android.app.Application;
import android.content.Context;

/**
 * 应用类
 *
 * @since 2020-05-30
 */
public class AccountApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
    }

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    public static Context getContext() {
        return context;
    }
}
