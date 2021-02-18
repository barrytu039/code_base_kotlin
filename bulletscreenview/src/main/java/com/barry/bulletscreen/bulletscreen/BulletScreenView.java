package com.barry.bulletscreen.bulletscreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulletScreenView extends FrameLayout {

    private int lv = 4;//滾動彈幕共有幾行可用
    private int height;//每一行的高度
    private Paint paint = new Paint();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, Temporary> map = new HashMap<>();//每一行最後的動畫
    private List<Temporary> list = new ArrayList<>();//存有當前螢幕上的所有動畫
    private int textSize = 20;
    private boolean stop = false;//暫停功能
    private long[] indexCountDown = {-1L,-1L,-1L,-1L};
    private int index = 0;
    public BulletScreenView(Context context) {
        this(context, null);
    }

    public BulletScreenView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //設定文字大小
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getContext().getResources().getDisplayMetrics()));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        height = (int) (paint.measureText("我") + 50);//測量一行的高度
    }

    //新增一條滾動彈幕
    public void add(String string) {
        if (stop) {
            return;
        }
        //建立控制元件
        final TextView textView = new TextView(getContext());
        textView.setText(string);
        textView.setTextSize(textSize);
        textView.setTextColor(Color.RED);
        textView.setMaxLines(1);
        addView(textView);
        //找到合適插入到行數
        float minPosition = Integer.MAX_VALUE;//最小的位置
        int minLv = 0;//最小位置的行數
        for (int i = 0; i < lv; i++) {
            Temporary temporary = map.get(i);//獲取到該行最後一個動畫
            if (temporary == null) {
                minLv = i;
                break;
            }
            float p = (float) map.get(i).animation.getAnimatedValue() + map.get(i).viewLength;//獲取位置
            if (minPosition > p) {
                minPosition = p;
                minLv = i;
            }
        }
        long duration = 5000;

        if (indexCountDown[index] == -1) {
            duration = (long) (Math.random()* 2000 + 2000);
            indexCountDown[index] = duration;
        } else {
            duration = (long) (Math.random()* 2000 + 2000);

        }
        indexCountDown[index] = duration;
        index++;
        if (index == 4) {
            index = 0;
        }
        //設定行數
        LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.topMargin =  height * minLv;
        textView.setLayoutParams(layoutParams);

        //設定動畫
        final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(textView, "translationX", getWidth(),
                -paint.measureText(string));
        objectAnimator.setDuration(duration);//設定動畫時間
        objectAnimator.setInterpolator(new LinearInterpolator());//設定差值器

        //將彈幕相關資料快取起來
        final Temporary temporary = new Temporary(objectAnimator);
        temporary.time = 0;
        temporary.viewLength = paint.measureText(string);
        list.add(temporary);
        map.put(minLv, temporary);
        //動畫結束監聽
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!stop) {
                    removeView(textView);//移除控制元件
                    list.remove(temporary);//移除快取
                }
            }
        });
        objectAnimator.start();//開啟動畫
    }

    //停止動畫
    public void stop() {
        if (stop) {
            return;
        }
        stop = true;
        for (int i = 0; i < list.size(); i++) {
            Temporary temporary = list.get(i);
            temporary.time = temporary.animation.getCurrentPlayTime();
            temporary.animation.cancel();//會呼叫結束介面
        }
    }

    //重新開始
    public void restart() {
        if (!stop) {
            return;
        }
        stop = false;
        for (Temporary temporary : list) {
            temporary.animation.start();
            temporary.animation.setCurrentPlayTime(temporary.time);
        }
    }

    //清除全部
    public void clear() {
        map.clear();
        list.clear();
        removeAllViews();
    }

    private static class Temporary {//方便快取動畫
        long time;
        float viewLength;
        ObjectAnimator animation;

        Temporary(ObjectAnimator animation) {
            this.animation = animation;
        }
    }

}