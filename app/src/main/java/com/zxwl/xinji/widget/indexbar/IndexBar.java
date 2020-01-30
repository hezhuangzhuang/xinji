package com.zxwl.xinji.widget.indexbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

/**
 * author：pc-20171125
 * data:2019/7/31 09:58
 */
public class IndexBar extends View {
    private Paint mPaint;
    private int itemHeight;//每个index占据空间
    private IndexChangeListener listener;
    private List<String> indexsList;
    private int textSize = 40;
    private int selTextColor = Color.BLACK;
    private int norTextColor = Color.GRAY;
    private float yAxis;//文字y轴方向的基线

    public IndexBar(Context context) {
        this(context, null);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(norTextColor);
        mPaint.setTextSize(textSize);
        mPaint.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float total = -fontMetrics.ascent + fontMetrics.descent;
        yAxis = total / 2 - fontMetrics.descent;
    }

    private int topMargin;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (indexsList != null && indexsList.size() > 0) {
            itemHeight = h / (indexsList.size());
            topMargin = (h - itemHeight * indexsList.size()) / 2;

            Log.i("IndexLayout", "item高度:" + itemHeight + ",topMargin高度：" + topMargin);

        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (indexsList != null && indexsList.size() > 0) {
            for (int i = 0; i < indexsList.size(); i++) {
                Paint.FontMetrics fm = mPaint.getFontMetrics();
                float y = itemHeight / 2 + (fm.bottom - fm.top) / 2 - fm.bottom + itemHeight * i + topMargin;
                Log.i("IndexLayout", "起始点:" + y);
                canvas.drawText(indexsList.get(i),
                        getWidth() / 2,
                        y,
                        mPaint);
            }
        }
    }

    private int curPos = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (indexsList == null || indexsList.size() == 0) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPaint.setColor(selTextColor);
                invalidate();
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < itemHeight / 2 || (event.getY() - itemHeight / 2) > itemHeight * indexsList.size()) {
                    return true;
                }

                int position = (int) ((event.getY() - itemHeight / 2) / itemHeight * 1.0f);
                if (position >= 0 && position < indexsList.size()) {
                    ((IndexLayout) getParent()).drawCircle(event.getY(), indexsList.get(position));
                    if (listener != null && curPos != position) {
                        curPos = position;
                        listener.indexChanged(indexsList.get(position));
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                ((IndexLayout) getParent()).dismissCircle();
                mPaint.setColor(norTextColor);
                invalidate();
                break;
        }
        return true;
    }


    public interface IndexChangeListener {
        void indexChanged(String indexName);
    }

    public void setIndexsList(List<String> indexs) {
        this.indexsList = indexs;
        requestLayout();
    }

    public void setIndexChangeListener(IndexChangeListener listener) {
        this.listener = listener;
    }

    public void setIndexTextSize(int textSize) {
        this.textSize = textSize;
        mPaint.setTextSize(textSize);
    }

    public void setSelTextColor(int selTextColor) {
        this.selTextColor = selTextColor;
    }

    public void setNorTextColor(int norTextColor) {
        this.norTextColor = norTextColor;
        mPaint.setColor(norTextColor);
    }
}
