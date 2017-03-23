package com.yy.videoview.videoview;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.VideoView;

import com.yy.videoview.R;
import com.yy.videoview.VideoActivity;

/**
 * Created by 13160677911 on 2016-11-27.
 */

public class YyVideoView extends VideoView {
    int firstX = 0;
    int secondX = 0;
    int firstY = 0;
    int secondY = 0;

    public static int distanceX = 0;  //横向改变的值
    public static int distanceY = 0;  //纵向改变的值

    //屏幕宽高
    private int screenWidth;
    private int screenHeight;


    //视频播放器宽高
    private int videoWidth;
    private int videoHeight;


    private boolean leftFlag = false;  //左边滑动标示
    private boolean rightFlag = false;  //右边滑动标示
    private boolean speedFlag = false;  //快进滑动标示
    private boolean rewindFlag = false;  //快退滑动标示

    public int audio;  //系统当前音量
    public int light;  //系统当前亮度

    private ContentResolver mContentResolver;
    private AudioManager mAudioManager;

    /*
    * 第一属于程序内实例化时采用，之传入Context即可
    * 第二个用于layout文件实例化，会把XML内的参数通过AttributeSet带入到View内。
    * 第三个主题的style信息，也会从XML里带入
    * */
    public YyVideoView(Context context) {
        this(context, null);
        System.out.println("1个参数构造方法调用");
    }

