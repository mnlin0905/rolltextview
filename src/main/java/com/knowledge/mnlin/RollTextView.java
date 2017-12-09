package com.knowledge.mnlin;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;

import com.knowledge.mnlin.rolltextview.R;

import java.util.ArrayList;
import java.util.List;

/**
 * function :
 *
 * @author ACChain
 * @date 2017/12/7
 */

public class RollTextView<T extends Object> extends RecyclerView {
    /**
     * 监听可见性
     */
    ViewTreeObserver.OnWindowFocusChangeListener onWindowFocusChangeListener;

    /**
     * 信息源，泛型，可以通过toString方法获取对应的字符串
     */
    private List<T> list;

    /**
     * 当前是否正在自动执行动画
     */
    private transient boolean isAutoAnimate;

    /**
     * 适配器
     */
    private RollAdapter<T> adapter;

    /**
     * 每次滚动间隔的时间
     */
    private long interval = 2000;

    /**
     * 动画处理handler
     */
    private Handler handler;

    /**
     * handle中的处理函数
     */
    private Runnable callback;

    /**
     * 滚动方向，默认为true
     * <p>
     * 0 表示向上滚动
     * 1 表示向下滚动
     * 2 表示向右滚动
     * 3 表示向左滚动
     */
    private int direction;

    /**
     * 一次显示的数目
     */
    private int appearCount = 1;

    /**
     * 记录上个刚出现的position的位置
     */
    private int currentPosition;

    /**
     * 是否可以手指来滚动，默认为true
     */
    private boolean canScrollByHand = true;

    public RollTextView(Context context) {
        this(context, null);
    }

    public RollTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RollTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setMinimumHeight(context.getResources().getDimensionPixelSize(R.dimen.prefer_view_height));

        handler = new Handler(context.getMainLooper());

        list = new ArrayList<>();
        adapter = new RollAdapter<T>(context, list);
        setAdapter(adapter);
        super.setLayoutManager(new LinearLayoutManager(context, OrientationHelper.VERTICAL, false));

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (!isAutoAnimate && isVisible()) {
                    post(() -> startAnimation());
                }
            }
        });

        setOnTouchListener((v, event) -> true);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
    }

    /**
     * 设定该recycle的高度为item的高度
     */
    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        int height = getMeasuredHeight();
        if (getChildCount() != 0) {
            height = getChildAt(0).getHeight() * appearCount;
        }
        setMeasuredDimension(getMeasuredWidth(), height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return super.onTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 添加信息源
     */
    public void refreshData(List list) {
        if (list == null || list.size() == 0) {
            return;
        }

        this.list.clear();
        this.list.addAll(list);

        //设定每次显示的内容不能超过所有的内容
        if (list.size() < appearCount) {
            appearCount = list.size();
        }
        //如果横向滚动，默认高度只能是1
        if (direction == 2 || direction == 3) {
            appearCount = 1;
        }

        //截取头部或尾部进行数据添加
        //为了循环滚动，需要在头尾添加n个元素，保证正常滚动
        if (direction == 0 || direction == 3) {
            List header = list.subList(0, appearCount);
            this.list.addAll(header);
        } else {
            List tail = list.subList(list.size() - appearCount, list.size());
            this.list.addAll(0, tail);
        }

        //设置水平或竖直滚动
        super.setLayoutManager(new LinearLayoutManager(getContext(), (direction == 0 || direction == 1) ? OrientationHelper.VERTICAL : OrientationHelper.HORIZONTAL, false) {
            @Override
            public boolean canScrollVertically() {
                return super.canScrollVertically();
            }
        });

        adapter.setDirectionAndAppearCount(direction, appearCount);

        //如果数据为再次刷新出现，则先停止动画，然后加载数据，然后开启动画
        if (isAutoAnimate) {
            stopAnimation(1);
        }
        adapter.notifyDataSetChanged();
        postDelayed(this::startAnimation, 200);
    }

    /**
     * 禁止主动调用该方法
     */
    @Override
    @Deprecated
    public final void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
    }

    /**
     * 任务重复执行
     */
    private synchronized void intervalRun() {
        if (!(getLayoutManager() instanceof LinearLayoutManager)) {
            throw new RuntimeException("默认只能使用 LinearLayoutManager");
        }
        LinearLayoutManager manager = (LinearLayoutManager) getLayoutManager();
        int firstPosition = manager.findFirstVisibleItemPosition();
        int lastPosition = manager.findLastVisibleItemPosition();

        //检查当前滚动是否错位，错位则更改
        if (lastPosition - firstPosition + 1 != getMeasuredHeight() / getChildAt(0).getMeasuredHeight()) {
            post(this::startAnimation);
            return;
        }

        //如果在某个时刻isAutoAnimate发生变化，则停止动画
        if (!isAutoAnimate) {
            stopAnimation(3);
            return;
        }

        Log.v(getClass().getSimpleName(), "动画执行一次。。。" + "当前最后和最开始的位置：" + firstPosition + "   " + lastPosition);

        if (direction == 0 && lastPosition == list.size() - 1) {
            scrollToPosition(currentPosition = 0);
        }
        if (direction == 1 && firstPosition == 0) {
            scrollToPosition(currentPosition = list.size() - 1);
        }
        if (direction == 2 && firstPosition == 0) {
            scrollToPosition(currentPosition = list.size() - 1);
        }
        if (direction == 3 && lastPosition == list.size() - 1) {
            scrollToPosition(0);
        }

        //让上面内容先滚动结束，然后再开启滚动流程
        post(() -> {
            switch (direction) {
                case 0:
                    smoothScrollBy(0, getChildAt(0).getHeight());
                    break;
                case 1:
                    smoothScrollBy(0, -getChildAt(0).getHeight());
                    break;
                case 2:
                    smoothScrollBy(-getChildAt(0).getWidth(), 0);
                    break;
                case 3:
                    smoothScrollBy(getChildAt(0).getWidth(), 0);
                    break;
            }
            callback = this::intervalRun;
            handler.postDelayed(callback, interval);
        });
    }

