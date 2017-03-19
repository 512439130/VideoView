package com.yy.videoview.videoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.VideoView;

/**
 * Created by 13160677911 on 2016-11-27.
 */

public class YyVideoView extends VideoView {
    int firstX = 0;
    int secondX = 0;
    int firstY = 0;
    int secondY = 0;

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

    /*
    * 第一属于程序内实例化时采用，之传入Context即可
    * 第二个用于layout文件实例化，会把XML内的参数通过AttributeSet带入到View内。
    * 第三个主题的style信息，也会从XML里带入
    * */
    public YyVideoView(Context context) {
        this(context, null);
    }

    public YyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public YyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*//获取MyTextView当前实例的宽
        videoWidth = this.getWidth();
        //获取MyTextView当前实例的高
        videoHeight = this.getHeight();

        //通过Log.v打印输出显示
        Log.v("onMeasure获取控件宽高", ",宽:" + videoWidth + "高:" + videoHeight);*/
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
        System.out.println("视频播放器宽高，宽度" + videoWidth  + "，高度=" + videoHeight );


    }

    /**
     * 获取宽高
     */
    private void obtainWidthOrHeight() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;


    }

    /**
     * 解决onTouch和onclick冲突问题
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final int moveX = (int) event.getX();


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                firstX = (int) event.getX();//按下的时候开始的x的位置
                firstY = (int) event.getY();


                break;
            case MotionEvent.ACTION_UP:
                secondX = (int) event.getX();//up的时候x的位置
                secondY = (int) event.getY();
                int distanceX = secondX - firstX;
                int distanceY = secondY - firstY;
                System.out.println("firstX=" + firstX);
                System.out.println("secondX=" + secondX);
                System.out.println("firstY=" + firstY);
                System.out.println("secondY=" + secondY);
                if ((Math.abs(distanceX) < 20 && Math.abs(distanceY) < 20 )) {
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

    private void SlideListener() {
        if (firstX < videoWidth / 2) {   //左边上下滑动（亮度）http://www.cnblogs.com/zyw-205520/p/5660991.html

            if (firstY - secondY > 100 && (firstY - secondY) > (firstX - secondX)) {
                leftFlag = true;
                System.out.println("增加亮度");

            } else if (secondY - firstY > 100 && (secondY - firstY) > (secondX - firstX)) {
                leftFlag = true;
                System.out.println("减少亮度");
            }else if (secondX - firstX > 100 && ((secondX - firstX) > (secondY - firstY))) {
                speedFlag = true;
                System.out.println("快进");
            }else if (firstX - secondX > 100 && ((firstX - secondX) > (firstY - secondY)) ){
                rewindFlag = true;
                System.out.println("快退");
            }
        }

        if (firstX > videoWidth / 2 ) {  //右边上下滑动


            if (firstY - secondY > 100 && ((firstY - secondY) > (firstX - secondX))) {
                rightFlag = true;
                System.out.println("增加声音");
            } else if (secondY - firstY > 100 && ((secondY - firstY) > (secondX - firstX))) {
                rightFlag = true;
                System.out.println("减少声音");
            }else if (secondX - firstX > 100 && ((secondX - firstX) > (secondY - firstY))) {
                speedFlag = true;
                System.out.println("快进");
            }else if (firstX - secondX > 100 && ((firstX - secondX) > (firstY - secondY)) ){
                rewindFlag = true;
                System.out.println("快退");
            }
        }

       /* if (firstX > screenWidth / 2 + 20) {

            if (secondX - firstX > 100 && (secondX - firstX) > (secondY - firstY)) {
                speedFlag = true;
                System.out.println("快进");
            }

        }

        if (firstX < screenWidth / 2 - 20) {

            if (firstX - secondX > 100 && ((firstX - secondX) > (firstY - secondY)) ){
                rewindFlag = true;
                System.out.println("快退");
            }

        }*/


        if (leftFlag == true) {
            System.out.println("亮度消失动画");
            leftFlag = false;

        } else if (rightFlag == true) {
            System.out.println("声音消失动画");
            rightFlag = false;

        }

        if (speedFlag == true) {
            System.out.println("快进消失动画");
            speedFlag = false;

        } else if (rewindFlag == true) {
            System.out.println("快退消失动画");
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





                /*switch (action) {
                    case MotionEvent.ACTION_DOWN://按下
                        //当手指按下的时候

                        x1 = event.getX();
                        y1 = event.getY();
                        System.out.println("x1 = " + x1 + ",y1 = " + y1);

                        if (x1 < screenWidth / 2 - 100) {   //左边上下滑动（亮度）http://www.cnblogs.com/zyw-205520/p/5660991.html

                            if (y1 - y2 > 100 && (y1 - y2) > (x1 - x2)) {
                                setShowAnimation(brightnessImageView, 1800);  //渐现
                                leftFlag = true;
                                System.out.println("增加亮度");

                            } else if (y2 - y1 > 100 && (y2 - y1) > (x2 - x1)) {
                                leftFlag = true;
                                setShowAnimation(brightnessImageView, 1800);  //渐现
                                System.out.println("减少亮度");
                            }
                            return true;
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
                            return true;
                        }

                        if (x1 > screenWidth / 2 + 20) {

                            if (x2 - x1 > 100 && (x2 - x1) > (y2 - y1)) {
                                speedFlag = true;
                                setShowAnimation(speedImageView, 1800);  //渐现
                                System.out.println("快进");
                            }
                            return true;
                        }

                        if (x1 < screenWidth / 2 - 20) {

                            if (x1 - x2 > 100 && (x1 - x2) > (y1 - y2)) {
                                rewindFlag = true;
                                setShowAnimation(rewindImageView, 1800);  //渐现
                                System.out.println("快退");
                            }
                            return true;
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
                            return true;
                        } else if (rightFlag == true) {
                            setHideAnimation(voiceImageView, 1300);
                            rightFlag = false;
                            return true;
                        }

                        if (speedFlag == true) {
                            setHideAnimation(speedImageView, 1300);
                            speedFlag = false;
                            return true;
                        } else if (rewindFlag == true) {
                            setHideAnimation(rewindImageView, 1300);
                            rewindFlag = false;
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE://移动
                        //遗留问题（快进）


                        break;
                }*/


}
