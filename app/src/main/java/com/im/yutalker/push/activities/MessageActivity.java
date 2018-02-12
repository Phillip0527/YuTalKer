package com.im.yutalker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.im.yutalker.common.app.Activity;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.factory.model.Author;
import com.im.yutalker.factory.model.dp.Group;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.message.ChatGroupFragment;
import com.im.yutalker.push.fragments.message.ChatUserFragment;

import org.w3c.dom.Text;

public class MessageActivity extends Activity {
    // 接收者id，可以是人和群
    public static final String KEY_RECEIVER_ID = "KEY_RECEIVER_ID";
    // 是否是群
    private static final String KEY_RECEIVER_IS_GROUP = "KEY_RECEIVER_IS_GROUP";

    private String mReceiverId;
    private boolean mIsGroup;

    /**
     * 显示一个人的聊天记录
     *
     * @param context 上下文
     * @param author  聊天的人
     */
    public static void show(Context context, Author author) {
        if (context == null || author == null || TextUtils.isEmpty(author.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, author.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, false);
        context.startActivity(intent);
    }

    /**
     * 发起群聊天
     *
     * @param context 上下文
     * @param group   群的Model
     */
    public static void show(Context context, Group group) {
        if (context == null || group == null || TextUtils.isEmpty(group.getId()))
            return;
        Intent intent = new Intent(context, MessageActivity.class);
        intent.putExtra(KEY_RECEIVER_ID, group.getId());
        intent.putExtra(KEY_RECEIVER_IS_GROUP, true);
        context.startActivity(intent);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        mReceiverId = bundle.getString(KEY_RECEIVER_ID);
        mIsGroup = bundle.getBoolean(KEY_RECEIVER_IS_GROUP);
        return !TextUtils.isEmpty(mReceiverId);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        setTitle("");
        Fragment fragment;
        if (mIsGroup)
            fragment = new ChatGroupFragment();
        else
            fragment = new ChatUserFragment();

        // 从activity传递参数到fragment
        Bundle bundle = new Bundle();
        bundle.putString(KEY_RECEIVER_ID, mReceiverId);
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }
}
