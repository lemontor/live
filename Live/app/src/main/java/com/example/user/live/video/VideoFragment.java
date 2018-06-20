package com.example.user.live.video;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.example.user.live.R;
import com.example.user.live.app.App;
import com.example.user.live.utils.ConstantUtils;
import com.example.user.live.utils.SharedPreferencesUtils;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.utils.VolleyUtils;
import com.example.user.live.video.adapter.VideoUpedAdapter;
import com.example.user.live.video.entity.VideoUpInfoBean;
import com.example.user.live.video.upload.VideoUpLoadActivity;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/4/24.
 */
public class VideoFragment extends Fragment {

    private View contentView;
    private ListView mLvLive;
    private VolleyUtils volleyUtils;
    private RelativeLayout layoutList;
    private LinearLayout layoutUp;
    SharedPreferencesUtils sharedPreferencesUtils;
    private VideoUpedAdapter upedAdapter;
    private List<VideoUpInfoBean.VideoBean> videoBeanList;
    private VideoUpInfoBean videoUpInfoBean;
    private ImageView ivUp;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateVideo(VideoUpInfoBean.VideoBean  videoBean) {
        if(videoBean != null){
            loadData(sharedPreferencesUtils.getString(ConstantUtils.USER_ID));
            if(App.upCount > 0){
                ivUp.setVisibility(View.VISIBLE);
            }else{
                ivUp.setVisibility(View.GONE);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_video, null);
        EventBus.getDefault().register(this);
        initUI(contentView);
        initObj();
        initListener();
        initData();
        return contentView;
    }

    private void initData() {
        loadData(sharedPreferencesUtils.getString(ConstantUtils.USER_ID));
    }

    private void initListener() {
        mLvLive.setAdapter(upedAdapter);
        layoutList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(App.upCount == 0){
                    ToastUtils.showToast(getActivity(),"暂无视频上传...");
                }else{
                    Intent intent = new Intent(getActivity(), VideoUpLoadActivity.class);
                    intent.putExtra("has",2);
                    startActivity(intent);
                }

            }
        });

        layoutUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoUpInfoBean = new VideoUpInfoBean();
                videoUpInfoBean.setData(videoBeanList);
                EventBus.getDefault().postSticky(videoUpInfoBean);
                Intent intent = new Intent(getActivity(), VideoList.class);
                startActivity(intent);

            }
        });
    }

    private void initObj() {
        volleyUtils = new VolleyUtils();
        sharedPreferencesUtils = new SharedPreferencesUtils(getActivity());
        videoBeanList = new ArrayList<>();
        upedAdapter = new VideoUpedAdapter(getActivity(), videoBeanList);

    }

    private void initUI(View contentView) {
        mLvLive = (ListView) contentView.findViewById(R.id.lv);
        View emptyView = contentView.findViewById(R.id.layout_empty);
        emptyView.setVisibility(View.GONE);
        mLvLive.setEmptyView(emptyView);
        layoutList = (RelativeLayout) contentView.findViewById(R.id.layout_list);
        layoutUp = (LinearLayout) contentView.findViewById(R.id.layout_up);
        ivUp = (ImageView) contentView.findViewById(R.id.iv_up);
    }

    private void loadData(String userId) {
        volleyUtils.getVideoList(userId, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_onResponse", response);
                if (response != null && !response.equals("")) {
                    VideoUpInfoBean videoUpInfoBean = new Gson().fromJson(response, VideoUpInfoBean.class);
                    if (videoUpInfoBean != null) {
                        videoBeanList.clear();
                        videoBeanList.addAll(videoUpInfoBean.getData());
                        upedAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("tag_onErrorResponse", error.getMessage());
            }
        });
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
