package com.example.user.live.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.user.live.Main;
import com.example.user.live.R;
import com.example.user.live.utils.ConstantUtils;
import com.example.user.live.utils.GetVideoDataUtils;
import com.example.user.live.utils.SharedPreferencesUtils;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.utils.VolleyUtils;
import com.example.user.live.video.entity.VideoEntity;
import com.example.user.live.view.LoadDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 2018/4/19.
 */
public class Login extends Activity implements View.OnClickListener {

    private LinearLayout mLayoutCode;
    private EditText mEdtPhone, mEdtCode;
    private TextView mTvCode, mTvLoginCode;
    private LinearLayout mLayoutPassword;
    private EditText mEdtUserName, mEdtPassWord;
    private TextView mTvLoginPassWord, mTvChange;
    private boolean isPhone = false;
    private boolean isPassClick = false;
    private boolean isPhoneClick = false;
    private boolean isClickCode = true;
    private int count = 59;
    private boolean isRun;
    private VolleyUtils volleyUtils;
    private final EventHandler eventHandler = new EventHandler(this);
    private SharedPreferencesUtils sharedPreferencesUtils;
    private LoadDialog loadDialog;

    private static class EventHandler extends Handler {
        private final WeakReference<Login> reference;

        public EventHandler(Login login) {
            reference = new WeakReference<Login>(login);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Login activity = reference.get();
            if (msg.what == 1001) {
                if (activity != null) {
                    if (activity.count > 0) {
                        activity.mTvCode.setText("重新发送" + "(" + activity.count + ")");
                        activity.count--;
                        activity.isRun = true;
                        activity.isClickCode = false;
                        sendEmptyMessageDelayed(1001, 1000);
                    } else {
                        removeMessages(1001);
                        activity.count = 59;
                        activity.mTvCode.setText("获取验证码");
                        activity.mTvCode.setBackgroundResource(R.drawable.layout_border_login_orange);
                        activity.isClickCode = true;
                        activity.isRun = false;
                    }
                }
            } else {
                removeMessages(1001);
                activity.count = 59;
                activity.mTvCode.setText("获取验证码");
                activity.isClickCode = true;
                activity.isRun = false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        initListener();
    }

    private void initListener() {
        volleyUtils = new VolleyUtils();
        loadDialog = new LoadDialog(this);
        sharedPreferencesUtils = new SharedPreferencesUtils(this);
        mEdtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    mTvLoginPassWord.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                    isPassClick = false;
                } else {
                    if (mEdtPassWord.getText().toString().length() >= 6) {
                        mTvLoginPassWord.setBackgroundResource(R.drawable.layout_border_login_btn);
                        isPassClick = true;
                    } else {
                        mTvLoginPassWord.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                        isPassClick = false;
                    }
                }
            }
        });

        mEdtPassWord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEdtUserName.getText().toString().equals("")) {
                    mTvLoginPassWord.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                    isPassClick = false;
                } else {
                    if (editable.toString().length() >= 6) {
                        mTvLoginPassWord.setBackgroundResource(R.drawable.layout_border_login_btn);
                        isPassClick = true;
                    } else {
                        mTvLoginPassWord.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                        isPassClick = false;
                    }
                }
            }
        });

        mEdtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("") || editable.toString().length() < 11) {
                    mTvLoginCode.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                    isPassClick = false;
                } else {
                    if (mEdtCode.getText().toString().length() >= 6) {
                        mTvLoginCode.setBackgroundResource(R.drawable.layout_border_login_btn);
                        isPassClick = true;
                    } else {
                        mTvLoginCode.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                        isPassClick = false;
                    }
                }

            }
        });

        mEdtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (mEdtPhone.getText().toString().trim().equals("") || mEdtPhone.toString().trim().length() < 11) {
                    mTvLoginCode.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                    isPhoneClick = false;
                } else {
                    if (editable.toString().length() >= 6) {
                        mTvLoginCode.setBackgroundResource(R.drawable.layout_border_login_btn);
                        isPhoneClick = true;
                    } else {
                        mTvLoginCode.setBackgroundResource(R.drawable.layout_border_login_btn_normal);
                        isPhoneClick = false;
                    }
                }
            }
        });
    }

    private void initUI() {
        mLayoutCode = (LinearLayout) findViewById(R.id.verifycode_login);
        mEdtPhone = (EditText) findViewById(R.id.phone_number);
        mEdtCode = (EditText) findViewById(R.id.verifycode);
        mTvCode = (TextView) findViewById(R.id.getverifycode);
        mTvLoginCode = (TextView) findViewById(R.id.verfiy_login);
        mLayoutPassword = (LinearLayout) findViewById(R.id.password_login);
        mEdtUserName = (EditText) findViewById(R.id.et_login_account_number);
        mEdtPassWord = (EditText) findViewById(R.id.et_login_password);
        mTvLoginPassWord = (TextView) findViewById(R.id.tv_login);
        mTvChange = (TextView) findViewById(R.id.changeto_verifycode_login);
        mTvCode.setOnClickListener(this);
        mTvLoginCode.setOnClickListener(this);
        mTvLoginPassWord.setOnClickListener(this);
        mTvChange.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeto_verifycode_login:
                if (!isPhone) {
                    mLayoutCode.setVisibility(View.VISIBLE);
                    mLayoutPassword.setVisibility(View.GONE);
                    isPhone = true;
                } else {
                    mLayoutCode.setVisibility(View.GONE);
                    mLayoutPassword.setVisibility(View.VISIBLE);
                    isPhone = false;
                }
                break;
            case R.id.tv_login:
                if (isPassClick) {
                    checkLogin(mEdtUserName.getText().toString().trim(), mEdtPassWord.getText().toString().trim());
                }
                break;
            case R.id.verfiy_login:
                if (isPhoneClick) {
                    Log.e("tag_pass_login", "能手机登录了");
                    loginForPhone(mEdtPhone.getText().toString().trim(), mEdtCode.getText().toString().trim());
                }
                break;
            case R.id.getverifycode:
                if (isClickCode) {
                    if (mEdtPhone.getText().toString().equals("")) {
                        ToastUtils.showToast(Login.this, "手机号不能为空");
                    } else {
                        if (checkPhone(mEdtPhone.getText().toString().trim())) {
                            getCode(mEdtPhone.getText().toString().trim());
                        } else {
                            ToastUtils.showToast(Login.this, "请输入正确的手机号");
                        }
                    }
                }
                break;
        }
    }

    //检测手机号
    private boolean checkPhone(String strPhone) {
        String re = "^\\d{11}$";
        Pattern pat = Pattern.compile(re);
        Matcher matcher = pat.matcher(strPhone);
        if (!matcher.find()) {
            return false;
        } else {
            return true;
        }
    }

    private String checkResponse(String response) {
        String re = "^\\((.*)\\)$";
        Pattern pat = Pattern.compile(re);
        Matcher matcher = pat.matcher(response);
        if (!matcher.find()) {
            return response;
        } else {
            return matcher.group(1);
        }
    }


    /*
    登录认证
     */
    public void checkLogin(String loginId, String passWord) {
        loadDialog.showDialog();
        volleyUtils.checkLogin(loginId, passWord, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("")) {
                    String reStr = checkResponse(response);
                    try {
                        JSONObject jsonObject = new JSONObject(reStr);
                        int code = jsonObject.getInt("code");
                        String msg = jsonObject.getString("msg");
                        String token = jsonObject.getString("token");
                        if (code == 200) {
                            sharedPreferencesUtils.putString(ConstantUtils.TOKEN, token);
                            loginForId(token);
                        } else {
                            initDialog(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (loadDialog.isShow()) {
                    loadDialog.dismissDialog();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (loadDialog.isShow()) {
                    loadDialog.dismissDialog();
                }
            }
        });
    }

    /*
    用户名登录
     */
    public void loginForId(String token) {
        volleyUtils.loginForId(token, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_login", response + "");
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            JSONObject childJson = jsonObject.getJSONObject("data");
                            if (childJson != null) {
                                String dep_id = childJson.getString("dep_id");
                                String user_id = childJson.getString("user_id");
                                sharedPreferencesUtils.putString(ConstantUtils.DEP_ID, dep_id);
                                sharedPreferencesUtils.putString(ConstantUtils.USER_ID, user_id);
                                Intent intent = new Intent(Login.this, Main.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            String msg = jsonObject.getString("msg");
                            initDialog(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (loadDialog.isShow()) {
                    loadDialog.dismissDialog();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                if (loadDialog.isShow()) {
                    loadDialog.dismissDialog();
                }
            }
        });
    }

    /*
    获取短信验证码
     */
    public void getCode(String phone) {
        volleyUtils.getCode(phone, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_code", response + "");
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        String msg = jsonObject.getString("msg");
                        if (code == 0) {
                            eventHandler.sendEmptyMessageDelayed(1001, 1000);
                            mTvCode.setBackgroundResource(R.drawable.layout_border_login_orange_normal);
                        } else {
                            ToastUtils.showToast(Login.this, msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    /*
    短信登录
     */
    public void loginForPhone(String phone, String code) {
        volleyUtils.loginForPhone(phone, code, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                if (response != null && !response.equals("")) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        if (code == 0) {
                            JSONObject childJson = jsonObject.getJSONObject("data");
                            if (childJson != null) {
                                String dep_id = childJson.getString("dep_id");
                                String user_id = childJson.getString("user_id");
                                sharedPreferencesUtils.putString(ConstantUtils.DEP_ID, dep_id);
                                sharedPreferencesUtils.putString(ConstantUtils.USER_ID, user_id);
                                Intent intent = new Intent(Login.this, Main.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            String msg = jsonObject.getString("msg");
                            initDialog(msg);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }


    public void initDialog(String notify) {
        View dialogView = View.inflate(this, R.layout.dialog_notify, null);
        TextView tvNotify = (TextView) dialogView.findViewById(R.id.tv_notify);
        TextView tvSure = (TextView) dialogView.findViewById(R.id.tv_sure);
        TextView tvCancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        tvNotify.setText(notify);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}
