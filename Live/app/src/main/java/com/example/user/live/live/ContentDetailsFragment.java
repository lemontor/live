package com.example.user.live.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.user.live.R;

/**
 * Created by user on 2018/4/24.
 */
public class ContentDetailsFragment  extends Fragment{

    private ListView mLv;
    private View  mLvContent;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLvContent = inflater.inflate(R.layout.layout_live_content,null);
        return mLvContent;
    }
}
