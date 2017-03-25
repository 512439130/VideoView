package com.yy.videoview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import android.os.Bundle;


import android.view.View;
import android.view.Window;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.yy.videoview.videoview.YyVideoView;


public class VideoActivity extends Activity{
    private YyVideoView mVideoView;

    //控制视频相关（播放，暂停，继续）
    private ImageView startImageView;
    private ImageView stopImageView;
    private ImageView allImageView;

    //控制视频相关（快进，声音，亮度）
    public static ImageView voiceImageView;  //声音
    public static ImageView brightnessImageView;  //亮度
    public static ImageView speedImageView; //快进
    public static ImageView rewindImageView; //快退




    private static SeekBar mSeekBar;


    //变换图标相关
    private Boolean StartFlag = true;
    private Boolean ScreenFlag = true;
    private Boolean StopFlag = true;

    //触碰VideoView显示与隐藏SeekBar
    private Boolean SeekBarFlag = true;
    private LinearLayout SeekBarLinearLayout;


    private int duration = 0;  //记录记录视频的总时间
    private int currentPosition = 0;  //记录当前播放进度
    private static int buffer_percent = 0; //记录当前缓存百分比

    /**
     * 定时隐藏
     */
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);   //去除头部
        setContentView(R.layout.activity_video);

        initView();
        initLayout(true);
        initEvent();
        initListener();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        mVideoView = (YyVideoView) findViewById(R.id.id_vv_videoview);
        startImageView = (ImageView) findViewById(R.id.id_iv_start);
        stopImageView = (ImageView) findViewById(R.id.id_iv_stop);
        allImageView = (ImageView) findViewById(R.id.id_iv_all);
        mSeekBar = (SeekBar) findViewById(R.id.id_sk_seekbar);


        voiceImageView = (ImageView) findViewById(R.id.id_iv_voice); //声音
        brightnessImageView = (ImageView) findViewById(R.id.id_iv_brightness); // 亮度
        speedImageView = (ImageView) findViewById(R.id.id_iv_speed); //快进
        rewindImageView = (ImageView) findViewById(R.id.id_iv_rewind); //快退

        SeekBarLinearLayout = (LinearLayout) findViewById(R.id.id_layout_bottom);


        //初始状态默认为不显示控制组件（不添加会引起进度条控件失效）
        hideToImage();


    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        Intent intent = getIntent();
        //从Intent当中根据key取得value
        if (intent != null) {
            String value = intent.getStringExtra(UrlUtils.video_type);
            if (value.equals("local")) {
                mVideoView.setVideoPath(UrlUtils.videoLocalUrl);  //播放本地视频
            } else if (value.equals("interent")) {
                //设置视频路径
                String videoUrl2 = UrlUtils.videoUrl;
                Uri uri = Uri.parse(videoUrl2);
                mVideoView.setVideoURI(uri);
            }
        }
    }

    /**
     * 初始化监听
     */
    private void initListener() {

        startImageView.setOnClickListener(new MyImageViewOnClickListener()); //开始按钮
        stopImageView.setOnClickListener(new MyImageViewOnClickListener());  //结束按钮
        allImageView.setOnClickListener(new MyImageViewOnClickListener());   //继续状态转换
        mVideoView.setOnClickListener(new MyVideoViewOnClickListener());    //视频播放器点击事件

        mSeekBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());  //进度条的滑动监听

        mVideoView.setOnPreparedListener(new MyOnPreparedListener());  //播放监听
        mVideoView.setOnCompletionListener(new MyOnCompletionListener());

        mVideoView.setOnInfoListener(new MyOnInfoListener());  //缓冲状态监听
    }

    /**
     * 视频暂停时
     */
    @Override
    protected void onPause() {
        currentPosition = mVideoView.getCurrentPosition();
        duration = mVideoView.getDuration();
        buffer_percent = mVideoView.getBufferPercentage();

        // stopTimer();    //点击home键退出播放器时，停止掉Timer计时器
        System.out.println("onPause调用");
        mVideoView.pause();
        super.onPause();

    }


    /**
     * 视频重新启动时
     */
    @Override
    protected void onResume() {
        mVideoView.resume();
        super.onResume();
    }

    /**
     * 视频关闭时
     */
    @Override
    protected void onDestroy() {
        mVideoView.stopPlayback();
        mVideoView.destroyDrawingCache();
        super.onDestroy();
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

    /**
     * 初始状态默认为不显示
     */
    private void hideToImage() {
        voiceImageView.setVisibility(View.INVISIBLE);
        brightnessImageView.setVisibility(View.INVISIBLE);
        speedImageView.setVisibility(View.INVISIBLE);
        rewindImageView.setVisibility(View.INVISIBLE);
    }

    /**
     * 视频控制按钮点击事件监听
     */
    public class MyImageViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.id_iv_start:  //继续和暂停按钮的重合
                    System.out.println("开始播放");
                    //变换开始按钮为暂停按钮

                    if (StartFlag) {   //点击了暂停按钮，变化为播放按钮
                        startImageView.setImageResource(R.drawable.ic_seekbar_start);
                        mVideoView.pause();
                        StartFlag = false;
                    } else {
                        startImageView.setImageResource(R.drawable.ic_seekbar_pause);
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
                    startImageView.setImageResource(R.drawable.ic_seekbar_start);
                    StartFlag = false;  //改变图标
                    StopFlag = false;

                    mSeekBar.setProgress(0);//停止后改变进度条为初始
                    mVideoView.stopPlayback();
                    currentPosition = 0; //播放进度记录清零
                    break;
                case R.id.id_iv_all:  //横屏/竖屏显示
                    if (ScreenFlag) {   //点击了暂停按钮，变化为播放按钮
                        allImageView.setImageResource(R.drawable.ic_seekbar_all);
                        ScreenFlag = false;
                        initLayout(false);  //改变视频为全屏
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  //横屏
                    } else {
                        allImageView.setImageResource(R.drawable.ic_seekbar_max);
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
     * 进度条滑动监听
     */
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            System.out.println("停止拖动");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            System.out.println("开始拖动");
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            System.out.println("停止拖动");
            //获取视频总长
            duration = mVideoView.getDuration();
            //先获取进度条当前进度
            int progress = seekBar.getProgress() * duration / 100;   //进度条的百分比转换成视频播放时间
            mVideoView.seekTo(progress);

        }
    }


    /**
     * videoview（屏幕点击事件监听）
     */
    private class MyVideoViewOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            System.out.println("点击了VideoView");
            if (SeekBarFlag) {
                setShowAnimation(SeekBarLinearLayout, 1500);  //SeekBar显示渐现
                SeekBarFlag = false;
                    /*mHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            setHideAnimation(SeekBarLinearLayout, 2000);//SeekBar隐藏
                            SeekBarFlag = true;
                        }
                    },4000);*/
            } else {
                setHideAnimation(SeekBarLinearLayout, 1500);//SeekBar隐藏
                SeekBarFlag = true;
            }
        }
    }

    /**
     * 视频播放器监听
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        //播放视频前，VideoView对象必须要进入Prepared状态
        @Override
        public void onPrepared(MediaPlayer mp) {
            //加载进度条2秒

            mVideoView.start();
            mp.seekTo(currentPosition);
            mp.setLooping(true);   //设置循环播放
            //设置ViedoView的缓冲监听
            mp.setOnBufferingUpdateListener(new MyOnBufferingUpdateListener());
        }
    }
    /**
     * 视频缓冲监听
     */
    private class MyOnBufferingUpdateListener implements MediaPlayer.OnBufferingUpdateListener {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            // 获得当前播放时间和当前视频的长度
            currentPosition = mVideoView.getCurrentPosition();
            duration = mVideoView.getDuration();
            int time = 0;
            time = ((currentPosition * 100) / duration) + time;
            // 设置进度条的主要进度，表示当前的播放时间
            mSeekBar.setProgress(time);

            // 设置进度条的次要进度，表示视频的缓冲进度
            buffer_percent = percent;
            mSeekBar.setSecondaryProgress(buffer_percent);
            System.out.println("当前缓冲的进度=" + buffer_percent);
        }
    }

    /**
     * 播放结束监听
     */
    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mSeekBar.setProgress(0);//停止后改变进度条为初始
            currentPosition = 0; //播放进度记录清零
            mVideoView.start();
            //播放结束后的动作
            mp.seekTo(currentPosition);


        }
    }

    /**
     * 缓冲状态监听
     */
    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                //Toast.makeText(VideoActivity.this, "正在缓冲", Toast.LENGTH_LONG).show();
            } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                if (mp.isPlaying()) {
                   // Toast.makeText(VideoActivity.this, "缓冲结束", Toast.LENGTH_LONG).show();
                    mVideoView.setVisibility(View.VISIBLE);
                }
            }
            return true;
        }
    }

    private AlphaAnimation mHideAnimation = null;
    private AlphaAnimation mShowAnimation = null;

    /**
     * View渐隐动画效果
     */
    public void setHideAnimation(final View view, int duration) {
        if (mHideAnimation != null || mShowAnimation != null) {
            mShowAnimation = null;
            mHideAnimation = null;  //对上一次的动画对象进行销毁
        }
        if (null == view || duration < 0) {
            return;
        }

        if (null != mHideAnimation) {
            mHideAnimation.cancel();
        }
        // 监听动画结束的操作
        mHideAnimation = new AlphaAnimation(1.0f, 0.0f);
        mHideAnimation.setDuration(duration);
        mHideAnimation.setFillAfter(true);
        mHideAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                view.setClickable(false);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setVisibility(View.GONE);
            }
        });
        view.startAnimation(mHideAnimation);
    }

    /**
     * View渐现动画效果
     */
    public void setShowAnimation(final View view, int duration) {
        if (mHideAnimation != null || mShowAnimation != null) {
            mShowAnimation = null;
            mHideAnimation = null;  //对上一次的动画对象进行销毁
        }
        if (null == view || duration < 0) {
            return;
        }
        if (null != mShowAnimation) {
            mShowAnimation.cancel();
        }
        mShowAnimation = new AlphaAnimation(0.0f, 1.0f);
        mShowAnimation.setDuration(duration);
        mShowAnimation.setFillAfter(true);
        mShowAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                view.setClickable(true);
            }
        });
        view.startAnimation(mShowAnimation);
    }


}






