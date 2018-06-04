package com.example.user.live.live;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.livecloud.event.AlivcEvent;
import com.alibaba.livecloud.event.AlivcEventResponse;
import com.alibaba.livecloud.event.AlivcEventSubscriber;
import com.alibaba.livecloud.live.AlivcMediaFormat;
import com.alibaba.livecloud.live.AlivcMediaRecorder;
import com.alibaba.livecloud.live.AlivcMediaRecorderFactory;
import com.alibaba.livecloud.live.AlivcRecordReporter;
import com.alibaba.livecloud.live.AlivcStatusCode;
import com.alibaba.livecloud.live.OnLiveRecordErrorListener;
import com.alibaba.livecloud.live.OnNetworkStatusListener;
import com.alibaba.livecloud.live.OnRecordStatusListener;
import com.alibaba.livecloud.model.AlivcWatermark;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.user.live.MainActivity;
import com.example.user.live.R;
import com.example.user.live.app.App;
import com.example.user.live.bean.AllCommonCountBean;
import com.example.user.live.bean.BackBean;
import com.example.user.live.bean.CommentBean;
import com.example.user.live.bean.CustomMessage;
import com.example.user.live.bean.StopBean;
import com.example.user.live.jpush.utils.ExampleUtil;
import com.example.user.live.utils.SoftKeyboardStateHelper;
import com.example.user.live.utils.ToastUtils;
import com.example.user.live.utils.VolleyUtils;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class LiveCameraActivity extends Activity implements View.OnClickListener {

    private boolean isBeauty = false;
    private VolleyUtils volleyUtils;

    private PopupWindow mPopWindow;
    private CircleImageView circleImageView;
    private TextView mTvName, mTvPerCount, mTvConCount, mTvBack;
    private ProgressDialog mProgressDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_content:
                mEdtLayout.setVisibility(View.VISIBLE);
                mMenuLayout.setVisibility(View.GONE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showKeyboard(mEdtContent);
                    }
                }, 900);
                break;
            case R.id.iv_beaty:
