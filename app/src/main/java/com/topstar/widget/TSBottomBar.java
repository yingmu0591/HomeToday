package com.topstar.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.topstar.TSApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * 底部工具栏
 */
public class TSBottomBar extends View {

    private int containerId;//填充容器id
    private int nameSize = 10;// 名称大小
    private int itemCount;// 标签项数量
    private int beforeNameColor;// 名称选择前颜色
    private int afterNameColor;//名称选择后颜色
    private int currentCheckedIndex;// 当前选择的下标
    //    private int defaultCheckedIndex;// 默认选择的下标
    private Context context;
    private List<Class<? extends Fragment>> fragmentClassList = new ArrayList<>();// 标签项碎片类列表
    private List<Fragment> fragmentList = new ArrayList<>();// 标签项碎片实例列表
    private List<String> nameList = new ArrayList<>();// 标签文本列表
    private List<Integer> beforeIconList = new ArrayList<>();// 选择前图片列表
    private List<Bitmap> beforeBitmapList = new ArrayList<>();//选择前位图列表
    private List<Integer> afterIconList = new ArrayList<>();// 选择后图片列表
    private List<Bitmap> afterBitmapList = new ArrayList<>();// 选择后位图列表
    private List<Rect> iconRectList = new ArrayList<>();// 图像的矩形区域
    private Paint paint = new Paint();
    private Fragment currentFragment;

    public TSBottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    /**
     * 设置底部菜单操作的填充容器（展示页面内容的地方）
     *
     * @return TSBottomBar
     */
    public TSBottomBar setContainer(int containerId) {
        this.containerId = containerId;
        return this;
    }

    /**
     * 设置名称大小
     *
     * @return TSBottomBar
     */
    public TSBottomBar setTextSize(int nameSize) {
        this.nameSize = nameSize;
        return this;
    }

//    /**
//     * 设置图标宽度
//     *
//     * @return TSBottomBar
//     */
//    public TSBottomBar setIconWidth(int iconWidth) {
//        this.iconWidth = iconWidth;
//        return this;
//    }

//    /**
//     * 设置图标高度
//     *
//     * @return TSBottomBar
//     */
//    public TSBottomBar setIconHeight(int iconHeight) {
//        this.iconHeight = iconHeight;
//        return this;
//    }

//    /**
//     * 设置缩进
//     *
//     * @return TSBottomBar
//     */
//    public TSBottomBar setMargin(int margin) {
//        this.margin = margin;
//        return this;
//    }

    /**
     * 设置名称选中前后的颜色（仅仅设置名称的颜色）
     *
     * @return TSBottomBar
     */
    public TSBottomBar setBeforeAndAfterNameColor(int beforeColorId, int afterColorId) {
        this.beforeNameColor = TSApplication.getResourcesColor(beforeColorId);
        this.afterNameColor = TSApplication.getResourcesColor(afterColorId);
        return this;
    }

    /**
     * 添加标签项
     *
     * @return TSBottomBar
     */
    public TSBottomBar addItem(Class<? extends Fragment> fragmentClass, int nameId, int beforeIcon, int afterIcon) {
        this.fragmentClassList.add(fragmentClass);
        this.nameList.add(TSApplication.getResourcesString(nameId));
        this.beforeIconList.add(beforeIcon);
        this.afterIconList.add(afterIcon);
        return this;
    }

//    /**
//     * 设置默认选择
//     *
//     * @return TSBottomBar
//     */
//    public TSBottomBar setDefaultChecked(int defaultCheckedIndex) {//从0开始
//        this.defaultCheckedIndex = defaultCheckedIndex;
//        return this;
//    }

