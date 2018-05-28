package com.example.user.live.video;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.user.live.R;
import com.example.user.live.view.StaticListView;

/**
 * Created by user on 2018/4/27.
 */
public class VideoUpLoad extends Activity implements View.OnClickListener{

    private StaticListView lvFinish, lvFail;
    private LinearLayout layoutLoading;
    private TextView tvFinishNotify, tvFinishClear;
    private TextView tvFailNotify, tvFailClear;
    private TextView tvLoadNotify, tvLoadClear;
    private TextView tvTitle,tvSet;
    private View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = View.inflate(this,R.layout.activity_video_upload,null);
        setContentView(rootView);
        initUI();
        initListener();
    }

    private void initUI() {
        lvFinish = (StaticListView) findViewById(R.id.lv_finish);
        lvFail = (StaticListView) findViewById(R.id.lv_fail);
        tvLoadNotify = (TextView) findViewById(R.id.tv_notify);
        tvLoadClear = (TextView) findViewById(R.id.tv_clear);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSet = (TextView) findViewById(R.id.tv_set);
        layoutLoading = (LinearLayout) findViewById(R.id.layout_up);
        View finishView = LayoutInflater.from(this).inflate(R.layout.layout_head_upload, null);
        View failView = LayoutInflater.from(this).inflate(R.layout.layout_head_upload, null);
        tvFinishNotify = (TextView) finishView.findViewById(R.id.tv_notify);
        tvFinishClear = (TextView) finishView.findViewById(R.id.tv_clear);
        tvFailNotify = (TextView) failView.findViewById(R.id.tv_notify);
        tvFailClear = (TextView) failView.findViewById(R.id.tv_clear);
        lvFinish.addHeaderView(finishView);
        lvFail.addHeaderView(failView);
        tvLoadNotify.setText("进行中（1）");
        tvLoadClear.setText("全部暂停");
        tvLoadClear.setTextColor(getResources().getColor(R.color.color_34A446));
        tvTitle.setText("任务列表");
        tvSet.setText("");
        Drawable drawable = getResources().getDrawable(R.mipmap.spsc_gengduo);
        tvSet.setCompoundDrawablesWithIntrinsicBounds(drawable,null,null,null);
    }

    private void initListener() {
        tvSet.setOnClickListener(this);
    }

    private PopupWindow mPopNotify;

    private void initPop() {
        if(mPopNotify == null){
            mPopNotify = new PopupWindow(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            View popView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_op, null);
            TextView  tvClearIng  = (TextView) popView.findViewById(R.id.tv_clear_ing);
            TextView  tvReset  = (TextView) popView.findViewById(R.id.tv_reset);
            TextView  tvClearFail = (TextView) popView.findViewById(R.id.tv_clear_fail);
            TextView  tvCancel  = (TextView) popView.findViewById(R.id.tv_cancel);
            tvClearIng.setOnClickListener(this);
            tvReset.setOnClickListener(this);
            tvClearFail.setOnClickListener(this);
            tvCancel.setOnClickListener(this);
            mPopNotify.setContentView(popView);
            mPopNotify.setFocusable(true);
            mPopNotify.setBackgroundDrawable(new PaintDrawable(getResources().getColor(R.color.color_44000000)));
            mPopNotify.setAnimationStyle(R.style.popwin_anim_style);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_clear_ing:
                break;
            case R.id.tv_reset:
                break;
            case R.id.tv_clear_fail:
                break;
            case R.id.tv_cancel:
                mPopNotify.dismiss();
                break;
            case R.id.tv_set:
                initPop();
                mPopNotify.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                break;
        }
    }
}