//                if (!isBeauty) {
//                    mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
//                    isBeauty = true;
//                } else {
//                    mMediaRecorder.removeFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
//                    isBeauty = false;
//                }

                break;
            case R.id.iv_camera://转换摄像头
                int currFacing = mMediaRecorder.switchCamera();
                if (currFacing == AlivcMediaFormat.CAMERA_FACING_FRONT) {
                    mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
                }
                mConfigure.put(AlivcMediaFormat.KEY_CAMERA_FACING, currFacing);
                break;
            case R.id.iv_cancel:
                showEditDialog(LiveCameraActivity.this, "确定要退出直播吗?", 0);
                break;
            case R.id.tv_send://发送
                String content = mEdtContent.getText().toString().trim();
                if (!"".equals(content)) {
                    if (content.length() > 200) {
                        showEditDialog(LiveCameraActivity.this, "最多输入200个字，\n请重新输入", 1);
                    } else {
                        setContent(liveId, content, teacherId);
                    }
                } else {
                    Toast.makeText(LiveCameraActivity.this, "请输入信息", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_back:
                EventBus.getDefault().post(new BackBean());
                mPopWindow.dismiss();
                LiveCameraActivity.this.finish();
                break;
        }
    }


    private static final String TAG = "AlivcLiveDemo";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] permissionManifest = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private final int PERMISSION_DELAY = 100;
    private boolean mHasPermission = false;

    private SurfaceView _CameraSurface;
    private AlivcMediaRecorder mMediaRecorder;
    private AlivcRecordReporter mRecordReporter;

    private Surface mPreviewSurface;
    private Map<String, Object> mConfigure = new HashMap<>();
    private boolean isRecording = false;
    private int mPreviewWidth = 0;
    private int mPreviewHeight = 0;
    private DataStatistics mDataStatistics = new DataStatistics(1000);
    private ImageView mIvContent, mIvBeaty, mIvOrientation, mIvClose;
    private ListView mLvContent;
    private TextView mTvPersonCount;
    private RelativeLayout mEdtLayout;
    private RelativeLayout mMenuLayout;
    private EditText mEdtContent;
    private TextView mTvSend;
    private String liveId;
    private String teacherName;
    private String teacherId;
    private String img;
    private List<CommentBean.commentListBean> commentList;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_camera_real);
        if (Build.VERSION.SDK_INT >= 23) {
            permissionCheck();
        } else {
            mHasPermission = true;
        }
        EventBus.getDefault().register(this);
        setInit();
        initView();
        getExtraData();


        setRequestedOrientation(screenOrientation ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //采集
        _CameraSurface = (SurfaceView) findViewById(R.id.camera_surface);
        _CameraSurface.getHolder().addCallback(_CameraSurfaceCallback);
        _CameraSurface.setOnTouchListener(mOnTouchListener);

        //对焦，缩放
        mDetector = new GestureDetector(_CameraSurface.getContext(), mGestureDetector);
        mScaleDetector = new ScaleGestureDetector(_CameraSurface.getContext(), mScaleGestureListener);

        mMediaRecorder = AlivcMediaRecorderFactory.createMediaRecorder();
        mMediaRecorder.init(this);
//        mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
        mDataStatistics.setReportListener(mReportListener);

        /**
         * this method only can be called after mMediaRecorder.init(),
         * else will return null;
         */
        mRecordReporter = mMediaRecorder.getRecordReporter();

        mDataStatistics.start();
        mMediaRecorder.setOnRecordStatusListener(mRecordStatusListener);
        mMediaRecorder.setOnNetworkStatusListener(mOnNetworkStatusListener);
        mMediaRecorder.setOnRecordErrorListener(mOnErrorListener);

        mConfigure.put(AlivcMediaFormat.KEY_CAMERA_FACING, cameraFrontFacing);
        mConfigure.put(AlivcMediaFormat.KEY_MAX_ZOOM_LEVEL, 3);
        mConfigure.put(AlivcMediaFormat.KEY_OUTPUT_RESOLUTION, resolution);
        mConfigure.put(AlivcMediaFormat.KEY_MAX_VIDEO_BITRATE, maxBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_BEST_VIDEO_BITRATE, bestBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_MIN_VIDEO_BITRATE, minBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_INITIAL_VIDEO_BITRATE, initBitrate * 1000);
        mConfigure.put(AlivcMediaFormat.KEY_DISPLAY_ROTATION, screenOrientation ? AlivcMediaFormat.DISPLAY_ROTATION_90 : AlivcMediaFormat.DISPLAY_ROTATION_0);
        mConfigure.put(AlivcMediaFormat.KEY_EXPOSURE_COMPENSATION, -1);//曝光度
        mConfigure.put(AlivcMediaFormat.KEY_WATERMARK, mWatermark);
        mConfigure.put(AlivcMediaFormat.KEY_FRAME_RATE, frameRate);

        onKeyBoardListener();
        volleyUtils = new VolleyUtils();
        //获取评论
        getContent();
        registerMessageReceiver(liveId);

    }


    private String pushUrl;
