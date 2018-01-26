package com.im.yutalker.push.activities;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.im.yutalker.common.app.Activity;
import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.common.app.ToolBarActivity;
import com.im.yutalker.push.R;
import com.im.yutalker.push.fragments.search.SearchGroupFragment;
import com.im.yutalker.push.fragments.search.SearchUserFragment;

public class SearchActivity extends ToolBarActivity {
    private static final String EXTRA_TYPE = "EXTRA_TYPE";
    public static final int TYPE_USER = 1; //搜索人
    public static final int TYPE_GROUP = 2;//搜索群

    private int type; // 具体需要显示的类型
    private SearchFragment mSearchFragment;

    /**
     * 显示搜索界面
     *
     * @param context 上下文
     * @param type    搜索类型
     */
    public static void show(Context context, int type) {
        Activity activity = (Activity) context;
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        context.startActivity(intent);
        activity.overridePendingTransition(R.anim.right_to_current,R.anim.current_to_left);
    }

    @Override
    protected boolean initArgs(Bundle bundle) {
        type = bundle.getInt(EXTRA_TYPE);
        // 是搜索人或者搜索群
        return type == TYPE_USER || type == TYPE_GROUP;
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        Fragment fragment;
        if (type == TYPE_USER) {
            SearchUserFragment searchUserFragment = new SearchUserFragment();
            fragment = searchUserFragment;
            mSearchFragment = searchUserFragment;
        } else {
            SearchGroupFragment searchGroupFragment = new SearchGroupFragment();
            fragment = searchGroupFragment;
            mSearchFragment = searchGroupFragment;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.lay_container, fragment)
                .commit();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 初始化菜单
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search, menu);
        // 得到搜索菜单
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        if (searchView != null) {
            // 拿到一个搜索的管理器
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

            // 搜索的监听
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // 点击提交按钮的时候
                    searchQuery(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    searchQuery(query);
                    return true;
                    // 当输入改变的时候，不及时搜索，只在为空的时候搜索
//                    if (TextUtils.isEmpty(query)) {
//                        searchQuery(query);
//                        return true;
//                    }
//                    return false;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 请求搜索的方法
     *
     * @param query 搜索的内容文字
     */
    private void searchQuery(String query) {
        if (mSearchFragment == null)
            return;
        mSearchFragment.search(query);
    }

    /**
     * 搜索的Fragment必须继承的接口
     */
    public interface SearchFragment {
        void search(String query);
    }
}
