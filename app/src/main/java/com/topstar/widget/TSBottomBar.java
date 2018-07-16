package com.topstar.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.topstar.TSApplication;

/**
 * 底部工具栏
 */
public class TSBottomBar extends View {
    private int containerId;//填充容器id
    private int beforeTextColor;// 选择前文本颜色
    private int afterTextColor;//选择后文本颜色
    private int checkedIndex;// 选择项
    private int itemCount;//Item数量
    private int itemWidth;//Item宽度
    private int target;// 触发的目标Item（一开始未点击过，相当没目标）
    private Paint paint = new Paint();// 画图工具（Canvas为画板）
    private Rect rect = new Rect();
    private Item[] itemArray;
    private Class<? extends Item>[] itemClassArray;
    private DrawingMetadata[] drawingMetadataArray;
    private Handler handler;
    private Runnable runnable;
    private FragmentManager fragmentManager;

    public TSBottomBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.fragmentManager = ((Activity) context).getFragmentManager();
        Log.i("TSBottomBar", "TSBottomBar");
    }

    /**
     * 设置底部菜单操作的填充容器（展示页面内容的地方）
     */
    public void setContainer(int containerId) {
        this.containerId = containerId;
    }

    /**
     * 设置名称选中前后的颜色（仅设置文本的颜色）
     */
    public void setBeforeAndAfterTextColor(int beforeColorId, int afterColorId) {
        this.beforeTextColor = TSApplication.getResourcesColor(beforeColorId);
        this.afterTextColor = TSApplication.getResourcesColor(afterColorId);
    }

    /**
     * 设置默认选择
     */
    public void setDefaultChecked(int defaultCheckedIndex) {
        this.checkedIndex = defaultCheckedIndex;
    }

    /**
     * 添加标签项
     */
    @SafeVarargs
    public final void addItems(Class<? extends Item>... itemClasses) {
        this.itemClassArray = itemClasses;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {// 初始化绘图数据
        Log.i("TSBottomBar", "onSizeChanged");
        this.itemCount = this.itemClassArray.length;
        if (this.itemCount != 0) {
            if (this.itemArray == null) {//第一次打开程序才会为null，否则会在onRestoreInstanceState中进行初始化
                this.itemArray = new Item[this.itemCount];
                for (int i = 0; i < this.itemCount; i++) {
                    this.itemArray[i] = this.createItemInstance(this.itemClassArray[i]);
                }
            }
            this.drawingMetadataArray = new DrawingMetadata[this.itemCount];// 初始化绘图元数据数组
            this.itemWidth = width / this.itemCount;//Item的宽度
            int iconSize = (int) (height * 0.4);//导航栏高度的2/5做为图标的大小（图标高度和宽度一致）
            int iconItemStartX = (this.itemWidth - iconSize) / 2;//图标在每个Item中左右边距（作为图标的X轴，因为图标居中所以左右一样）
            this.paint.setTextSize(iconSize / 2);//文本的大小（高度）设置为图标的一半
            int itemStartX;//每个Item开始X轴坐标
            boolean isChecked;
            Item item;
            DrawingMetadata drawingMetadata;
            for (int i = 0; i < this.itemCount; i++) {
                item = this.itemArray[i];
                isChecked = i == this.checkedIndex;
                drawingMetadata = new DrawingMetadata();
                drawingMetadata.text = TSApplication.getResourcesString(item.getTextResourcesId());//获取文本
                drawingMetadata.bitmap = this.getBitmap(isChecked ? item.getAfterIconResourcesId() : item.getBeforeIconResourcesId());//画图标，null代表全部画出
                itemStartX = i * itemWidth;//Item开始X轴坐标
                this.paint.getTextBounds(drawingMetadata.text, 0, drawingMetadata.text.length(), this.rect);//使用一个矩形测量文本需要的高度(rec.height)和宽度(rec.width)
                drawingMetadata.iconTop = (height - iconSize - this.rect.height()) / 2;
                drawingMetadata.iconBottom = drawingMetadata.iconTop + iconSize;//设置图标结束Y轴坐标（与开始的坐标差就是图标的高度）
                drawingMetadata.iconLeft = itemStartX + iconItemStartX;//设置图标开始X轴坐标
                drawingMetadata.iconRight = drawingMetadata.iconLeft + iconSize;//设置图标结束X轴坐标（与开始的坐标差就是图标的宽度）
                drawingMetadata.textStartX = itemStartX + (this.itemWidth - this.rect.width()) / 2;
                drawingMetadata.textBaseLine = height - drawingMetadata.iconTop;
                drawingMetadata.textColor = isChecked ? this.afterTextColor : this.beforeTextColor;
                this.drawingMetadataArray[i] = drawingMetadata;
                if (isChecked) this.switchFragment(item, null);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("TSBottomBar", "onDraw：width=" + getWidth() + ";height=" + getHeight());
        super.onDraw(canvas);//这里让view自身替我们画背景，如果指定的话
        this.paint.setColor(Color.LTGRAY);// 设置边框颜色
        canvas.drawLine(0, 0, getWidth(), 0, this.paint);// 画上边框（其实就是指定开始和结束的两点坐标画一条直线）
        if (this.itemCount != 0) {
            for (DrawingMetadata drawingMetadata : this.drawingMetadataArray) {
                this.rect.top = drawingMetadata.iconTop;
                this.rect.bottom = drawingMetadata.iconBottom;
                this.rect.left = drawingMetadata.iconLeft;
                this.rect.right = drawingMetadata.iconRight;
                this.paint.setAntiAlias(false);//非抗锯齿
                canvas.drawBitmap(drawingMetadata.bitmap, null, this.rect, this.paint);//画图标，null代表全部画出
                this.paint.setAntiAlias(true);//抗锯齿
                this.paint.setColor(drawingMetadata.textColor);//设置字体颜色为选择状态
                canvas.drawText(drawingMetadata.text, drawingMetadata.textStartX, drawingMetadata.textBaseLine, this.paint);// 画文字（第三个参数代表基线）
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("TSBottomBar", "ACTION_DOWN");
                this.target = (int) (event.getX() / this.itemWidth);// 获取当前点击对应的Item下标
                this.handler = new Handler();
                this.runnable = new Runnable() {
                    @Override
                    public void run() {
                        switchItem();
                    }
                };
                this.handler.postDelayed(this.runnable, 1000);//延迟1秒执行
                break;
            case MotionEvent.ACTION_UP:
                Log.i("TSBottomBar", "ACTION_UP");
                this.handler.removeCallbacks(this.runnable);//关闭此定时器
                this.handler.post(this.runnable);//立即执行
        }
        return true;//如果return true,那么表示该方法消费了此次事件,如果return false,那么表示该方法并未处理完全,该事件仍然需要以某种方式传递下去继续等待处理
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.i("TSBottomBar", "onSaveInstanceState");
        Bundle bundle = new Bundle();
        bundle.putInt("checkedIndex", this.checkedIndex);
        if (this.itemArray != null) {
            Item item;
            for (int i = 0; i < this.itemArray.length; i++) {
                item = this.itemArray[i];//item.isAdded()
                Log.i("TSBottomBar", "item.isAdded()=" + item.isAdded());
                if (item.isAdded()) {
                    this.fragmentManager.putFragment(bundle, i + "", item);
                }
            }
        }
        super.onSaveInstanceState();
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Log.i("TSBottomBar", "onRestoreInstanceState");
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            this.setDefaultChecked(bundle.getInt("checkedIndex"));
            Item item;
            int length = this.itemClassArray.length;
            this.itemArray = new Item[length];
            for (int i = 0; i < length; i++) {
                Log.i("TSBottomBar", "onRestoreInstanceState：i=" + i);
                item = (Item) this.fragmentManager.getFragment((Bundle) state, "" + i);
                Log.i("TSBottomBar", "onRestoreInstanceState：item=" + item);
                if (item == null) {
                    item = this.createItemInstance(this.itemClassArray[i]);
                }
                this.itemArray[i] = item;
            }
        }
        super.onRestoreInstanceState(null);
    }

    private Item createItemInstance(Class<? extends Item> itemClass) {
        try {
            return itemClass.newInstance();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Bitmap getBitmap(int resId) {//生成位图
        return BitmapFactory.decodeResource(getContext().getResources(), resId);
    }

    private class DrawingMetadata {//图标绘图数据
        private String text;// 文本内容
        private Bitmap bitmap;//需要绘制的图片
        private int textBaseLine;//文本基线
        private int textStartX;// 文本左边对齐位置（X轴）
        private int textColor;//文本颜色
        private int iconTop;//图标开始Y轴坐标（上下边距一致则垂直居中）
        private int iconBottom;//图标结束Y轴坐标（与开始的坐标差就是图标的高度）
        private int iconLeft;//图标开始X轴坐标
        private int iconRight;//图标结束X轴坐标（与开始的坐标差就是图标的宽度）
    }

    private void switchFragment(Item showItem, Item hideItem) {
        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        Log.i("TSBottomBar", "hideItem：" + hideItem);
        if (hideItem != null) {
            transaction.hide(hideItem);
        }
        Log.i("TSBottomBar", "switchFragment: " + showItem + " ADD：" + showItem.isAdded());
        if (showItem.isAdded()) {//准备显示的页面已经加载过
            transaction.show(showItem);
        } else {
            transaction.add(this.containerId, showItem);
        }
        transaction.commit();
    }

    private void switchItem() {
        Log.i("TSBottomBar", "从" + this.checkedIndex + "切换到：" + this.target);
        if (this.target != this.checkedIndex) {
            Item targetItem = this.itemArray[this.target];
            Item currentItem = this.itemArray[this.checkedIndex];
            this.switchFragment(targetItem, currentItem);
            DrawingMetadata targetDrawingMetadata = this.drawingMetadataArray[this.target];
            DrawingMetadata currentDrawingMetadata = this.drawingMetadataArray[this.checkedIndex];
            targetDrawingMetadata.textColor = this.afterTextColor;
            targetDrawingMetadata.bitmap = this.getBitmap(targetItem.getAfterIconResourcesId());
            currentDrawingMetadata.textColor = this.beforeTextColor;
            currentDrawingMetadata.bitmap = this.getBitmap(currentItem.getBeforeIconResourcesId());
            this.checkedIndex = this.target;
            invalidate();//请求重绘View树,即draw()过程
        }
    }

    /**
     * 底部菜单栏选项
     */
    public static abstract class Item extends Fragment {
        private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //////////

            Log.i("TSBottomBar::Item", "onCreate: "+getActivity());
            /////////
            if (savedInstanceState != null) {
                FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
                if (savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN)) {
                    fragmentManager.hide(this);
                } else {
                    fragmentManager.show(this);
                }
                fragmentManager.commit();
            }
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
        }

        /**
         * 获取选项中文本在资源文件中对应的ID（如：R.string.home_name）
         */
        public abstract int getTextResourcesId();

        /**
         * 获取选项中选择前图片在资源文件中对应的ID（如：R.drawable.icon_home）
         */
        public abstract int getBeforeIconResourcesId();

        /**
         * 获取选项中选择后图片在资源文件中对应的ID（如：R.drawable.icon_home_press）
         */
        public abstract int getAfterIconResourcesId();
    }
}