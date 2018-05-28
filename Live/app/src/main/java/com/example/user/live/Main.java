package com.example.user.live;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.example.user.live.live.LiveFragment;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.video.VideoFragment;

/**
 * Created by user on 2018/4/24.
 */
public class Main extends AppCompatActivity {

    private LiveFragment liveFragment;
    private VideoFragment videoFragment;
    private FragmentManager fragmentManager;
    private Fragment curFragment = new Fragment();
    private TextView tvLive, tvVideo;
    private String useId;
    private Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_show);
        initUI();
        initObj();
        initListener();
        showFragment(liveFragment);
    }

    private void initListener() {
        tvLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLive.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                tvLive.setTextColor(getResources().getColor(R.color.color_34A446));
                tvVideo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tvVideo.setTextColor(getResources().getColor(R.color.color_666666));
                showFragment(liveFragment);
            }
        });

        tvVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvLive.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                tvLive.setTextColor(getResources().getColor(R.color.color_666666));
                tvVideo.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                tvVideo.setTextColor(getResources().getColor(R.color.color_34A446));
                showFragment(videoFragment);
            }
        });

    }

    private void initObj() {
        useId = getIntent().getStringExtra("useId");
        fragmentManager = getSupportFragmentManager();
        liveFragment = new LiveFragment();
        videoFragment = new VideoFragment();
        Bundle bundle = new Bundle();
        bundle.putString("useId", useId);
        liveFragment.setArguments(bundle);
        videoFragment.setArguments(bundle);
    }

    private void initUI() {
        tvLive = (TextView) findViewById(R.id.rb_live);
        tvVideo = (TextView) findViewById(R.id.rv_video);
        drawable = getResources().getDrawable(R.drawable.rectangle);
    }

    private void showFragment(Fragment fragment) {
        if (curFragment != fragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(curFragment);
            curFragment = fragment;
            if (!fragment.isAdded()) {
                transaction.add(R.id.layout_content, fragment).show(fragment).commit();
            } else {
                transaction.show(fragment).commit();
            }
        }
    }

    private long exitTime; //记录手机返回按键的时间间隔

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if ((java.lang.System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(getApplicationContext(), "再按一次退出程序");
                exitTime = java.lang.System.currentTimeMillis();
            } else {
                int pid = android.os.Process.myPid();    //获取当前应用程序的PID
                android.os.Process.killProcess(pid);    //杀死当前进程
//                HuatecApplication.getInstance().exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
