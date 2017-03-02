package com.yy.videoview.videoview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
import android.widget.VideoView;

import com.yy.videoview.MainActivity;

/**
 * Created by 13160677911 on 2016-11-27.
 */

public class YyVideoView extends VideoView {


    public YyVideoView(Context context) {
        super(context);
    }

    public YyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public YyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public YyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
