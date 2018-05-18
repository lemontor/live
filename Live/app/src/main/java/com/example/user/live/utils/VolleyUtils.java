package com.example.user.live.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.user.live.app.App;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class VolleyUtils {

    private static StringRequest stringRequest;

    private static ImageRequest  imageRequest;

    public VolleyUtils(){

    }


    public static void requestGetMethod(String tag, String url, final OnVolleyListener listener) {
        App.removeVolleyQueue(tag);
        stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_response", response + "");
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("tag_onErrorResponse", error.getMessage() + "");
                listener.onErrorResponse(error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data, "UTF-8");
                    return Response.success(jsonString,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (Exception je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        stringRequest.setTag(tag);
        App.addVolleyQueue(stringRequest);
    }

    public static String getAbsoluteUrl(String relativeUrl) {
        return ConstantUtils.BASE_URL + relativeUrl;

    }


    public static void stringRequestWithPost(String tag, final String url, final Map<String, String> params, final OnVolleyListener listener) {
        App.removeVolleyQueue(tag);
        stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
                Log.e("tag_error", error.getLocalizedMessage() + "   " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headParams = new HashMap<>();
                return headParams;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    Map<String, String> responseHeaders = response.headers;
                    String info = responseHeaders.get("Set-Cookie");
                    Log.e("tag_cookie", info + "  url=" + url);
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        stringRequest.setTag(tag);
        App.addVolleyQueue(stringRequest);

    }


    public interface OnVolleyListener {
        void onResponse(String response);
        void onErrorResponse(VolleyError error);
    }

    /*
    获取评论和在线人数
     */
    public void getContentAndPerson(String videoId, final OnVolleyListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("videoId", videoId);
        params.put("page","1");
        params.put("rows","4");
        String url = ConstantUtils.GET_CONTENT_PERSON_COUNT;
        Log.e("tag_url_volley",url+"");
        stringRequestWithPost(videoId, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);

            }
        });
    }

    /*
    获取评论和在线人数
     */
    public void getAllContentAndPerson(String videoId,String teacherId, final OnVolleyListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("videoId", videoId);
//        params.put("updateUser",teacherId);
        String url = ConstantUtils.GET_ALL_CONTENT_PERSON_COUNT;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_all",response+"");
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("tag_onErrorResponse",error.getMessage()+"");

                listener.onErrorResponse(error);
            }
        });
    }

    /*
   添加评论
    */
    public void addContent(String videoId,String content,String userId, final OnVolleyListener listener) {
        Map<String, String> params = new HashMap<>();
        params.put("videoId", videoId);
        params.put("content", content);
        params.put("createUser",userId);
        params.put("appkeyType", "0");
        String url = ConstantUtils.GET_PUSH_CONTENT;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
    }

    public void checkLogin(String loginId,String passWord, final OnVolleyListener listener){
        Map<String, String> params = new HashMap<>();
        params.put("loginId", loginId);
        params.put("password", passWord);
        params.put("from","youxue");
        String url = ConstantUtils.CHECK_LOGIN;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
    }


    public void  loginForId(String token, final OnVolleyListener listener){
        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        String url = ConstantUtils.LOGIN_ID;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);
            }
        });
    }


    public void getCode(String phone,final OnVolleyListener listener){
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        String url = ConstantUtils.GET_CODE;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);

            }
        });
    }


    public void loginForPhone(String phone,String code,final OnVolleyListener listener){
        Map<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        params.put("mobileCode", code);
        String url = ConstantUtils.LOGIN_PHONE;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);

            }
        });
    }


    public void  getLive(final OnVolleyListener listener){
        Map<String, String> params = new HashMap<>();
        String url =  ConstantUtils.LIVE_LIST;
        stringRequestWithPost(url, url, params, new OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                listener.onResponse(response);

            }

            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorResponse(error);

            }
        });
    }



}
