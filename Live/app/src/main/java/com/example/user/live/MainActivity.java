package com.example.user.live;

import android.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import com.android.volley.VolleyError;
import com.example.user.live.bean.BackBean;
import com.example.user.live.bean.CommentBean;
import com.example.user.live.bean.CustomMessage;
import com.example.user.live.jpush.utils.ExampleUtil;
import com.example.user.live.live.DemoActivity;
import com.example.user.live.live.LiveCameraActivity;
import com.example.user.live.utils.ConstantUtils;
import com.example.user.live.utils.SharedPreferencesUtils;
import com.example.user.live.utils.VolleyUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends AppCompatActivity {

    private LinearLayout  mLayoutContent;
    private WebView  mWebView;
//    String targetUrl = "http://210.12.56.75:8777/mobile/index.html";
//    String targetUrl = "http://210.12.56.75:8266/mobile/index.html";
    String targetUrl = "http://study.huatec.com/mobile/index.html";
    private SharedPreferencesUtils sharedPreferencesUtils;
    private String[] permissions = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<String> mPermissionList;
    private static int FIND_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        mPermissionList = new ArrayList<>();
        WebSettings  webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);//支持与javascript交互
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过js打开新窗口
        //设置自适应屏幕，两者合用
        mWebView.setWebChromeClient(new WebChromeClient());
        webSetting.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSetting.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSetting.setDefaultTextEncodingName("utf-8");
        initListener();
        EventBus.getDefault().register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for(String str : permissions){
                if(ContextCompat.checkSelfPermission(this,str) != PackageManager.PERMISSION_GRANTED){
                    mPermissionList.add(str);
                }
            }
            if(!mPermissionList.isEmpty()){
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, FIND_PERMISSIONS);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initListener() {


        AppInterface appInterface = new AppInterface(this);
        mWebView.addJavascriptInterface(appInterface,"Android");

        mWebView.loadUrl(targetUrl);
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("tag_url",url+"");
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("tag_errorCode",errorCode+"");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.e("tag_url",url+"");
                super.onPageStarted(view, url, favicon);
            }


            //            onReceivedError()
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(BackBean backBean) {
        Log.e("tag_info","onMessage");
        goBack();
    }

    private  void  initUI(){
        sharedPreferencesUtils = new SharedPreferencesUtils(this);
        mLayoutContent = (LinearLayout) findViewById(R.id.layout_content);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayoutContent.addView(mWebView);
    }



    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public class AppInterface{

        public Context  context;

        public  AppInterface(Context c){
            this.context = c;
        }

        @android.webkit.JavascriptInterface
        public void login(String useId){
            Log.e("tag_login","login");
            sharedPreferencesUtils.putString(ConstantUtils.USER_ID,useId);
            Intent  intent = new Intent(MainActivity.this,Main.class);
            intent.putExtra("useId",useId);
            startActivity(intent);
            finish();
        }
    }

//
//
//    public class WebAppInterface{
//
//        public Context  context;
//
//        public  WebAppInterface(Context c){
//            this.context = c;
//        }
//        //String pushUrl,String liveId,String teacherName,String img
//        @android.webkit.JavascriptInterface
//        public void openLive(String pushUrl,String liveId,String teacherName,String img,String teacherId){
//              Log.e("tag_","open_live");
//            Log.e("tag_openLive",pushUrl+"_"+liveId+"_"+teacherName+"_"+img+"_"+teacherId);
//            Intent  intent = new Intent(MainActivity.this, LiveCameraActivity.class);
//            Bundle  bundle = new Bundle();
//            bundle.putString("pushUrl",pushUrl);
//            bundle.putString("liveId",liveId);
//            bundle.putString("teacherName",teacherName);
//            bundle.putString("img",img);
//            bundle.putString("teacherId",teacherId);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//    }

    public  void  goBack(){
        if(mWebView != null && mWebView.canGoBack()){
           mWebView.goBack();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == FIND_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
                    if (showRequestPermission) {
                        showDialog();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 设置参数
        builder.setTitle("注意")
                .setMessage("不允许需要的权限，部分功能将会无法实现！")
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {// 积极
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                    }
                });
        builder.create().show();
    }

}
