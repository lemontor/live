package com.example.user.live.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.example.user.live.R;
import com.example.user.live.utils.VolleyUtils;

/**
 * Glide.with(this).load(url).transform(new CornersTransform(this,50)).into(iv1);
 * Created by user on 2018/4/23.
 */
public class LiveFragment extends Fragment {

    private View contentView;
    private ListView mLvLive;
    private VolleyUtils volleyUtils;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_live,null);
        initUI(contentView);
        initObj();
        initListener();
        initData();
        return contentView;
    }

    private void initData() {
        loadLiveData();
    }

    private void initListener() {
    }

    private void initObj() {
        volleyUtils = new VolleyUtils();
    }

    private void initUI(View contentView) {
        mLvLive = (ListView) contentView.findViewById(R.id.lv);
        View emptyView =  contentView.findViewById(R.id.layout_empty);
        mLvLive.setEmptyView(emptyView);
    }


    private void  loadLiveData(){
        volleyUtils.getLive(new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_live",response+"");
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("tag_liveonErrorResponse",error.getMessage()+"");

            }
        });
    }

}
