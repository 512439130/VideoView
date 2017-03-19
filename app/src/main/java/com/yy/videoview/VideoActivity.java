package com.yy.videoview;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yy.videoview.videoview.YyVideoView;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class VideoActivity extends Activity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener {
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
    public static TextView iconTextView;
    public static TextView timeTextView;

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;


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
    private Timer timer;


    //屏幕宽高
    private int screenWidth;
    private int screenHeight;


   /* private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            int buffer_time = bundle.getInt("buffer_time");


            mSeekBar.setMax(duration);
            mSeekBar.setProgress(currentPosition);
            mSeekBar.setSecondaryProgress(buffer_time);


            System.out.println("handler_buffer_time=" + buffer_time);

           *//* System.out.println("总时间=" + duration);
            System.out.println("进度条=" + mSeekBar.getProgress());
            System.out.println("缓冲进度条=" + mSeekBar.getSecondaryProgress());*//*

        }
    };*/
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

    }


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
        iconTextView = (TextView) findViewById(R.id.id_tv_icon);
        timeTextView = (TextView) findViewById(R.id.id_tv_time);
        SeekBarLinearLayout = (LinearLayout) findViewById(R.id.id_layout_bottom);


        //初始状态默认为不显示控制组件（不添加会引起进度条控件失效）
        hideToImage();



    }


    /**
     * 初始状态默认为不显示
     */
    private void hideToImage() {
        voiceImageView.setVisibility(View.INVISIBLE);
        brightnessImageView.setVisibility(View.INVISIBLE);
        speedImageView.setVisibility(View.INVISIBLE);
        rewindImageView.setVisibility(View.INVISIBLE);
        iconTextView.setVisibility(View.INVISIBLE);
        timeTextView.setVisibility(View.INVISIBLE);
    }

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


        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        startImageView.setOnClickListener(new MyImageViewOnClickListener());
        stopImageView.setOnClickListener(new MyImageViewOnClickListener());
        allImageView.setOnClickListener(new MyImageViewOnClickListener());
        mVideoView.setOnClickListener(new View.OnClickListener() {
            long prelongTim = 0;//定义上一次单击的时间
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
        });

        //设置进度条的滑动监听
        mSeekBar.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());


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

    //播放视频前，VideoView对象必须要进入Prepared状态
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mp.seekTo(currentPosition);
        mp.setLooping(true);   //设置循环播放
        //设置音量//mp.setVolume(100,100);

        //addSeekBar();

        //设置ViedoView的缓冲监听
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                // 获得当前播放时间和当前视频的长度
                currentPosition = mVideoView.getCurrentPosition();
                duration = mVideoView.getDuration();
                int time = ((currentPosition * 100) / duration);
                // 设置进度条的主要进度，表示当前的播放时间
                mSeekBar.setProgress(time);

                // 设置进度条的次要进度，表示视频的缓冲进度
                buffer_percent = percent;
                mSeekBar.setSecondaryProgress(buffer_percent);
                System.out.println("当前缓冲的进度=" + buffer_percent);

            }


        });
        //播放结束监听
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });
        //设置错误信息监听
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Toast.makeText(VideoActivity.this, "正在缓冲", Toast.LENGTH_LONG).show();
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            //此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
            if (mp.isPlaying()) {
                Toast.makeText(VideoActivity.this, "缓冲结束", Toast.LENGTH_LONG).show();
                mVideoView.setVisibility(View.VISIBLE);
            }
        }
        return true;
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

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            //获取视频总长
            duration = mVideoView.getDuration();
            //先获取进度条当前进度
            int progress = seekBar.getProgress() * duration / 100;   //进度条的百分比转换成视频播放时间

            mVideoView.seekTo(progress);

        }
    }
}


/**
 * 开启一个计时器监听进度条变化
 */
    /*
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
                int duration = mVideoView.getDuration();//获取视频时长
                int currentPosition = mVideoView.getCurrentPosition(); //获取当前位置
                int buffer_time = duration * (buffer_percent / 82); //获取缓冲进度
                System.out.println("总时间=" + duration);
                System.out.println("进度条=" + mSeekBar.getProgress());


                System.out.println("当前缓冲的时间="+buffer_time);

                Message msg = VideoActivity.handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("duration", duration);
                bundle.putInt("currentPosition", currentPosition);

                bundle.putInt("buffer_time", buffer_time);

                msg.setData(bundle);
                //发送消息，让进度条更新
                VideoActivity.handler.sendMessage(msg);
            }
        }, 5, 500);
    }*/



