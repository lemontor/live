package com.example.user.live.live;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.user.live.Main;
import com.example.user.live.R;
import com.example.user.live.utils.VolleyUtils;

/**
 * Glide.with(this).load(url).transform(new CornersTransform(this,50)).into(iv1);
 * Created by user on 2018/4/23.
 */
public class LiveFragment extends Fragment {

    private LinearLayout layoutWebView;
    private View contentView;
//    private String url = "http://210.12.56.75:8266/mobile/liebiao.html";
    private String url = "http://study.huatec.com/mobile/liebiao.html";
    private String useId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.activity_live,null);
        initUI(contentView);
        initListener();
        return contentView;
    }

    private void initListener() {
        WebView webView = new WebView(getActivity());
        LiveInterface liveInterface = new LiveInterface(getActivity());
        webView.addJavascriptInterface(liveInterface, "Android");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if(!url.contains("")){

                }
                super.onPageStarted(view, url, favicon);
            }
        });
        layoutWebView.addView(webView);
    }

    private void initUI(View contentView) {
        layoutWebView = (LinearLayout) contentView.findViewById(R.id.line_webview);
        Bundle bundle = getArguments();
        if(bundle != null){
            useId = bundle.getString("useId");
        }
    }


    @Override
    public void onDestroy() {
        if(layoutWebView != null){
            layoutWebView.removeAllViews();
        }
        super.onDestroy();
    }


    public class LiveInterface{
        public Context context;
        public  LiveInterface(Context c){
            this.context = c;
        }
        @android.webkit.JavascriptInterface
        public void detail(String liveId){
            Intent intent = new Intent(getActivity(),LiveDetailsActivity.class);
            intent.putExtra("details",liveId);
            startActivity(intent);
        }
    }

}
