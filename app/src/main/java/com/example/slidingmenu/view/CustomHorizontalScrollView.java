package com.example.slidingmenu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.example.slidingmenu.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * 继承HorizontalScrollView，自定义SlidingMenu：
 * 1、onMeasure()：决定内部View(子View)的宽和高，以及自己的宽和高
 * 2、onLayout()：决定子View放置的位置
 * 3、onTouchEvent
 */

/**
 * 用户自定义属性(允许用户设置菜单离屏幕右边的边距)用法：
 * 1、书写xml文件，values/attrs.xml
 * 2、在布局文件中使用，特别注意自定义属性的使用
 *    xmlns:ykmeory="http://schemas.android.com/apk/res/com.example.slidingmenu"
 *    ykmeory是任意命名，
 *    com.example.slidingmenu是项目所在包，不是自定义控件所在包
 *    实际使用中这么写：xmlns:ykmeory="http://schemas.android.com/apk/res-auto"，项目可自动解析
 * 3、在构造方法中(3个参数的构造方法)获取设置的值
 */

/**
 * 属性动画是Android 3.0才有的，3.0以下可添加nineoldandroids-2.4.0.jar,见build.gradle
 */
public class CustomHorizontalScrollView extends HorizontalScrollView {
    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mContent;
    private int mScreenWidth;

    private int mMenuWidth;

    private int mMenuRightPadding=50;//侧滑菜单到左边的距离，单位dp

    private boolean once;//默认false

    private boolean isOpen;//默认false

    /**
     * 未使用自定义属性时调用
     * @param context
     * @param attrs
     */
    public CustomHorizontalScrollView(Context context, AttributeSet attrs) {

        /*WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        mScreenWidth=dm.widthPixels;

        //把dp转化为px
        mMenuRightPadding= (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,
                getResources().getDisplayMetrics());//把50dp转为像素值*/

        this(context,attrs,0);//去调用三个参数的构造方法
    }

    /**
     * 当使用自定义属性时，调用此构造方法
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public CustomHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //获取自定义的属性
        TypedArray ta=context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomHorizontalScrollView,defStyleAttr,0);

        int n=ta.getIndexCount();
        for (int i=0;i<n;i++){
            int attr=ta.getIndex(i);
            switch (attr){
                case R.styleable.CustomHorizontalScrollView_rightPadding:
                    mMenuRightPadding=ta.getDimensionPixelSize(attr,
                            (int) TypedValue.
                                    applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,getResources().getDisplayMetrics()));
                    break;
            }
        }

        ta.recycle();

        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm=new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        mScreenWidth=dm.widthPixels;
    }

    public CustomHorizontalScrollView(Context context) {
        this(context, null);//去调用两个参数的构造方法
    }

    /**
     * 设置子View的宽和高，设置自己的宽和高
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!once){
            mWapper= (LinearLayout) getChildAt(0);
            mMenu= (ViewGroup) mWapper.getChildAt(0);
            mContent= (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth=mMenu.getLayoutParams().width=mScreenWidth-mMenuRightPadding;
            mContent.getLayoutParams().width=mScreenWidth;
            once=true;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 通过设置偏移量，将menu隐藏
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed){
            this.scrollTo(mMenuWidth,0);//隐藏菜单
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action=ev.getAction();

        switch (action){
            case MotionEvent.ACTION_UP:
                //隐藏在左边的宽度
                int scrollX=getScrollX();

                if (scrollX >= mMenuWidth/2){
                    //隐藏menu
                    this.smoothScrollTo(mMenuWidth,0);//smoothScrollTo有一个动画效果
                    isOpen=false;
                }else {
                    //显示menu
                    this.smoothScrollTo(0,0);
                    isOpen=true;
                }

                return true;
        }


        return super.onTouchEvent(ev);
    }

    /**
     * 打开菜单
     */
    public void openMenu(){
        if (isOpen)
            return;

        this.smoothScrollTo(0,0);//打开菜单
        isOpen=true;
    }

    /**
     * 关闭菜单
     */
    public void closeMenu(){
        if (!isOpen)
            return;

        this.smoothScrollTo(mMenuWidth,0);
        isOpen=false;
    }

    /**
     * 切换菜单
     */
    public void toggle(){
        if (isOpen){
            closeMenu();
        }else {
            openMenu();
        }
    }

    /**
     * 抽屉式菜单(一)
     * 抽屉式侧滑就是菜单仿佛在内容区域地下
     *
     * 滚动发生时调用（添加此方法可实现抽屉式侧滑效果，不需要抽屉式侧滑可注释掉此方法）
     * @param l
     * @param t
     * @param oldl
     * @param oldt
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        float scale=l*1.0f/mMenuWidth;//scale初始值是1,变化范围是1～0

        /*
         * 抽屉式菜单(二)
         * 区别1：内容区域1.0～0.7缩放的效果
         *        scale:1.0～0.0
         *        0.7+0.3*scale
         * 区别2：菜单的偏移量需要修改
         * 区别3：菜单的显示时有缩放以及透明度变化
         *        缩放：0.7～1.0
         *              1.0-scale*0.3
         *        透明度：0.6～1.0
         *               0.6+0.4(1-scale)
         */
        float rightScale=0.7f+0.3f*scale;
        float leftScale=1.0f-scale*0.3f;
        float leftAlpha=0.6f+0.4f*(1-scale);

        //调用属性动画，设置TranslationX
        ViewHelper.setTranslationX(mMenu,mMenuWidth*scale*0.7f);
        ViewHelper.setScaleX(mMenu, leftScale);
        ViewHelper.setScaleY(mMenu, leftScale);
        ViewHelper.setAlpha(mMenu,leftAlpha);

        //设置content的缩放中心点
        ViewHelper.setPivotX(mContent, 0);
        ViewHelper.setPivotY(mContent,mContent.getHeight()/2);

        ViewHelper.setScaleX(mContent, rightScale);
        ViewHelper.setScaleY(mContent,rightScale);
    }

}
