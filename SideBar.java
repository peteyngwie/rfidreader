package com.smartcity.cgs;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class SideBar extends View   {
    private  SideBar funSideBarListener ;        // function sidebar

     ;   // in order to get the application context
    private Context mContext ;
    ImageView leftarrowImg , homeImg , cameraImg ;


    public SideBar(Context context) {
        super(context);
        mContext = context ;

    }   // one paramters constructor

    public SideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context ;

    }   // two  paramters constructor

    public SideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context ;

    }   // three paramters constructor

    public SideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context ;


    }  // three paramters constructor



    private onLetterTouchedChangeListener onLetterTouchedChangeListener = null;

    // 侧邊攔字母顯示

    private String[] alphabet = { "前一頁" , "回首頁" , "照相"} ;     //
    private static final Integer[] FunctionIcons = {

        R.drawable.sidebarleftarrow , // back to previous page
        R.drawable.sidebarhome ,      // back to home
        R.drawable.sidebarcamera ,    // camera

       } ;


    MyApplication myApplication = new MyApplication() ;

    private int currentChooseAlphabetIndex = -1 ;
    private Paint paint = new Paint() ;      // define a painter


    private TextView textViewDialog = null;

    /**
     * 为SideBar设置显示字母的TextView
     * @param textViewDialog
     */
    public void setTextViewDialog(TextView textViewDialog) {
        this.textViewDialog = textViewDialog;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // SideBar的高度

        int viewHeight = getHeight();
        // SideBar的宽度
        int viewWidth = getWidth();
        //获得每个字母索引的高度
        // int singleHeight = viewHeight / alphabet.length;
        // 取得每個功能的高度

        int singleHeight = viewHeight / FunctionIcons.length;

        //绘制每一个字母的索引
        for (int i = 0; i < FunctionIcons.length; i++) {

            paint.setColor(Color.parseColor("#3A3C4BD9"));   // image's color
            paint.setColor(Color.rgb(58, 60, 75)); // 设置字母颜色

            // paint.setTypeface(Typeface.DEFAULT_BOLD);//设置字体
            // paint.setTextSize(20);    // 设置字体大小
            paint.setAntiAlias(true); // 抗锯齿

            //如果当前的手指触摸索引和字母索引相同，那么字体颜色进行区分

            if (currentChooseAlphabetIndex == i) {
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }

            /*
             * 绘制字体，需要制定绘制的x、y轴坐标
             *
             * x轴坐标 = 控件宽度的一半 - 字体宽度的一半
             * y轴坐标 = singleHeight * i + singleHeight
             */

            Rect src = new Rect();  // 圖片 >>原矩形
            Rect dst = new Rect();  // 螢幕 >>目標矩形

            float xpos = viewWidth / 2 - paint.measureText(alphabet[i]) / 2;
            float ypos = singleHeight * i + singleHeight;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),FunctionIcons[0] ) ;
            Rect srcrect = new Rect() ;
            Rect desrect = new Rect() ;

            canvas.drawBitmap(bitmap,srcrect , desrect , paint);

             // canvas.drawText(alphabet[i], xpos, ypos, paint);
            // 重置画笔，准备绘制下一个字母索引FunctionIcons
            paint.reset();
        }
    }

    public void setOnLetterTouchedChangeListener(
            onLetterTouchedChangeListener onLetterTouchedChangeListener) {

        this.onLetterTouchedChangeListener = onLetterTouchedChangeListener;
    }

    private onLetterTouchedChangeListener getOnLetterTouchedChangeListener() {
        return onLetterTouchedChangeListener;
    }

    /**
     * 当手指触摸的字母索引发生变化时，调用该回调接口
     *
     * @author owen
     */
    public interface onLetterTouchedChangeListener {
        public void onTouchedLetterChange(String letterTouched);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // 触摸事件的代码
        final int action = event.getAction();
        //手指触摸点在屏幕的Y坐标
        final float touchYPos = event.getY();
        // 因为currentChoosenAlphabetIndex会不断发生变化，所以用一个变量存储起来
        int preChoosenAlphabetIndex = currentChooseAlphabetIndex;
        final onLetterTouchedChangeListener listener = getOnLetterTouchedChangeListener();

        // 比例 = 手指触摸点在屏幕的y轴坐标 / SideBar的高度
        // 触摸点的索引 = 比例 * 字母索引数组的长度
        final int currentTouchIndex = (int) (touchYPos / getHeight() * alphabet.length);

        switch (action) {
            case MotionEvent.ACTION_UP:

                // 如果手指没有触摸屏幕，SideBar的背景颜色默認為透明灰黑色，索引字母提示控件不可见

                setBackground(new ColorDrawable(0x3A3C4BD9));
                currentChooseAlphabetIndex = -1;
                invalidate();

                if (textViewDialog != null) {
                    textViewDialog.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                // 其他情况，比如滑动屏幕、点击屏幕等等，SideBar会改变背景颜色，索引字母提示控件可见，同时需要设置内容
                setBackgroundResource(R.drawable.sidebarbackground) ;

                // 不是同一个字母索引
                if (currentTouchIndex != preChoosenAlphabetIndex) {
                    // 如果触摸点没有超出控件范围
                    if (currentTouchIndex >= 0 && currentTouchIndex < alphabet.length) {
                        if (listener != null) {
                            listener.onTouchedLetterChange(alphabet[currentTouchIndex]);
                        }

                        if (textViewDialog != null) {
                            textViewDialog.setText(alphabet[currentTouchIndex]);
                            textViewDialog.setVisibility(View.VISIBLE);
                        }

                        currentChooseAlphabetIndex = currentTouchIndex;
                        invalidate();
                    }
                }
                break;
        }

        return super.dispatchTouchEvent(event);
    }


    public class MyApplication extends Application {

        private static MyApplication instance;

        public static MyApplication getInstance() {
            return instance;
        }

        @Override
        public void onCreate() {
            super.onCreate();
            instance = this;
        }
    }
}

