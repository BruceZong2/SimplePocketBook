package com.zq.personal.accountbook.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Activity的基类
 */
public class BaseActivity extends Activity {
    public ActionBar actionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.actionBar = this.getActionBar();
        if (this.actionBar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.actionBar.setDisplayHomeAsUpEnabled(true);
            } else {
                this.actionBar.setHomeButtonEnabled(true);
                this.actionBar.setDisplayShowHomeEnabled(true);
            }
        }
    }
}
