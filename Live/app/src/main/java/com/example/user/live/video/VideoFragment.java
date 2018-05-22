package com.example.user.live.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.user.live.R;
import com.example.user.live.utils.VolleyUtils;

/**
 * Created by user on 2018/4/24.
 */
public class VideoFragment extends Fragment {

    private View contentView;
    private ListView mLvLive;
    private VolleyUtils volleyUtils;
    private LinearLayout layoutList,layoutUp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_video,null);
        initUI(contentView);
        initObj();
        initListener();
        initData();
        return contentView;
    }

    private void initData() {
    }

    private void initListener() {
        layoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        layoutUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initObj() {
        volleyUtils = new VolleyUtils();
    }

    private void initUI(View contentView) {
        mLvLive = (ListView) contentView.findViewById(R.id.lv);
        View emptyView =  contentView.findViewById(R.id.layout_empty);
        emptyView.setVisibility(View.GONE);
        mLvLive.setEmptyView(emptyView);
        layoutList = (LinearLayout) contentView.findViewById(R.id.layout_list);
        layoutUp = (LinearLayout) contentView.findViewById(R.id.layout_up);
    }
}
