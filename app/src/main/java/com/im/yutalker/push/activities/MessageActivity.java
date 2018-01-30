package com.im.yutalker.push.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.im.yutalker.factory.model.Author;
import com.im.yutalker.push.R;

public class MessageActivity extends Activity {

    /**
     * 显示一个人的聊天记录
     * @param context 上下文
     * @param author 聊天的人
     */
    public static void show(Context context, Author author) {
        context.startActivity(new Intent(context, MessageActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
    }

}
