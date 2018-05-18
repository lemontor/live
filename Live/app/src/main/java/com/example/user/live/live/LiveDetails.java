package com.example.user.live.live;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ViewUtils;
import android.widget.LinearLayout;

import com.example.user.live.R;
import com.example.user.live.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2018/4/24.
 */
public class LiveDetails extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_details);
        initUI();
    }
    ArrayList<Fragment> fragments;

    private void initUI() {
        fragments = new ArrayList<>();
        fragments.add(new LiveDetailsFragment());
        fragments.add(new ContentDetailsFragment());
        viewPager = (ViewPager) findViewById(R.id.vp);
        viewPager.setAdapter(new FragmentPAdapter(this,
                getSupportFragmentManager(), fragments));
        tabLayout = (TabLayout) findViewById(R.id.tool_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.getTabAt(0).setText("直播简介");
        tabLayout.getTabAt(1).setText("评论");
        LinearLayout linearLayout = (LinearLayout) tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.drawable_layout));
        linearLayout.setDividerPadding(ToastUtils.dpToPx(getResources(), 10));
    }

    //构造器
    private class FragmentPAdapter extends FragmentPagerAdapter {
        //
        public FragmentPAdapter(LiveDetails live, FragmentManager supportFragmentManager, List<Fragment> fragments) {
            super(supportFragmentManager);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

    }

}