//    private int resolution = AlivcMediaFormat.OUTPUT_RESOLUTION_360P;
    private int resolution = AlivcMediaFormat.OUTPUT_RESOLUTION_720P;
    private boolean screenOrientation = false;
    //    private int cameraFrontFacing = 0;
    private int cameraFrontFacing = 1;
    private AlivcWatermark mWatermark;
    private int bestBitrate = 600;
    private int minBitrate = 500;
    private int maxBitrate = 800;
    private int initBitrate = 600;
    private int frameRate = 30;
    private String watermarkUrl = "assets://Alivc/wartermark/qupai-logo.png";
    private int paddingX = 14;
    private int paddingY = 14;
    private int site = 1;

    private View rootView;

    private void getExtraData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            pushUrl = bundle.getString("pushUrl");
            Log.e("tag_pushUrl===", pushUrl + "");
            liveId = bundle.getString("liveId");
            teacherName = bundle.getString("teacherName");
            img = bundle.getString("img");
            teacherId = bundle.getString("teacherId");
            mWatermark = new AlivcWatermark.Builder()
                    .watermarkUrl(watermarkUrl)
                    .paddingX(paddingX)
                    .paddingY(paddingY)
                    .site(site)
                    .build();
        }
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList, teacherName);
        mLvContent.setAdapter(commentAdapter);

        mTvName.setText(teacherName);


        ImageRequest imageRequest = new ImageRequest(
                img,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        circleImageView.setImageBitmap(response);
                    }
                }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                circleImageView.setImageResource(R.mipmap.ic_launcher);
            }
        });
        App.addVolleyQueue(imageRequest);
    }

    private void permissionCheck() {
        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (String permission : permissionManifest) {
            if (PermissionChecker.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionCheck = PackageManager.PERMISSION_DENIED;
            }
        }
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissionManifest, PERMISSION_REQUEST_CODE);
        } else {
            mHasPermission = true;
        }
    }

    private void initView() {
        rootView = View.inflate(this, R.layout.activity_live_camera_real, null);
        mEdtLayout = (RelativeLayout) findViewById(R.id.edt_layout);

        mMenuLayout = (RelativeLayout) findViewById(R.id.menu_layout);

        mIvContent = (ImageView) findViewById(R.id.iv_content);
        mIvContent.setOnClickListener(this);

        mIvBeaty = (ImageView) findViewById(R.id.iv_beaty);
        mIvBeaty.setOnClickListener(this);

        mIvOrientation = (ImageView) findViewById(R.id.iv_camera);
        mIvOrientation.setOnClickListener(this);

        mIvClose = (ImageView) findViewById(R.id.iv_cancel);
        mIvClose.setOnClickListener(this);

        mLvContent = (ListView) findViewById(R.id.lv_content);
        mTvPersonCount = (TextView) findViewById(R.id.tv_count);
        mEdtContent = (EditText) findViewById(R.id.edt_content);
        mTvSend = (TextView) findViewById(R.id.tv_send);
        mTvSend.setOnClickListener(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mPopWindow = new PopupWindow(dm.widthPixels, dm.heightPixels);
        View v1 = View.inflate(this, R.layout.pop_finish, null);
        circleImageView = (CircleImageView) v1.findViewById(R.id.circle_iv);
        mTvName = (TextView) v1.findViewById(R.id.tv_name);
        mTvPerCount = (TextView) v1.findViewById(R.id.tv_per_count);
        mTvConCount = (TextView) v1.findViewById(R.id.tv_cnt_count);
        mTvBack = (TextView) v1.findViewById(R.id.tv_back);
        mTvBack.setOnClickListener(this);

        mPopWindow.setContentView(v1);
        mPopWindow.setFocusable(true);
        mPopWindow.setBackgroundDrawable(new PaintDrawable(getResources().getColor(R.color.color_66000000)));
        mPopWindow.setAnimationStyle(R.style.popwin_anim_style);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreviewSurface != null) {
            mMediaRecorder.prepare(mConfigure, mPreviewSurface);
            Log.d("AlivcMediaRecorder", " onResume==== isRecording =" + isRecording + "=====");
        }
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_BITRATE_DOWN, mBitrateDownRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_BITRATE_RAISE, mBitrateUpRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_SUCC, mAudioCaptureSuccRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_DATA_DISCARD, mDataDiscardRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_INIT_DONE, mInitDoneRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_SUCC, mVideoEncoderSuccRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_FAILED, mVideoEncoderFailedRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_VIDEO_ENCODED_FRAMES_FAILED, mVideoEncodeFrameFailedRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_AUDIO_ENCODED_FRAMES_FAILED, mAudioEncodeFrameFailedRes));
        mMediaRecorder.subscribeEvent(new AlivcEventSubscriber(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_FAILED, mAudioCaptureOpenFailedRes));
        if (!isRecording) {
            try {
                mMediaRecorder.startRecord(pushUrl);
            } catch (Exception e) {
            }
            isRecording = true;
        }
    }

    @Override
    protected void onPause() {
        if (isRecording) {
            mMediaRecorder.stopRecord();
            isRecording = false;
        }
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_BITRATE_DOWN);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_BITRATE_RAISE);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_SUCC);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_DATA_DISCARD);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_INIT_DONE);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_SUCC);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_VIDEO_ENCODER_OPEN_FAILED);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_VIDEO_ENCODED_FRAMES_FAILED);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_AUDIO_ENCODED_FRAMES_FAILED);
        mMediaRecorder.unSubscribeEvent(AlivcEvent.EventType.EVENT_AUDIO_CAPTURE_OPEN_FAILED);
        /**
         * 如果要调用stopRecord和reset()方法，则stopRecord（）必须在reset之前调用，否则将会抛出IllegalStateException
         */
        mMediaRecorder.reset();
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(CustomMessage message) {
        if (null != message) {
            Log.e("tag_message==finish==", message.getMessage().toString() + "");
            try {
                JSONObject jsonObject = new JSONObject(message.getMessage().toString());
                String comment = jsonObject.getString("comment");
                int people = jsonObject.getInt("people");
                if (people == -100) {
                    if (liveId != null) {
                        if (liveId.equals(comment)) {
                            mMediaRecorder.stopRecord();
                            isRecording = false;
                            ToastUtils.showToast(LiveCameraActivity.this,"您当前的直播已被关闭");
                        }
                    }
                } else {
                    if (!"".equals(comment)) {
                        CommentBean.commentListBean tempComment = new CommentBean.commentListBean();
                        tempComment.setNameContent(comment);
                        if (commentList.size() > 0) {
                            if (commentList.size() >= 4) {//长度等于四
                                commentList.remove(0);
                                commentList.add(commentList.size(), tempComment);
                            } else {
                                commentList.add(commentList.size(), tempComment);
                            }
                            commentAdapter.notifyDataSetChanged();
                        } else {
                            commentList.add(commentList.size(), tempComment);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                    if (people == 0) {
                        mTvPersonCount.setVisibility(View.GONE);
                    } else {
                        mTvPersonCount.setVisibility(View.VISIBLE);
                        mTvPersonCount.setText("当前人数 : " + people);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(StopBean stopBean) {
        if (stopBean != null) {
            Log.e("tag_message====", stopBean.getVideoId().toString() + "");
        }
    }


    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
//        RecordLoggerManager.closeLoggerFile();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
        EventBus.getDefault().unregister(this);
        mDataStatistics.stop();
        mMediaRecorder.release();
    }

    private Handler mHandler = new Handler();
    private GestureDetector mDetector;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector.OnGestureListener mGestureDetector = new GestureDetector.OnGestureListener() {
        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            if (mPreviewWidth > 0 && mPreviewHeight > 0) {
                float x = motionEvent.getX() / mPreviewWidth;
                float y = motionEvent.getY() / mPreviewHeight;
                mMediaRecorder.focusing(x, y);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    };

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mDetector.onTouchEvent(motionEvent);
            mScaleDetector.onTouchEvent(motionEvent);
            return true;
        }
    };

    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mMediaRecorder.setZoom(scaleGestureDetector.getScaleFactor());
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        }
    };

    private void startPreview(final SurfaceHolder holder) {
        if (!mHasPermission) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startPreview(holder);
                }
            }, PERMISSION_DELAY);
            return;
        }
        mMediaRecorder.prepare(mConfigure, mPreviewSurface);
        mMediaRecorder.setPreviewSize(_CameraSurface.getMeasuredWidth(), _CameraSurface.getMeasuredHeight());
        if ((int) mConfigure.get(AlivcMediaFormat.KEY_CAMERA_FACING) == AlivcMediaFormat.CAMERA_FACING_FRONT) {
            mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_BEAUTY_ON);
        }
    }

    private final SurfaceHolder.Callback _CameraSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            holder.setKeepScreenOn(true);
            mPreviewSurface = holder.getSurface();
            startPreview(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mMediaRecorder.setPreviewSize(width, height);
            mPreviewWidth = width;
            mPreviewHeight = height;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mPreviewSurface = null;
            mMediaRecorder.stopRecord();
            mMediaRecorder.reset();
        }
    };


    private OnRecordStatusListener mRecordStatusListener = new OnRecordStatusListener() {
        @Override
        public void onDeviceAttach() {
            mMediaRecorder.addFlag(AlivcMediaFormat.FLAG_AUTO_FOCUS_ON);
        }

        @Override
        public void onDeviceAttachFailed(int facing) {

        }

        @Override
        public void onSessionAttach() {
            if (isRecording && !TextUtils.isEmpty(pushUrl)) {
                mMediaRecorder.startRecord(pushUrl);
            }
            mMediaRecorder.focusing(0.5f, 0.5f);
        }

        @Override
        public void onSessionDetach() {

        }

        @Override
        public void onDeviceDetach() {

        }

        @Override
        public void onIllegalOutputResolution() {
            Log.d(TAG, "selected illegal output resolution");
            ToastUtils.showToast(LiveCameraActivity.this, R.string.illegal_output_resolution);
        }
    };

    //推流状态监听接口
    private OnNetworkStatusListener mOnNetworkStatusListener = new OnNetworkStatusListener() {
        @Override
        public void onNetworkBusy() {
            Log.w("network_status", "==== on network busy ====");
            ToastUtils.showToast(LiveCameraActivity.this, "当前网络状态极差，已无法正常流畅直播，确认要继续直播吗？");
        }

        @Override
        public void onNetworkFree() {
//            ToastUtils.showToast(LiveCameraActivity.this, "network free");
            Log.w("network_status", "===== on network free ====");
        }

        @Override
        public void onConnectionStatusChange(int status) {
            Log.w("network_status", "ffmpeg Live stream connection status-->" + status);

            switch (status) {
                case AlivcStatusCode.STATUS_CONNECTION_START:
//                    ToastUtils.showToast(LiveCameraActivity.this, "Start live stream connection!");
                    Log.w("network_status", "Start live stream connection!");
                    break;
                case AlivcStatusCode.STATUS_CONNECTION_ESTABLISHED:
                    Log.w("network_status", "Live stream connection is established!");
//                    showIllegalArgumentDialog("链接成功");
//                    ToastUtils.showToast(LiveCameraActivity.this, "Live stream connection is established!");
                    break;
                case AlivcStatusCode.STATUS_CONNECTION_CLOSED:
                    Log.w("network_status", "Live stream connection is closed!");
//                    ToastUtils.showToast(LiveCameraActivity.this, "Live stream connection is closed!");
//                    mLiveRecorder.stop();
//                    mLiveRecorder.release();
//                    mLiveRecorder = null;
//                    mMediaRecorder.stopRecord();
                    break;
            }
        }

//        @Override
//        public void onFirstReconnect() {
//            ToastUtils.showToast(LiveCameraActivity.this, "首次重连");
//        }


        @Override
        public boolean onNetworkReconnectFailed() {
            Log.w(TAG, "Reconnect timeout, not adapt to living");
            ToastUtils.showToast(LiveCameraActivity.this, "长时间重连失败，已不适合直播，请退出");
            mMediaRecorder.stopRecord();
            showIllegalArgumentDialog("网络重连失败");
            return false;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean hasPermission = true;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        int toastTip = 0;
                        if (Manifest.permission.CAMERA.equals(permissions[i])) {
                            toastTip = R.string.no_camera_permission;
                        } else if (Manifest.permission.RECORD_AUDIO.equals(permissions[i])) {
                            toastTip = R.string.no_record_audio_permission;
                        }
                        if (toastTip != 0) {
                            ToastUtils.showToast(this, toastTip);
                            hasPermission = false;
                        }
                    }
                }
                mHasPermission = hasPermission;
                break;
        }
    }


    public void showIllegalArgumentDialog(String message) {
        if (illegalArgumentDialog == null) {
            illegalArgumentDialog = new AlertDialog.Builder(this)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            illegalArgumentDialog.dismiss();
                        }
                    })
                    .setTitle("提示")
                    .create();
        }
        illegalArgumentDialog.dismiss();
        illegalArgumentDialog.setMessage(message);
        illegalArgumentDialog.show();
    }

    AlertDialog illegalArgumentDialog = null;

    private OnLiveRecordErrorListener mOnErrorListener = new OnLiveRecordErrorListener() {
        @Override
        public void onError(int errorCode) {
            Log.w("liveRecor", "Live stream connection error-->" + errorCode);
            String error = null;
            switch (errorCode) {
                case AlivcStatusCode.ERROR_ILLEGAL_ARGUMENT:
                    showIllegalArgumentDialog("-22错误产生");
                    error = "参数非法";
                case AlivcStatusCode.ERROR_SERVER_CLOSED_CONNECTION:
                    error = "服务器关闭链接";

                case AlivcStatusCode.ERORR_OUT_OF_MEMORY:
                    error = "内存不足";

                case AlivcStatusCode.ERROR_CONNECTION_TIMEOUT:
                    error = "网络链接超时";

                case AlivcStatusCode.ERROR_BROKEN_PIPE:
                    error = "管道中断";

                case AlivcStatusCode.ERROR_IO:
                    error = "I/O错误";

                case AlivcStatusCode.ERROR_NETWORK_UNREACHABLE:
                    error = "网络不可达";
//                    ToastUtils.showToast(LiveCameraActivity.this, "Live stream connection error-->" + errorCode);
                    break;
                default:
                    Log.w("liveRecor", "Live stream connection error-->" + errorCode);
                    Log.w("liveRecor", "Live stream connection error-->" + error + "");
                    break;
            }
        }
    };

    DataStatistics.ReportListener mReportListener = new DataStatistics.ReportListener() {
        @Override
        public void onInfoReport() {
            runOnUiThread(mLoggerReportRunnable);
        }
    };

    private Runnable mLoggerReportRunnable = new Runnable() {
        @Override
        public void run() {
            if (mRecordReporter != null) {
            }
        }
    };

    private AlivcEventResponse mBitrateUpRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Bundle bundle = event.getBundle();
            int preBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_PRE_BITRATE);
            int currBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_CURR_BITRATE);
            Log.w(TAG, "event->up bitrate, previous bitrate is " + preBitrate +
                    "current bitrate is " + currBitrate);
