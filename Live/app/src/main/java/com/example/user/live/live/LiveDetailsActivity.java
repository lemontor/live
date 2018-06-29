package com.example.user.live.live;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.user.live.R;

/**
 * Created by user on 2018/6/19.
 */
public class LiveDetailsActivity extends Activity {

    private WebView webView;
    private String url = "http://study.huatec.com/mobile/jianjie.html?id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_details_activity);
        webView = (WebView) findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        String id = getIntent().getStringExtra("details");
        url = url + id;
        Log.e("tag_url", url);
        WebAppInterface webAppInterface = new WebAppInterface(this);
        BackAppInterface backAppInterface = new BackAppInterface(this);
        webView.addJavascriptInterface(webAppInterface, "Android");
        webView.addJavascriptInterface(backAppInterface, "And");
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
    }

    public class WebAppInterface {

        public Context context;

        public WebAppInterface(Context c) {
            this.context = c;
        }

        //String pushUrl,String liveId,String teacherName,String img
        @android.webkit.JavascriptInterface
        public void openLive(String pushUrl, String liveId, String teacherName, String img, String teacherId) {
            Log.e("tag_", "open_live");
            Log.e("tag_openLive", pushUrl + "_" + liveId + "_" + teacherName + "_" + img + "_" + teacherId);
            Intent intent = new Intent(LiveDetailsActivity.this, LiveCameraActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("pushUrl", pushUrl);
            bundle.putString("liveId", liveId);
            bundle.putString("teacherName", teacherName);
            bundle.putString("img", img);
            bundle.putString("teacherId", teacherId);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }


    public class BackAppInterface {

        public Context context;

        public BackAppInterface(Context c) {
            this.context = c;
        }

        @android.webkit.JavascriptInterface
        public void backLive() {
            finish();
        }
    }

}
