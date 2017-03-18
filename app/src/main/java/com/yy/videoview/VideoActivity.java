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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yy.videoview.videoview.YyVideoView;

import java.util.Timer;
import java.util.TimerTask;


public class VideoActivity extends Activity {
    private YyVideoView mVideoView;

    //控制视频相关（播放，暂停，继续）
    private ImageView startImageView;
    private ImageView stopImageView;
    private ImageView allImageView;

    //控制视频相关（快进，声音，亮度）
    private ImageView voiceImageView;  //声音
    private ImageView brightnessImageView;  //亮度
    private ImageView speedImageView; //快进
    private ImageView rewindImageView; //快退
    private TextView iconTextView;
    private TextView timeTextView;

    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    float y1 = 0;
    float y2 = 0;


    private boolean leftFlag = false;  //左边滑动标示
    private boolean rightFlag = false;  //右边滑动标示
    private boolean speedFlag = false;  //快进滑动标示
    private boolean rewindFlag = false;  //快退滑动标示


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


    //屏幕宽高
    private int screenWidth;
    private int screenHeight;


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

        hideToImage();

        obtainWidthOrHeight();


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

    /**
     * 获取屏幕宽高
     */
    private void obtainWidthOrHeight() {
        WindowManager wm = this.getWindowManager();

        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();
        System.out.println("1/2宽度=" + screenWidth / 2 + "，1/2高度=" + screenHeight / 2);
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
                mVideoView.setVideoPath("sdcard/Download/Movies/wodota3.mp4");  //播放本地视频
            } else if (value.equals("interent")) {
                //播放网络视频http://www.jb51.net/article/90992.htm

                //设置视频路径
                String videoUrl2 = UrlUtils.videoUrl;

                Uri uri = Uri.parse(videoUrl2);
                mVideoView.setVideoURI(uri);
            }
        }

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
        videoRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN://按下
                        //当手指按下的时候

                        x1 = event.getX();
                        y1 = event.getY();
                        System.out.println("x1 = " + x1 + ",y1 = " + y1);

                        if (x1 < screenWidth / 2 - 100) {   //左边上下滑动（亮度）http://www.cnblogs.com/zyw-205520/p/5660991.html

                            if (y1 - y2 > 100 && (y1 - y2) > (x1 - x2)){
                                setShowAnimation(brightnessImageView, 1800);  //渐现
                                leftFlag = true;
                                System.out.println("增加亮度");

                            }else if (y2 - y1 > 100 && (y2 - y1) > (x2 - x1)) {
                                leftFlag = true;
                                setShowAnimation(brightnessImageView, 1800);  //渐现
                                System.out.println("减少亮度");
                            }
                        }
                        if (x1 > screenWidth / 2 + 100) {  //右边上下滑动


                            if (y1 - y2 > 100 && (y1 - y2) > (x1 - x2)) {
                                setShowAnimation(voiceImageView, 1800);  //渐现
                                rightFlag = true;
                                System.out.println("增加声音");
                            } else if (y2 - y1 > 100 && (y2 - y1) > (x2 - x1)) {
                                rightFlag = true;
                                setShowAnimation(voiceImageView, 1800);  //渐现
                                System.out.println("减少声音");
                            }
                        }
                        if (x1 > screenWidth / 2 + 20) {

                            if (x2 - x1 > 100 && (x2 - x1) > (y2 - y1)) {
                                speedFlag = true;
                                setShowAnimation(speedImageView, 1800);  //渐现
                                System.out.println("快进");
                            }
                        }
                        if (x1 < screenWidth / 2 - 20) {

                            if (x1 - x2 > 100 && (x1 - x2) > (y1 - y2)) {
                                rewindFlag = true;
                                setShowAnimation(rewindImageView, 1800);  //渐现
                                System.out.println("快退");
                            }
                        }


                        break;

                    case MotionEvent.ACTION_UP://松开
                        //当手指离开的时候
                        x2 = event.getX();
                        y2 = event.getY();

                        System.out.println("x2 = " + x2 + ",y2 = " + y2);

                        if (leftFlag == true) {
                            setHideAnimation(brightnessImageView, 1300);
                            leftFlag = false;
                        } else if (rightFlag == true) {
                            setHideAnimation(voiceImageView, 1300);
                            rightFlag = false;
                        }
                        if (speedFlag == true) {
                            setHideAnimation(speedImageView, 1300);
                            speedFlag = false;
                        } else if (rewindFlag == true) {
                            setHideAnimation(rewindImageView, 1300);
                            rewindFlag = false;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        //遗留问题（快进）


                        break;
                }
                return true;
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
                Message msg = VideoActivity.handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("duration", duration);
                bundle.putInt("currentPosition", currentPosition);
                msg.setData(bundle);
                //发送消息，让进度条更新
                VideoActivity.handler.sendMessage(msg);
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
