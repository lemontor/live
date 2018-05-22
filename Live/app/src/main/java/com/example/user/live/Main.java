package com.example.user.live;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.example.user.live.live.LiveFragment;
import com.example.user.live.video.VideoFragment;

/**
 * Created by user on 2018/4/24.
 */
public class Main extends AppCompatActivity {
    private LiveFragment liveFragment;
    private VideoFragment videoFragment;
    private FragmentManager fragmentManager;
    private Fragment curFragment = new Fragment();
//    private RadioGroup mRgMain;

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
//        mRgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, int i) {
//                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_live) {
//                    showFragment(liveFragment);
//                } else {
//                    showFragment(videoFragment);
//                }
//            }
//        });
    }

    private void initObj() {
        fragmentManager = getSupportFragmentManager();
        liveFragment = new LiveFragment();
        videoFragment = new VideoFragment();
    }

    private void initUI() {
//        mRgMain = (RadioGroup) findViewById(R.id.rg_bottom);
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

}
