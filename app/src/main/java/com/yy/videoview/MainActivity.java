package com.yy.videoview;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.yy.videoview.videoview.YyVideoView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    private YyVideoView mVideoView;

    //控制视频相关
    private ImageView startImageView;
    private ImageView stopImageView;
    private ImageView allImageView;

    private static SeekBar mSeekBar;

    //变换图标相关
    private Boolean StartFlag = true;
    private Boolean ScreenFlag = true;
    private Boolean StopFlag = true;

    //触碰VideoView显示与隐藏SeekBar
    private Boolean SeekBarFlag = true;

    private LinearLayout bottomLinearLayout;
    private RelativeLayout videoRelativeLayout;
    private RelativeLayout viewRelativeLayout;

    private int currentPosition = 0;  //记录当前播放进度

    private Timer timer;


    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            mSeekBar.setMax(duration);
            mSeekBar.setProgress(currentPosition);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //去除头部
        setContentView(R.layout.activity_main);

        initView();
        initLayout(true);
        initEvent();
    }


    private void initView() {
        mVideoView = (YyVideoView) findViewById(R.id.id_vv_videoview);

        startImageView = (ImageView) findViewById(R.id.id_iv_start);
        stopImageView = (ImageView) findViewById(R.id.id_iv_stop);
        allImageView = (ImageView) findViewById(R.id.id_iv_all);
        mSeekBar = (SeekBar) findViewById(R.id.id_sk_seekbar);

        startImageView.setOnClickListener(new MyImageViewOnClickListener());
        stopImageView.setOnClickListener(new MyImageViewOnClickListener());
        allImageView.setOnClickListener(new MyImageViewOnClickListener());
        //设置进度条的滑动监听
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //先获取进度条当前进度
                int progress = seekBar.getProgress();
                mVideoView.seekTo(progress);

            }
        });

        bottomLinearLayout = (LinearLayout) findViewById(R.id.id_layout_bottom);
        videoRelativeLayout = (RelativeLayout) findViewById(R.id.id_view_videoview);
        viewRelativeLayout = (RelativeLayout) findViewById(R.id.id_view_videoview);
    }

    private void initEvent() {
        mVideoView.setVideoPath("sdcard/Download/Movies/wodota3.mp4");
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                mVideoView.seekTo(currentPosition);
                addSeekBar();
            }
        });
        videoRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击了VideoView");
                if (SeekBarFlag) {
                    bottomLinearLayout.setVisibility(View.VISIBLE);  //SeekBar显示
                    SeekBarFlag = false;
                } else {
                    bottomLinearLayout.setVisibility(View.INVISIBLE);  //SeekBar隐藏
                    SeekBarFlag = true;
                }
            }
        });

    }

    /**
     * 设置视频全屏播放
     *
     * @param fullscreen
     */
    private void initLayout(Boolean fullscreen) {
        if (!fullscreen) {//设置RelativeLayout的全屏模式
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mVideoView.setLayoutParams(layoutParams);
        } else {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_RIGHT);
            mVideoView.setLayoutParams(layoutParams);
        }
    }


    public class MyImageViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_iv_start:  //继续和暂停按钮的重合
                    System.out.println("开始播放");
                    //变换开始按钮为暂停按钮

                    if (StartFlag) {   //点击了暂停按钮，变化为播放按钮
                        startImageView.setImageResource(R.mipmap.start);
                        mVideoView.pause();
                        StartFlag = false;
                    } else {
                        startImageView.setImageResource(R.mipmap.pause);
                        StartFlag = true;
                        if (!StopFlag) {  //当停止按钮按下后
                            mVideoView.resume();
                            StopFlag = true;  //改变为初始状态
                        } else {
                            mVideoView.start();
                        }

                    }


                    break;
                case R.id.id_iv_stop:  //停止播放
                    System.out.println("停止播放");
                    //停止后改变图标
                    startImageView.setImageResource(R.mipmap.start);
                    StartFlag = false;  //改变图标
                    StopFlag = false;
                    stopTimer();  //停止计时器
                    mSeekBar.setProgress(0);//停止后改变进度条为初始
                    mVideoView.stopPlayback();
                    currentPosition = 0; //播放进度记录清零
                    break;
                case R.id.id_iv_all:  //横屏/竖屏显示
                    if (ScreenFlag) {   //点击了暂停按钮，变化为播放按钮
                        allImageView.setImageResource(R.mipmap.all);
                        ScreenFlag = false;
                        initLayout(false);  //改变视频为全屏
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //横屏
                    } else {
                        allImageView.setImageResource(R.mipmap.max);
                        ScreenFlag = true;
                        initLayout(true);
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //竖屏
                        //全屏显示
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 开启一个计时器监听进度条变化
     */
    private void addSeekBar() {
        if (timer == null) {
            timer = new Timer();
            System.out.println("计时器为空");
        }
        //设置计时任务:每500毫秒获取一次当前的播放进度,更新进度条
        //获取当前曲目的持续时间
        timer.schedule(new TimerTask() {
            @Override
            public void run() {  //每500毫秒执行一次
                //获取当前播放进度
                int duration = mVideoView.getDuration();//获取歌曲时长
                int currentPosition = mVideoView.getCurrentPosition(); //获取当前位置
                Message msg = MainActivity.handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("duration", duration);
                bundle.putInt("currentPosition", currentPosition);
                msg.setData(bundle);
                //发送消息，让进度条更新
                MainActivity.handler.sendMessage(msg);
            }
        }, 5, 500);
    }


    @Override
    protected void onPause() {
        currentPosition = mVideoView.getCurrentPosition();
        stopTimer();    //点击home键退出播放器时，停止掉Timer计时器
        System.out.println("onPause调用");
        mVideoView.pause();
        super.onPause();

    }


    @Override
    protected void onResume() {
        mVideoView.resume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mVideoView.stopPlayback();
        mVideoView.destroyDrawingCache();
        super.onDestroy();
    }


    /**
     * 停止Timer计时器
     */
    private void stopTimer() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

    }


}
