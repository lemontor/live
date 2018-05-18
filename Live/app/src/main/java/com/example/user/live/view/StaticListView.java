package com.example.user.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by 123 on 2016/5/12.
 */
public class StaticListView extends ListView {
    private Context context;
    public StaticListView(Context context) {
        super(context);
        this.context = context;
    }

    public StaticListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

//    @Override
//    public void setAdapter(ListAdapter adapter) {
//        super.setAdapter(adapter);
//        View headView = View.inflate(context, R.layout.layout_head_order,null);
//        super.addHeaderView(headView);
//        View  footView = View.inflate(context,R.layout.layout_foot_order,null);
//        super.addFooterView(footView);
//    }


    //    @Override
//    public void addHeaderView(View v) {
//        View  headView = View.inflate(context, R.layout.layout_head_order,null);
//        super.addHeaderView(headView);
//
//    }


//    @Override
//    public void addFooterView(View v) {
//        super.addFooterView(v);
//        View  footView = View.inflate(context,R.layout.layout_foot_order,null);
//        addFooterView(footView);
//    }
}