//            ToastUtils.showToast(LiveCameraActivity.this, "event->up bitrate, previous bitrate is " + preBitrate +
//                    "current bitrate is " + currBitrate);
        }
    };
    private AlivcEventResponse mBitrateDownRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Bundle bundle = event.getBundle();
            int preBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_PRE_BITRATE);
            int currBitrate = bundle.getInt(AlivcEvent.EventBundleKey.KEY_CURR_BITRATE);
            Log.w(TAG, "event->down bitrate, previous bitrate is " + preBitrate +
                    "current bitrate is " + currBitrate);
//            ToastUtils.showToast(LiveCameraActivity.this, "event->down bitrate, previous bitrate is " + preBitrate +
//                    "current bitrate is " + currBitrate);
        }
    };
    private AlivcEventResponse mAudioCaptureSuccRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event->audio recorder start success");
//            ToastUtils.showToast(LiveCameraActivity.this, "event->audio recorder start success");
        }
    };

    private AlivcEventResponse mVideoEncoderSuccRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event->video encoder start success");
//            ToastUtils.showToast(LiveCameraActivity.this, "event->video encoder start success");
        }
    };
    private AlivcEventResponse mVideoEncoderFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event->video encoder start failed");
//            ToastUtils.showToast(LiveCameraActivity.this, "event->video encoder start failed");
        }
    };
    private AlivcEventResponse mVideoEncodeFrameFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event->video encode frame failed");
