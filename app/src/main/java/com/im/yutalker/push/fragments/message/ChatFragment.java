package com.im.yutalker.push.fragments.message;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.common.widget.MessageLayout;
import com.im.yutalker.common.widget.PortraitView;
import com.im.yutalker.common.widget.adapter.TextWatchAdapter;
import com.im.yutalker.common.widget.recycler.RecyclerAdapter;
import com.im.yutalker.factory.model.dp.Message;
import com.im.yutalker.factory.model.dp.User;
import com.im.yutalker.factory.persistence.Account;
import com.im.yutalker.factory.presenter.message.ChatContract;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.MessageActivity;

import net.qiujuer.genius.ui.widget.Loading;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 聊天View层的基础Fragment
 * Created by Phillip on 2018/2/9.
 */

public abstract class ChatFragment<InitModel>
        extends PresenterFragment<ChatContract.Presenter>
        implements AppBarLayout.OnOffsetChangedListener,
        ChatContract.View<InitModel> {
    protected String mReceiverId;

    protected Adapter mAdapter;

    @BindView(R.id.lay_container)
    MessageLayout mMessageLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.edit_content)
    EditText mContent;

    @BindView(R.id.btn_submit)
    View mSubmit;


    @Override
    protected void initArgs(Bundle bundle) {
        super.initArgs(bundle);
        mReceiverId = bundle.getString(MessageActivity.KEY_RECEIVER_ID);
    }


    @Override
    protected void initWidget(View root) {
        super.initWidget(root);

        initToolbar();
        initAppbar();
        initEditContent();

        // RecyclerView基本设置
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);

    }

    // 折叠appbar
    private void onBottomPanelOpened() {
        if (mAppBarLayout != null) {
            mAppBarLayout.setExpanded(false, true);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        // 开始进行初始化操作
        mPresenter.start();
    }

    // 初始化Toolbar
    protected void initToolbar() {
        Toolbar toolbar = mToolbar;
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
    }

    // 给界面的appbar设置一个监听，得到关闭与打开时的进度
    private void initAppbar() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    // 初始化输入框监听
    private void initEditContent() {
        // 临时写的土办法
        mContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //got focus
                    onBottomPanelOpened();
                } else {
                    //lost focus
                }
            }
        });
        mContent.addTextChangedListener(new TextWatchAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString().trim();
                boolean needSendMsg = !TextUtils.isEmpty(content);
                // 设置状态改变对应的按钮
                mSubmit.setActivated(needSendMsg);
            }
        });
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    }



    @OnClick(R.id.btn_face)
    void onFaceClick() {
        // TODO
    }

    @OnClick(R.id.btn_record)
    void onRecordClick() {
        // TODO
    }

    @OnClick(R.id.btn_submit)
    void onSubmitClick() {
        if (mSubmit.isActivated()) {
            // 发送
            String content = mContent.getText().toString();
            mContent.setText("");
            mPresenter.pushText(content);
        } else {
            onMoreClick();
        }
    }

    private void onMoreClick() {
        // TODO
    }


    @Override
    public RecyclerAdapter<Message> getRecyclerAdapter() {
        return mAdapter;
    }

    @Override
    public void onAdapterDataChanged() {
        // 数据改变后刷新布局滚动最底部
        mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // 内容的适配器
    private class Adapter extends RecyclerAdapter<Message> {
        @Override
        protected int getItemViewType(int position, Message message) {

            // 消息为文字的类型，判断发送者的id和当前登录人id是否一致
            // 我发送的信息在右边，收到的在左边
            boolean isRight = Objects.equals(message.getSender().getId(), Account.getUserId());
            switch (message.getType()) {
                // 文字内容
                case Message.TYPE_STR:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;

                // 图片内容
                case Message.TYPE_PIC:
                    return isRight ? R.layout.cell_chat_pic_right : R.layout.cell_chat_pic_left;

                // 语音内容
                case Message.TYPE_AUDIO:
                    return isRight ? R.layout.cell_chat_audio_right : R.layout.cell_chat_audio_left;

                // 文件内容（课后作业）
                default:
                    return isRight ? R.layout.cell_chat_text_right : R.layout.cell_chat_text_left;
            }
        }

        @Override
        protected ViewHolder<Message> onCreateViewHolder(View root, int viewType) {
            switch (viewType) {
                // 左右的布局
                case R.layout.cell_chat_text_right:
                case R.layout.cell_chat_text_left:
                    return new TextHolder(root);

                case R.layout.cell_chat_pic_right:
                case R.layout.cell_chat_pic_left:
                    return new PicHolder(root);

                case R.layout.cell_chat_audio_right:
                case R.layout.cell_chat_audio_left:
                    return new AudioHolder(root);

                // 默认情况下就是返回text的类型进行处理
                // 文件的实现（课后作业）
                default:
                    return new TextHolder(root);
            }
        }
    }

    // Holder的基类
    class BaseHolder extends RecyclerAdapter.ViewHolder<Message> {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;

        @Nullable // 允许为空，左边没有，右边有
        @BindView(R.id.loading)
        Loading mLoading;

        public BaseHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            User sender = message.getSender();
            // 进行数据加载
            sender.load();
            // 头像加载
            mPortraitView.setup(Glide.with(ChatFragment.this), sender);
            if (mLoading != null) {
                // 当前布局应该是右边
                int status = message.getStatus();
                if (status == Message.STATUS_DONE) {
                    // 正常状态,隐藏Loading
                    mLoading.stop();
                    mLoading.setVisibility(View.GONE);
                } else if (status == Message.STATUS_CREATED) {
                    // 正在发送中状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.setProgress(0);
                    mLoading.setForegroundColor(getResources().getColor(R.color.colorAccent, null));
                    mLoading.start();
                } else if (status == Message.STATUS_FAILED) {
                    // 发送失败状态
                    mLoading.setVisibility(View.VISIBLE);
                    mLoading.stop();
                    mLoading.setProgress(1);
                    mLoading.setForegroundColor(getResources().getColor(R.color.alertImportant, null));
                }

                // 当状态是错误的状态下才允许点击
                mPortraitView.setEnabled(status == Message.STATUS_FAILED);
            }
        }

        @OnClick(R.id.im_portrait)
        void onRePushClick() {
            // 重新发送
            if (mLoading != null && mPresenter.rePush(mData)) {
                // 必须是右边才有可能需要重新发送
                updateData(mData);
            }

        }

    }

    // 文字的Holder
    class TextHolder extends BaseHolder {
        @BindView(R.id.txt_content)
        TextView mContent;

        public TextHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // 把内容设置到布局上
            mContent.setText(message.getContent());
        }
    }

    // 图片的Holder
    class PicHolder extends BaseHolder {

        public PicHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }

    // 语音的Holder
    class AudioHolder extends BaseHolder {

        public AudioHolder(View itemView) {
            super(itemView);
        }

        @Override
        protected void onBind(Message message) {
            super.onBind(message);
            // TODO
        }
    }
}