/*    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (onWindowFocusChangeListener != null) {
            getViewTreeObserver().removeOnWindowFocusChangeListener(onWindowFocusChangeListener);
        }
    }*/

/*    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (onWindowFocusChangeListener == null) {
            onWindowFocusChangeListener = hasFocus -> {
                if (isVisible() && !isAutoAnimate) {
                    post(this::startAnimation);
                    Log.v(getClass().getSimpleName(), "动画开启，view可见。。。" );
                }
                if (!isVisible() && isAutoAnimate) {
                    post(()->this.stopAnimation(4));
                    Log.v(getClass().getSimpleName(), "动画结束，view消失。。。"  );
                }
            };
        }
        getViewTreeObserver().addOnWindowFocusChangeListener(onWindowFocusChangeListener);
    }*/

    /**
     * 判断当前view是否可见
     */
    private boolean isVisible() {
        boolean isVisible = false;

        //检测是否被父元素遮挡
        Rect rect = new Rect();
        boolean can = getLocalVisibleRect(rect);
        if (can) {
            isVisible = !(getBottom() < rect.top || getTop() > rect.bottom || getLeft() > rect.right || getRight() < rect.left);
        }

        /*
        * 检测可见性：
        * 1、查看view是否在当前的window窗口内；
        * 2、检测自身以及所有的父view是否为VISIBLE*/
        return isVisible && hasWindowFocus() && isShown();
    }

    /**
     * 设置一次显示的条数
     */
    public RollTextView<T> setAppearCount(int count) {
        this.appearCount = count;
        return this;
    }

    /**
     * 添加itemclick事件监听
     */
    public RollTextView setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        adapter.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * 设置间隔时间
     */
    public RollTextView setInterval(long interval) {
        this.interval = interval;
        return this;
    }

    /**
     * 设置view是否可以手动滑动，默认为true
     */
    private RollTextView setCanScroll(boolean canScrollByHand) {
        this.canScrollByHand = canScrollByHand;
        return this;
    }

    /**
     * 设置滚动方向
     * <p>
     * 0 表示向上滚动
     * 1 表示向下滚动
     * 2 表示向右滚动
     * 3 表示向左滚动
     */
    public RollTextView setRollDirection(int direction) {
        this.direction = direction;
        return this;
    }

    /**
     * 设置序号是否可见
     */
    public RollTextView setOrderVisible(boolean orderVisible) {
        adapter.setOrderVisible(orderVisible);
        return this;
    }

    /**
     * 设置文本尾部显示的内容，可为空
     */
    public RollTextView setEndText(String jump, boolean jumpVisible) {
        adapter.setEndText(jump, jumpVisible);
        return this;
    }

    /**
     * 设置layout文件，可自定义，但必须有三个变量存在:tv_left;tv_center;tv_right
     */
    public RollTextView setLayoutResource(@LayoutRes int layoutResource) {
        adapter.setLayoutResource(layoutResource);
        return this;
    }

    /**
     * 开启动画
     * <p>
     * 多次开启animation的话，默认会再次从第一个元素开始滚动
     */
    public synchronized void startAnimation() {
        if (list == null || list.size() == 0 || getChildCount() == 0) {
            return;
        }
        if (callback != null) {
            handler.removeCallbacks(callback);
        }
        scrollToPosition(currentPosition = 0);
        isAutoAnimate = true;
        intervalRun();
    }

    /**
     * 关闭动画
     */
    public synchronized void stopAnimation(int i) {
        if (list == null || list.size() == 0 || getChildCount() == 0) {
            return;
        }
        if (callback != null) {
            handler.removeCallbacks(callback);
        }
        isAutoAnimate = false;
    }
}
