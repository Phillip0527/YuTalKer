package com.im.yutalker.common.app;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.im.yutalker.common.R;


/**
 * Created by Phillip on 2018/1/24.
 */

public abstract class ToolBarActivity extends Activity {
    protected Toolbar mToolbar;

    @Override
    protected void initWidget() {
        super.initWidget();
        initToolbar((Toolbar) findViewById(R.id.toolbar));
    }

    public void initToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        initTitleNeedBack();
    }


    protected void initTitleNeedBack() {
        // 设置左上角的返回按钮为实际的返回效果，不设置就没有返回效果
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