//            ToastUtils.showToast(LiveCameraActivity.this, "event->video encode frame failed");
        }
    };


    private AlivcEventResponse mInitDoneRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event->live recorder initialize completely");
//            ToastUtils.showToast(LiveCameraActivity.this, "event->live recorder initialize completely");
        }
    };

    private AlivcEventResponse mDataDiscardRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Bundle bundle = event.getBundle();
            int discardFrames = 0;
            if (bundle != null) {
                discardFrames = bundle.getInt(AlivcEvent.EventBundleKey.KEY_DISCARD_FRAMES);
            }
            Log.w(TAG, "event->data discard, the frames num is " + discardFrames);
            ToastUtils.showToast(LiveCameraActivity.this, "event->data discard, the frames num is " + discardFrames);
        }
    };

    private AlivcEventResponse mAudioCaptureOpenFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event-> audio capture device open failed");
            ToastUtils.showToast(LiveCameraActivity.this, "event-> audio capture device open failed");
        }
    };

    private AlivcEventResponse mAudioEncodeFrameFailedRes = new AlivcEventResponse() {
        @Override
        public void onEvent(AlivcEvent event) {
            Log.w(TAG, "event-> audio encode frame failed");
            ToastUtils.showToast(LiveCameraActivity.this, "event-> audio encode frame failed");
        }
    };


    /*
    openType 1:字数
            0：直播关闭
     */
    public void showEditDialog(Context context, String message, final int openType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_notify, null);
        TextView tvCancel;    //确定按钮
        final TextView tvSure;    //内容
        TextView tvContent;
        tvContent = (TextView) view.findViewById(R.id.tv_notify);
        tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvSure = (TextView) view.findViewById(R.id.tv_sure);
        tvContent.setText(message);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (openType == 1) {
                    mEdtContent.setText("");
                }
            }
        });
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openType == 1) {
                    mEdtContent.setText("");
                    dialog.dismiss();
                } else {
                    mMediaRecorder.stopRecord();
                    isRecording = false;
                    dialog.dismiss();
                    getAllInfo(liveId, teacherId);
                }
            }
        });
        dialog.show();
    }

    /*
    监听键盘弹出或收回
     */
    private void onKeyBoardListener() {
        SoftKeyboardStateHelper softKeyboardStateHelper = new SoftKeyboardStateHelper(findViewById(R.id.root_layout));
        softKeyboardStateHelper.addSoftKeyboardStateListener(new SoftKeyboardStateHelper.SoftKeyboardStateListener() {
            @Override
            public void onSoftKeyboardOpened(int keyboardHeightInPx) {
                Log.e("tag_keyboard", "弹出");
            }

            @Override
            public void onSoftKeyboardClosed() {
                Log.e("tag_keyboard", "收回");
                mEdtLayout.setVisibility(View.GONE);
                mMenuLayout.setVisibility(View.VISIBLE);
            }
        });
    }


    /*
    键盘弹出
     */
    private void showKeyboard(EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(editText, 0);
    }

    /*
    键盘回收
     */
    private void closeKeyboard() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void getContent() {

        volleyUtils.getContentAndPerson(liveId, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                Log.e("tag_onresponse", response + "");
                if (null != response && !"".equals(response)) {
                    CommentBean commentBean = new Gson().fromJson(response, CommentBean.class);
                    if (0 == commentBean.getCode()) {
                        if (0 == commentBean.getData().getPeopleCount()) {//没有人在线
                            mTvPersonCount.setVisibility(View.GONE);
                        } else {
                            mTvPersonCount.setVisibility(View.VISIBLE);
                            mTvPersonCount.setText("当前人数 : " + commentBean.getData().getPeopleCount());
                        }
                        List<CommentBean.commentListBean> temp = commentBean.getData().getCommentList();
                        if (null != temp && 0 != temp.size()) {
                            commentList.addAll(temp);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void getAllInfo(String liveId, String teacherId) {
        buildProgressDialog();
        volleyUtils.getAllContentAndPerson(liveId, teacherId, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                cancelProgressDialog();
                AllCommonCountBean allCommonCountBean = new Gson().fromJson(response, AllCommonCountBean.class);
                if (allCommonCountBean.getCode() == 0) {
                    mTvPerCount.setText(String.valueOf(allCommonCountBean.getData().getPeopleCount()) + " 人看过");
                    mTvConCount.setText(String.valueOf(allCommonCountBean.getData().getCommentCount()) + " 条评论");
                    mPopWindow.showAsDropDown(rootView);
                } else {
                    finish();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                cancelProgressDialog();
                finish();
            }
        });
    }

    private void setContent(String videoId, String content, String createUser) {
        volleyUtils.addContent(videoId, content, createUser, new VolleyUtils.OnVolleyListener() {
            @Override
            public void onResponse(String response) {
                if (null != response && !"".equals(response)) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg = jsonObject.getString("msg");
                        int code = jsonObject.getInt("code");
                        if (code == 0 || code == 500) {
//                            mEdtLayout.setVisibility(View.GONE);
                            mMenuLayout.setVisibility(View.VISIBLE);
                            mEdtContent.setText("");
                            closeKeyboard();
                            Toast.makeText(LiveCameraActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
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


    /**
     * 加载框
     */
    public void buildProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setMessage("正在加载,请稍后!");
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }


    public void cancelProgressDialog() {
        if (mProgressDialog != null)
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
    }


    private MessageReceiver mMessageReceiver;

    public void registerMessageReceiver(String tag) {
        Set<String> sets = new HashSet<>();
        sets.add(tag);
        JPushInterface.setTags(this, sets, new TagAliasCallback() {
            @Override
            public void gotResult(int code, String s, Set<String> set) {
                String logs;
                switch (code) {
                    case 0:
                        logs = "Set tag and alias success极光推送别名设置成功";
                        break;
                    case 6002:
                        logs = "Failed to set alias and tags due to timeout. Try again after 60s.极光推送别名设置失败，60秒后重试";
                        break;
                    default:
                        logs = "极光推送设置失败，Failed with errorCode = " + code;
                        break;
                }
                Log.e("indexLog", logs);
            }
        });
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                        Log.e("tag_show_msg", showMsg.toString() + "");
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public static final String MESSAGE_RECEIVED_ACTION = "com.hszh.videodirect.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_EXTRAS = "extras";
    public static final String KEY_MESSAGE = "message";


    private void setInit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //将状态栏设置成全透明
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if ((winParams.flags & bits) == 0) {
                winParams.flags |= bits;
                //如果是取消全透明，params.flags &= ~bits;
                win.setAttributes(winParams);
            }
            //设置contentview为fitsSystemWindows
            ViewGroup viewGroup = (ViewGroup) findViewById(android.R.id.content);
            viewGroup.getChildAt(0).setFitsSystemWindows(true);
            //给statusbar着色
            View view = new View(this);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this)));
            view.setBackgroundColor(getResources().getColor(R.color.color_44000000));
            viewGroup.addView(view);
        }
    }


    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


}