    public YyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        System.out.println("2个参数构造方法调用");
        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {
        light = getSystemBrightness(context);
        mContentResolver = context.getContentResolver();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获得系统亮度
     * @param context
     * @return
     */
    private int getSystemBrightness(Context context) {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }


    /**
     * 加减亮度
     * @param lightness
     */
    public void setLightness(int lightness) {
        //获取屏幕当前的亮度
        System.out.println("系统当前的亮度=" + light);
        light = light + lightness;   //系统亮度加设置亮度等于需要的亮度
        if(light >= 0 && light <= 255){
            Settings.System.putInt(mContentResolver,Settings.System.SCREEN_BRIGHTNESS, light);
        }else{
            return;
        }

    }


    /**
     * 加减音量
     * @param volume
     */
    public void setAudio(int volume) {

        //获取屏幕当前音量
        audio = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //最大音量
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);  //max=15
        Log.d("==d==", "" + max);
        Log.d("==d==", "" + audio);
        audio = audio + volume;
        if (audio >= 0 && audio <= max) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audio, AudioManager.FLAG_PLAY_SOUND);
        } else {
            return;
        }
    }





    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        obtainWidthOrHeight();  //获取屏幕宽高
        //获取MyTextView当前实例的宽
        videoWidth = this.getWidth();
        //获取MyTextView当前实例的高
        videoHeight = this.getHeight();
        System.out.println("手机宽高，宽度" + screenWidth + "，高度=" + screenHeight);
        System.out.println("视频播放器宽高，宽度" + videoWidth + "，高度=" + videoHeight);
        //切换全屏小屏时需要先隐藏图标
        hideToImage();
    }

    /**
     * 初始状态默认为不显示
     */
    private void hideToImage() {
        VideoActivity.voiceImageView.setVisibility(View.INVISIBLE);
        VideoActivity.brightnessImageView.setVisibility(View.INVISIBLE);
        VideoActivity.speedImageView.setVisibility(View.INVISIBLE);
        VideoActivity.rewindImageView.setVisibility(View.INVISIBLE);
    }
    /**
     * 获取屏幕宽高
     */
    private void obtainWidthOrHeight() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    /**
     *
     * 屏幕滑动监听（解决onTouch和onclick冲突问题）
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstX = (int) event.getX();//按下的时候开始的x的位置
                firstY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                secondX = (int) event.getX();//up的时候x的位置
                secondY = (int) event.getY();
                distanceX = secondX - firstX;
                distanceY = secondY - firstY;
                System.out.println("firstX=" + firstX);
                System.out.println("secondX=" + secondX);
                System.out.println("firstY=" + firstY);
                System.out.println("secondY=" + secondY);
                if ((Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20)) {
                    //当没有变化的时候什么都不做

                    boolean flag = performClick();
                    if (flag) {
                        System.out.println("执行onClick事件");
                    }
                    return false;

                } else {
                    //执行move滑动后的操作
                    System.out.println("执行onTouch事件");
                    SlideListener();
                    return true;
                }
        }
        return true;
    }

    /**
     * 滑动手势判断
     */
    private void SlideListener() {
        if (firstX < videoWidth / 2) {   //左边上下滑动（声音）

            if (firstY - secondY > 100 && (firstY - secondY) > (firstX - secondX)) {
                leftFlag = true;
                System.out.println("增加声音");
                setShowAnimation(VideoActivity.voiceImageView, 1800);  //渐现
                if (audio <= 15) {
                    setAudio(2);  //系统声音最大值为15
                }else if(audio == 15){
                    setAudio(1);
                }

            } else if (secondY - firstY > 100 && (secondY - firstY) > (secondX - firstX)) {
                leftFlag = true;
                System.out.println("减少声音");
                setShowAnimation(VideoActivity.voiceImageView, 1800);  //渐现
                if (audio >= 0) {
                    setAudio(-2);  //系统声音最大值为15
                }else if(audio == 1){
                    setAudio(-1);
                }

            } else if (secondX - firstX > 100 && ((secondX - firstX) > (secondY - firstY))) {
                speedFlag = true;
                //System.out.println("快进");
                //setShowAnimation(VideoActivity.speedImageView, 1800);  //渐现
            } else if (firstX - secondX > 100 && ((firstX - secondX) > (firstY - secondY))) {
                rewindFlag = true;
                //System.out.println("快退");
                //setShowAnimation(VideoActivity.rewindImageView, 1800);  //渐现
            }
        }

        if (firstX > videoWidth / 2) {  //右边上下滑动（亮度）
            if (firstY - secondY > 100 && ((firstY - secondY) > (firstX - secondX))) {
                rightFlag = true;

                System.out.println("增加亮度");   //系统亮度最大值为255
                setShowAnimation(VideoActivity.brightnessImageView, 1800);  //渐现
                if(light <= 255){
                    setLightness(26);
                }else if(light == 234){
                    setLightness(21);
                }
            } else if (secondY - firstY > 100 && ((secondY - firstY) > (secondX - firstX))) {
                rightFlag = true;

                System.out.println("减少亮度");
                setShowAnimation(VideoActivity.brightnessImageView, 1800);  //渐现
                if(light >= 0){
                    setLightness(-26);
                }else if(light == 21){
                    setLightness(-21);
                }
            } else if (secondX - firstX > 100 && ((secondX - firstX) > (secondY - firstY))) {
                speedFlag = true;
                //System.out.println("快进");
                //setShowAnimation(VideoActivity.speedImageView, 1800);  //渐现
            } else if (firstX - secondX > 100 && ((firstX - secondX) > (firstY - secondY))) {
                rewindFlag = true;
                //System.out.println("快退");
                //setShowAnimation(VideoActivity.rewindImageView, 1800);  //渐现
            }
        }

        if (leftFlag == true) {
            setHideAnimation(VideoActivity.voiceImageView, 1300);
            System.out.println("声音消失动画");
            leftFlag = false;

        } else if (rightFlag == true) {

            setHideAnimation(VideoActivity.brightnessImageView, 1300);
            System.out.println("亮度消失动画");
            rightFlag = false;

        }

        if (speedFlag == true) {
            //setHideAnimation(VideoActivity.speedImageView, 1300);
            //System.out.println("快进消失动画");
            speedFlag = false;

        } else if (rewindFlag == true) {
            //setHideAnimation(VideoActivity.rewindImageView, 1300);
            //System.out.println("快退消失动画");
            rewindFlag = false;
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
