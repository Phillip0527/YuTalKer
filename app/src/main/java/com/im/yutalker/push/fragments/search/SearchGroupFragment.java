package com.im.yutalker.push.fragments.search;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.im.yutalker.common.app.Fragment;
import com.im.yutalker.push.R;
import com.im.yutalker.push.activities.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends Fragment implements SearchActivity.SearchFragment {


    public SearchGroupFragment() {
        // Required empty public constructor
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String query) {

    }
}