    /**
     * 构建底部导航栏
     */
    public void build() {
        this.itemCount = this.fragmentClassList.size();
        //预创建bitmap和icon的Rect并缓存
        for (int i = 0; i < itemCount; i++) {
            this.beforeBitmapList.add(this.getBitmap(this.beforeIconList.get(i)));
            this.afterBitmapList.add(this.getBitmap(this.afterIconList.get(i)));
            this.iconRectList.add(new Rect());
            Class<? extends Fragment> clx = fragmentClassList.get(i);
            try {
                fragmentList.add(clx.newInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
//        this.currentCheckedIndex = this.defaultCheckedIndex;
        this.switchFragment(this.currentCheckedIndex);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.initParam();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);//这里让view自身替我们画背景，如果指定的话
        if (this.itemCount != 0) {
            //画背景
            this.paint.setAntiAlias(false);
            for (int i = 0; i < this.itemCount; i++) {
                canvas.drawBitmap(i == this.currentCheckedIndex ? this.afterBitmapList.get(i) : this.beforeBitmapList.get(i), null, this.iconRectList.get(i), paint);//null代表bitmap全部画出
            }
            //画文字
            this.paint.setAntiAlias(true);
            for (int i = 0; i < itemCount; i++) {
                this.paint.setColor(i == this.currentCheckedIndex ? this.afterNameColor : this.beforeNameColor);
                canvas.drawText(this.nameList.get(i), this.nameXList.get(i), this.nameBaseLine, this.paint);
            }
        }
    }

    //////////////////////////////////////////////////
    //点击事件：down和up都在该区域内才响应
    //////////////////////////////////////////////////
    int target = -1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.target = this.withinWhichArea((int) event.getX());
                break;
            case MotionEvent.ACTION_UP:
                if (event.getY() < 0) {
                    break;
                }
                if (this.target == this.withinWhichArea((int) event.getX())) {
                    //这里触发点击事件
                    this.switchFragment(this.target);
                    this.currentCheckedIndex = this.target;
                    invalidate();
                }
                this.target = -1;
                break;
        }
        return true;
        //这里return super为什么up执行不到？是因为return super的值，全部取决于你是否
        //clickable，当你down事件来临，不可点击，所以return false，也就是说，而且你没
        //有设置onTouchListener，并且控件是ENABLE的，所以dispatchTouchEvent的返回值
        //也是false，所以在view group的dispatchTransformedTouchEvent也是返回false，
        //这样一来，view group中的first touch target就是空的，所以intercept标记位
        //果断为false，然后就再也进不到循环取孩子的步骤了，直接调用dispatch-
        // TransformedTouchEvent并传孩子为null，所以直接调用view group自身的dispatch-
        // TouchEvent了
    }

    /**
     * 显示指定碎片（只支持AppCompatActivity，如果需要支持老版的请自行修改）
     */
    protected void switchFragment(int index) {
        Fragment fragment = this.fragmentList.get(index);// 获取需要将要显示的碎片
        if (fragment != null) {
            FragmentTransaction transaction = ((AppCompatActivity) this.context).getSupportFragmentManager().beginTransaction();
            if (fragment.isAdded()) {
                if (this.currentFragment != null) {
                    transaction.hide(this.currentFragment).show(fragment);
                } else {
                    transaction.show(fragment);
                }
            } else {
                if (this.currentFragment != null) {
                    transaction.hide(this.currentFragment).add(this.containerId, fragment);
                } else {
                    transaction.add(this.containerId, fragment);
                }
            }
            this.currentFragment = fragment;
            transaction.commit();
        }
    }

    private Bitmap getBitmap(int resId) {//生成位图
        return ((BitmapDrawable) this.context.getResources().getDrawable(resId)).getBitmap();
    }

    private int nameBaseLine;
    private List<Integer> nameXList = new ArrayList<>();
    private int parentItemWidth;

    private void initParam() {
        if (this.itemCount != 0) {
            this.parentItemWidth = getWidth() / this.itemCount;//单个item宽
            int parentItemHeight = getHeight();//单个item高
            int iconWidth = dp2px(20);//icon的像素（宽）
            int iconHeight = dp2px(20);//icon的像素（高）
            int margin = dp2px(((float) 5) / 2);//图标文字margin（先指定5dp，这里除以一半才是正常的margin，不知道为啥，可能是图片的原因）
            this.paint.setTextSize(dp2px(this.nameSize));// 名称文字的高度
            Rect rect = new Rect();
            this.paint.getTextBounds(this.nameList.get(0), 0, this.nameList.get(0).length(), rect);
            int titleHeight = rect.height();
            int iconTop = (parentItemHeight - iconHeight - margin - titleHeight) / 2;//计算得出图标的起始top坐标、文本的baseLine
            this.nameBaseLine = parentItemHeight - iconTop;
            //对icon的rect的参数进行赋值
            int firstRectX = (this.parentItemWidth - iconWidth) / 2;//第一个icon的左
            int rectX;
            Rect temp;
            for (int i = 0; i < this.itemCount; i++) {
                rectX = i * this.parentItemWidth + firstRectX;
                temp = this.iconRectList.get(i);
                temp.left = rectX;
                temp.top = iconTop;
                temp.right = rectX + iconWidth;
                temp.bottom = iconTop + iconHeight;
            }
            String title;
            for (int i = 0; i < this.itemCount; i++) { //名称（单位是个问题）
                title = this.nameList.get(i);
                this.paint.getTextBounds(title, 0, title.length(), rect);
                this.nameXList.add((this.parentItemWidth - rect.width()) / 2 + this.parentItemWidth * i);
            }
        }
    }

    private int dp2px(float dpValue) {
        return (int) (dpValue * (this.context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    private int withinWhichArea(int x) {
        return x / this.parentItemWidth;
    }
}