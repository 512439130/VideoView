package com.yy.videoview.videoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

/**
 * Created by 13160677911 on 2016-11-27.
 */

public class YyVideoView extends VideoView {
    int firstX = 0;
    int secondX = 0;

    public YyVideoView(Context context) {
        super(context);
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
    }

    /**
     * 解决onTouch和onclick冲突问题
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
                break;
            case MotionEvent.ACTION_UP:
                secondX = (int) event.getX();//up的时候x的位置
                int distance = secondX - firstX;
                System.out.println(firstX);
                System.out.println(secondX);
                if (distance < 50) {
                    //当没有变化的时候什么都不做

                    boolean flag = performClick();
                    if(flag){
                        System.out.println("执行onClick事件");
                    }
                    return false;

                } else {
                    //执行move滑动后的操作
                    System.out.println("执行onTouch事件");
                    return true;

                }


        }
        return true;
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
