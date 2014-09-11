package com.powerock.hydra.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.ImageView;

public class LoadingImageView extends ImageView {
 
    public LoadingImageView(Context context) {
        super(context);
 
    }
 
    public LoadingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public LoadingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    int i = 600;
 
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
 
        Rect rect = canvas.getClipBounds();
        rect.top += i;
        System.out.println(rect.bottom + "---" + rect.top);
        canvas.drawRect(rect, paint);
 
        i -= 10;
        if (i >= 0) {
            new Thread(new Runnable() {
 
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(1);
                }
            }).start();
        }
 
        super.onDraw(canvas);
    }
 
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            invalidate();
        }
    };
 
}