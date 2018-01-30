package com.im.yutalker.common.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;
import com.im.yutalker.common.R;
import com.im.yutalker.factory.model.Author;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 头像控件封装
 * Created by Phillip on 2017/12/29.
 */

public class PortraitView extends CircleImageView {

    public PortraitView(Context context) {
        super(context);
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setup(RequestManager manager, Author author) {
        if (author == null)
            return;
        setup(manager, author.getPortrait());
    }

    public void setup(RequestManager manager, String url) {
        setup(manager, R.drawable.ic_default_portrait, url);
    }

    public void setup(RequestManager manager, int resourceId, String url) {
        if (url == null)
            url = "";
        manager.load(url)
                .placeholder(resourceId)
                .dontAnimate() // 不能使用动画，因为用占位图片会导致永远不显示头像图片
                .centerCrop()
                .into(this);
    }
}
