package com.powerock.hydra.common;

import java.util.Random;

import com.powerock.demo2.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class ProgressImageView extends View{  
 
 
    Paint mPaint;  
 
    Bitmap bitmap;  
    Bitmap mBitmap;
    int mBitmapWidth = 0;  
    int mBitmapHeight = 0;  
 
    int mArrayColor[] = null;  
    int mArrayColorLengh = 0;  
    long startTime = 0;  
    int mBackVolume = 0;  
    int progress = 0;
    public ProgressImageView(Context context) {  
        super(context);  
        init(context);  
 
    }  
 
    public ProgressImageView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        init(context);  
    }  
 
    void init(Context context) {  
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
 
        //�����ﴴ����һ��bitmap  
        bitmap = BitmapFactory.decodeResource(context.getResources(),  
            R.drawable.progressbg); 
        mBitmap=bitmap.copy(bitmap.getConfig(), true);
        //������bitmap����Ϊ����ͼƬ  
        setBackgroundDrawable(new BitmapDrawable(mBitmap));  
 
        mBitmapWidth = mBitmap.getWidth();  
        mBitmapHeight = mBitmap.getHeight();  
 
        mArrayColorLengh = mBitmapWidth * mBitmapHeight;  
        mArrayColor = new int[mArrayColorLengh];  
        int count = 0;  
        for (int i = 0; i < mBitmapHeight; i++) {  
        for (int j = 0; j < mBitmapWidth; j++) {  
            //���Bitmap ͼƬ��ÿһ�����color��ɫֵ  
            int color = mBitmap.getPixel(j, i);  
            //����ɫֵ����һ�������� ��������޸�  
            mArrayColor[count] = color;  
            //����������ĸ�ϸ�µĻ� ���԰���ɫֵ��R G B �õ�����Ӧ�Ĵ��� ����������Ͳ����������  
            int r = Color.red(color);  
            int g = Color.green(color);  
            int b = Color.blue(color);  
              
            count++;  
        }  
        }  
        startTime = System.currentTimeMillis();  
    }  
 
    /**  
     * ����һ�������  
     *   
     * @param botton  
     * @param top  
     * @return  
     */  
    int UtilRandom(int botton, int top) {  
        return ((Math.abs(new Random().nextInt()) % (top + 1 - botton)) + botton);  
    }  
 
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        if (progress <=100) {  
        setVolume(progress);         
        }else{
        	setVolume(100); 
        }
          
        //����ˢ����Ļ  
        invalidate();  
    }  
 
    public void setVolume(int volume) {  
        int startY = 0;  
        int endY = 0;  
        //�жϵ�ǰӦ����������� ���ǻ�ԭ�ɵ�����   
        
          
        startY = getValue(volume);  
        endY = getValue(mBackVolume);  
        //û��Ҫÿ�ζ�ѭ��ͼƬ�е����е㣬��Ϊ������ȽϺ�ʱ��  
        int count = startY * mBitmapWidth;  
        //��ͼƬ��Ҫ�����߻�ԭ ��ɫ����ʼ�� ��ʼ �� �յ�   
        for (int i = startY; i < endY; i++) {  
        for (int j = 0; j < mBitmapWidth; j++) {   
            //����Ҫ������ɫֵ�������  
            //����˵��һ�� ���color ��ȫ͸�� ����ȫ�� ����ֵΪ 0  
            //getPixel()����͸��ͨ�� getPixel32()�Ŵ�͸������ ����ȫ͸����0x00000000   
            //����͸����ɫ��0xFF000000 ���������͸�����־Ͷ���0��  
            int color = mBitmap.getPixel(j, i);  
            if (color != 0) {  
                mBitmap.setPixel(j, i, Color.parseColor("#ffffff"));  
            }  
            } 
            count++;  
        }  
         
        mBackVolume = volume;  
    }  
      
    //ͨ���ٷֱ� ����ͼƬ������ʵ����� �߶�  
    public int getValue(int volume) {  
        return mBitmapHeight - (mBitmapHeight * volume / 100);  
    }  
    
    public void setProgress(int progress){
    	this.progress = progress;
    }
 
}  