package com.jing.customview2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class CustomImageView extends View {
    private String titleText;
    private int titleTextColor = Color.RED;
    private int titleTextSize = 0;


    private Bitmap mImage;
    private int mImageScale;

    private TextPaint mTextPaint;
    private Rect mTextBound;


    private int mWidth;
    private int mHeight;


    private Rect rect;


    public CustomImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CustomImageView, defStyle, 0);


        int n = a.getIndexCount();


        for (int i= 0;i<n;i++){
            int attr = a.getIndex(i);

            switch (attr){
                case R.styleable.CustomImageView_image:
                    mImage = BitmapFactory.decodeResource(getResources(),a.getResourceId(attr,0));
                    break;
                case R.styleable.CustomImageView_imageScaleType:
                    mImageScale = a.getInt(attr,0);
                    break;

                case R.styleable.CustomImageView_titleText:
                    titleText = a.getString(attr);
                    break;
                case R.styleable.CustomImageView_titleTextColor:
                    titleTextColor = a.getInt(attr,Color.BLACK);
                    break;
                case R.styleable.CustomImageView_titleTextSize:
                    titleTextSize = a.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,16, getResources().getDisplayMetrics()));
                    break;

            }




        }



        a.recycle();

        rect = new Rect();

        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setTextSize(titleTextSize);

        mTextBound = new Rect();

        mTextPaint.getTextBounds(titleText,0,titleText.length(),mTextBound);



    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        /**
         * 边框
         */

        mTextPaint.setStrokeWidth(4);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(Color.CYAN);
        canvas.drawRect(0,0,getMeasuredWidth(),getMeasuredHeight(),mTextPaint);

        rect.left = getPaddingLeft();
        rect.right = mWidth - getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = mHeight - getPaddingBottom();

        mTextPaint.setColor(titleTextColor);
        mTextPaint.setStyle(Paint.Style.FILL);


        if (mTextBound.width() > mWidth){
            TextPaint paint = new TextPaint(mTextPaint);
            String msg = TextUtils.ellipsize(titleText,paint,(float)mWidth-getPaddingLeft()-getPaddingRight(),TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg,getPaddingLeft(),mHeight - getPaddingBottom(),paint);

        }else{

            canvas.drawText(titleText,mWidth/2-mTextBound.width()/2,mHeight-getPaddingBottom(),mTextPaint);

        }

        //取消使用掉的快
        rect.bottom -= mTextBound.height();

        if (mImageScale == 0){//fitxy

            canvas.drawBitmap(mImage,null,rect,mTextPaint);


        }else{
            //计算居中的矩形范围
            rect.left = mWidth / 2 - mImage.getWidth() / 2;
            rect.right = mWidth / 2 + mImage.getWidth() / 2;
            rect.top = (mHeight - mTextBound.height()) / 2 - mImage.getHeight() / 2;
            rect.bottom = (mHeight - mTextBound.height()) / 2 + mImage.getHeight() / 2;

            canvas.drawBitmap(mImage, null, rect, mTextPaint);

        }











    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        /**
         * 设置宽度
         */
         int specMode = MeasureSpec.getMode(widthMeasureSpec);
         int specSize = MeasureSpec.getSize(widthMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY){

            Log.e("xxx","EXACTLY");

            mWidth = specSize;


        }else{

            int desireByImg = getPaddingLeft() + getPaddingRight()+mImage.getWidth();

            int desireByTitle = getPaddingLeft() + getPaddingRight() + mTextBound.width();

            if (specMode == MeasureSpec.AT_MOST)//wrap_content
            {
                int desire = Math.max(desireByImg,desireByTitle);

                mWidth = Math.min(desire,specSize);

            }


        }


        /**
         * 设置高度
         */

        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);

        if (specMode == MeasureSpec.EXACTLY)
        {
           mHeight = specSize;

        }else{
            int desire = getPaddingTop() + getPaddingBottom() + mTextBound.height();

            if (specMode == MeasureSpec.AT_MOST){
                mHeight = Math.min(desire,specSize);

            }

        }


       setMeasuredDimension(mWidth,mHeight);


    }
}
