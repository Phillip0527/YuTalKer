package com.im.yutalker.push.fragments.search;


import android.media.Image;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.app.PresenterFragment;
import com.im.yutalker.common.widget.EmptyView;
import com.im.yutalker.common.widget.PortraitView;
import com.im.yutalker.common.widget.recycler.RecyclerAdapter;
import com.im.yutalker.factory.model.card.UserCard;
import com.im.yutalker.factory.presenter.contact.FollowContract;
import com.im.yutalker.factory.presenter.contact.FollowPresenter;
import com.im.yutalker.factory.presenter.search.SearchContract;
import com.im.yutalker.factory.presenter.search.SearchUserPresenter;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.SearchActivity;

import net.qiujuer.genius.ui.Ui;
import net.qiujuer.genius.ui.compat.UiCompat;
import net.qiujuer.genius.ui.drawable.LoadingCircleDrawable;
import net.qiujuer.genius.ui.drawable.LoadingDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchUserFragment extends PresenterFragment<SearchContract.Presenter> implements SearchActivity.SearchFragment, SearchContract.SearchUserView {

    @BindView(R.id.recycler)
    RecyclerView mRecyclerView;

    private RecyclerAdapter<UserCard> mAdapter;

    @BindView(R.id.empty)
    EmptyView mEmptyView;

    public SearchUserFragment() {
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_user;
    }

    @Override
    protected void initWidget(View root) {
        super.initWidget(root);
        // 初始化Recycler
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter = new RecyclerAdapter<UserCard>() {
            @Override
            protected int getItemViewType(int position, UserCard userCard) {
                return R.layout.cell_search_list;
            }

            @Override
            protected ViewHolder<UserCard> onCreateViewHolder(View root, int viewType) {
                return new SearchUserFragment.ViewHolder(root);
            }
        });

        // 初始化占位布局
        mEmptyView.bind(mRecyclerView);
        setPlaceHolderView(mEmptyView);
    }

    @Override
    protected void initData() {
        super.initData();
        // 第一次搜索
        search("");
    }

    @Override
    public void search(String query) {
        // activity->fragment->presenter->net
        mPresenter.search(query);
    }

    @Override
    public void onSearchDone(List<UserCard> userCards) {
        // 搜索成功的情况下返回数据
        mAdapter.replace(userCards);
        // 如果有数据则OK，没有数据显示空布局
        placeHolderView.triggerOkOrEmpty(mAdapter.getItemCount() > 0);
    }

    @Override
    protected SearchContract.Presenter initPresenter() {
        // 初始化presenter
        return new SearchUserPresenter(this);
    }

    /**
     * 每一个cell布局操作
     */
    class ViewHolder extends RecyclerAdapter.ViewHolder<UserCard>
            implements FollowContract.View {
        @BindView(R.id.im_portrait)
        PortraitView mPortraitView;
        @BindView(R.id.txt_name)
        TextView mName;
        @BindView(R.id.im_follow)
        ImageView mFollow;

        private FollowContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            new FollowPresenter(this);
        }

        @Override
        protected void onBind(UserCard userCard) {
            Glide.with(SearchUserFragment.this)
                    .load(userCard.getPortrait())
                    .centerCrop()
                    .into(mPortraitView);
            mName.setText(userCard.getName());
            mFollow.setEnabled(!userCard.isFollow());
        }

        @OnClick(R.id.im_follow)
        void onFollowClick() {
            // 发起关注
            mPresenter.follow(mData.getId());
        }

        @Override
        public void showError(int str) {
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                // 失败则停止动画，并设置一个圆圈
                LoadingDrawable drawable = ((LoadingDrawable) mFollow.getDrawable());
                drawable.setProgress(1);
                drawable.stop();
            }
        }

        @Override
        public void showLoading() {
            int minSize = (int) Ui.dipToPx(getResources(), 22);
            int maxSize = (int) Ui.dipToPx(getResources(), 30);
            // 初始化一个圆形动画drawable
            LoadingDrawable drawable = new LoadingCircleDrawable(minSize, maxSize);
            drawable.setBackgroundColor(0);
            int[] color = new int[]{UiCompat.getColor(getResources(), R.color.white_alpha_208)};
            drawable.setForegroundColor(color);
            // 把动画设置进控件
            mFollow.setImageDrawable(drawable);
            // 启动动画
            drawable.start();

        }

        @Override
        public void setPresenter(FollowContract.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onFollowSucceed(UserCard userCard) {
            if (mFollow.getDrawable() instanceof LoadingDrawable) {
                ((LoadingDrawable) mFollow.getDrawable()).stop();
                // 设置为默认的
                mFollow.setImageResource(R.drawable.sel_opt_done_add);
            }
            // 发起更新
            updateData(userCard);
        }
    }
}
