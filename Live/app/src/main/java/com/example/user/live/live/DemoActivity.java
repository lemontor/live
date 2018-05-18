package com.example.user.live.live;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.alibaba.livecloud.live.AlivcMediaFormat;
import com.example.user.live.R;
import com.example.user.live.utils.ToastUtils;

public class DemoActivity extends Activity implements View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private EditText urlET;
    private Button connectBT;
    private RadioGroup resolutionCB;
    private RadioButton resolution240button;
    private RadioButton resolution360button;
    private RadioButton resolution480button;
    private RadioButton resolution540button;
    private RadioButton resolution720button;
    private RadioButton resolution1080button;
    private RadioGroup rotationGroup;
    private RadioButton screenOrientation1;
    private RadioButton screenOrientation2;
    private CheckBox frontCameraMirror;
    private EditText mEtMaxBitrate;
    private EditText mEtMinBitrate;
    private EditText mEtBestBitrate;
    private EditText mEtInitialBitrate;
    private EditText mEtFrameRate;

    private EditText watermarkET;
    private EditText dxET;
    private EditText dyET;
    private EditText siteET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);

        connectBT = (Button) findViewById(R.id.connectBT);
        connectBT.setOnClickListener(this);
        urlET = (EditText) findViewById(R.id.rtmpUrl);
        resolutionCB = (RadioGroup) findViewById(R.id.resolution_group);
        resolution240button = (RadioButton) findViewById(R.id.radiobutton0);
        resolution360button = (RadioButton) findViewById(R.id.radiobutton1);
        resolution480button = (RadioButton) findViewById(R.id.radiobutton2);
        resolution540button = (RadioButton) findViewById(R.id.radiobutton3);
        resolution720button = (RadioButton) findViewById(R.id.radiobutton4);
        resolution1080button= (RadioButton) findViewById(R.id.radiobutton5);
        rotationGroup =(RadioGroup)findViewById(R.id.rotation_group);
        screenOrientation1 = (RadioButton) findViewById(R.id.screenOrientation1);
        screenOrientation2 = (RadioButton) findViewById(R.id.screenOrientation2);
        frontCameraMirror = (CheckBox) findViewById(R.id.front_camera_mirror);
        resolutionCB.setOnCheckedChangeListener(this);
        rotationGroup.setOnCheckedChangeListener(this);
        mEtBestBitrate = (EditText) findViewById(R.id.et_best_bitrate);
        mEtMaxBitrate = (EditText) findViewById(R.id.et_max_bitrate);
        mEtMinBitrate = (EditText) findViewById(R.id.et_min_bitrate);
        mEtInitialBitrate = (EditText) findViewById(R.id.et_init_bitrate);
        mEtFrameRate = (EditText) findViewById(R.id.et_frame_rate);


        watermarkET = (EditText) findViewById(R.id.watermark_path);
        dxET = (EditText) findViewById(R.id.dx);
        dyET = (EditText) findViewById(R.id.dy);
        siteET = (EditText) findViewById(R.id.site);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connectBT:
                int videoResolution = 0;
                int cameraFrontFacing = 0;
                boolean screenOrientation = false;
                if(resolution240button.isChecked()){
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_240P;
                }else if (resolution360button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_360P;
                } else if (resolution480button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_480P;
                } else if (resolution540button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_540P;
                } else if (resolution720button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_720P;
                } else if (resolution1080button.isChecked()) {
                    videoResolution = AlivcMediaFormat.OUTPUT_RESOLUTION_1080P;
                }

                if(frontCameraMirror.isChecked()){
                    cameraFrontFacing = AlivcMediaFormat.CAMERA_FACING_FRONT;
                }else {
                    cameraFrontFacing = AlivcMediaFormat.CAMERA_FACING_BACK;
                }

                if (screenOrientation1.isChecked()){
                    screenOrientation = true;
                }else {
                    screenOrientation = false;
                }

                if(TextUtils.isEmpty(urlET.getText())){
//                    ToastUtils.showToast(v.getContext(),"Push url is null");
                    return;
                }

                String watermark = watermarkET.getText().toString();
                int dx = dxET.getText().toString() == null ? 14 : Integer.parseInt(dxET.getText().toString());
                int dy = dyET.getText().toString() == null ? 14 : Integer.parseInt(dyET.getText().toString());
                int site = siteET.getText().toString() == null ? 1 : Integer.parseInt(siteET.getText().toString());
                int minBitrate = 500;
                int maxBitrate = 800;
                int bestBitrate = 600;
                int initBitrate = 600;
                int frameRate = TextUtils.isEmpty(mEtFrameRate.getText().toString())?
                        30: Integer.parseInt(mEtFrameRate.getText().toString());
                try{
                    minBitrate = Integer.parseInt(mEtMinBitrate.getText().toString());
                }catch (NumberFormatException e) {
                }

                try{
                    maxBitrate = Integer.parseInt(mEtMaxBitrate.getText().toString());
                }catch(NumberFormatException e){}

                try {
                    bestBitrate = Integer.parseInt(mEtBestBitrate.getText().toString());
                }catch (NumberFormatException e){}

                try {
                    initBitrate = Integer.parseInt(mEtInitialBitrate.getText().toString());
                }catch(NumberFormatException e){}

//                LiveCameraActivity.RequestBuilder builder = new LiveCameraActivity.RequestBuilder()
//                        .bestBitrate(bestBitrate)
//                        .cameraFacing(cameraFrontFacing)
//                        .dx(dx)
//                        .dy(dy)
//                        .site(site)
//                        .rtmpUrl(urlET.getText().toString())
//                        .videoResolution(videoResolution)
//                        .portrait(screenOrientation)
//                        .watermarkUrl(watermark)
//                        .minBitrate(minBitrate)
//                        .maxBitrate(maxBitrate)
//                        .frameRate(frameRate)
//                        .initBitrate(initBitrate);
//                LiveCameraActivity.startActivity(v.getContext(), builder);
//

                Intent intent = new Intent(DemoActivity.this, LiveCameraActivity.class);
                Bundle  bundle = new Bundle();
                bundle.putString("pushUrl","rtmp://192.168.11.11/hls/123456789");
                bundle.putString("liveId","ss");
                bundle.putString("teacherName","sfsd");
                bundle.putString("img","asdfasd");
                intent.putExtras(bundle);
//            intent.putExtra("pushUrl","1d4f0e52c26d4e31a2d2ea92f09c96fb");
                startActivity(intent);
                break;
        }
    }
}
