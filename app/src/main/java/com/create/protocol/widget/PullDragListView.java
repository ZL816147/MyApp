package com.create.protocol.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.create.protocol.R;

/**
 */
public class PullDragListView extends ListView implements OnScrollListener {

    public int scrollY;
    private OnScrollYListener onScrollYListener;

    // 列表刷新状态
    private enum DListViewState {
        LV_NORMAL, // 正常
        LV_PULL_REFRESH, // 下拉
        LV_RELEASE_REFRESH, // 释放
        LV_LOADING// 加载
    }

    // 列表加载状态
    private enum DListViewLoadingMore {
        LV_NORMAL, LV_LOADING, LV_OVER
    }

    private View mHeadView;// 头部控件
    private TextView mRefreshTextview;
    private TextView mLastUpdateTextView;
    private ImageView mArrowImageView;
    private ProgressBar mHeadProgressBar;

    // private int mHeadViewWidth; // 头部布局的宽高
    private int mHeadViewHeight;

    private View mFootView;// 底部控件
    private View mLoadMoreView;
    private TextView mLoadMoreTextView;
    private View mLoadingView;

    private Animation animation, reverseAnimation;// 动画效果

    private int mFirstItemIndex = -1;//

    private boolean mIsRecord = false;

    private int mStartY, mMoveY;// 手指的Y坐标

    private DListViewState mlistViewState = DListViewState.LV_NORMAL;

    private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_NORMAL;

    private final static int RATIO = 2;//

    private boolean mBack = false;//

    private OnRefreshLoadingMoreListener onRefreshLoadingMoreListener;// 自定义监听事件

    private boolean isScroller = true;
    /**
     * 上次更新时间的毫秒值
     */
    private long lastUpdateTime;
    /**
     * 用于存储上次更新时间
     */
    private SharedPreferences preferences;
    /**
     * 一分钟的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     * 一小时的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_HOUR = 60 * ONE_MINUTE;

    /**
     * 一天的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_DAY = 24 * ONE_HOUR;

    /**
     * 一月的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_MONTH = 30 * ONE_DAY;

    /**
     * 一年的毫秒值，用于判断上次的更新时间
     */
    public static final long ONE_YEAR = 12 * ONE_MONTH;

    /**
     * 上次更新时间的字符串常量，用于作为SharedPreferences的键值
     */
    private static final String UPDATED_AT = "updated_at";
    private int mId;

    public PullDragListView(Context context) {
        super(context, null);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        initDragListView(context);
        refreshUpdatedAtValue();
        setFooterDividersEnabled(false);
    }

    public PullDragListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        initDragListView(context);
        refreshUpdatedAtValue();
        setFooterDividersEnabled(false);
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    // 设置监听事件
    public void setOnRefreshListener(OnRefreshLoadingMoreListener onRefreshLoadingMoreListener, int id) {
        this.onRefreshLoadingMoreListener = onRefreshLoadingMoreListener;
        mId = id;
    }

    // 初始化控件
    public void initDragListView(Context context) {
        initHeadView(context);
        initLoadMoreView(context);
        setOnScrollListener(this);
    }

    public void initHeadView(Context context) {
        mHeadView = View.inflate(context, R.layout.head, null);
        mArrowImageView = mHeadView.findViewById(R.id.head_arrowImageView);
        mArrowImageView.setMinimumWidth(60);

        mHeadProgressBar = mHeadView.findViewById(R.id.head_progressBar);
        // 提示文本
        mRefreshTextview = mHeadView.findViewById(R.id.head_tipsTextView);

        mLastUpdateTextView = mHeadView.findViewById(R.id.head_lastUpdatedTextView);

        measureView(mHeadView);
        // 明确宽高
        // mHeadViewWidth = mHeadView.getMeasuredWidth();
        mHeadViewHeight = mHeadView.getMeasuredHeight();

        addHeaderView(mHeadView, null, false);//
        // 设置头部控件的内边距
        mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
        initAnimation();// 初始化箭头的动画效果
    }

    /**
     * 是否有加载更多
     */
    private boolean loadMoreAble = true;

    // 初始化底部加载数据的控件
    private void initLoadMoreView(Context context) {

        mFootView = View.inflate(context, R.layout.footer, null);

        mLoadMoreView = mFootView.findViewById(R.id.load_more_view);

        mLoadMoreTextView = mFootView.findViewById(R.id.load_more_tv);

        mLoadingView = mFootView.findViewById(R.id.loading_layout);
        addFooterView(mFootView);
    }

    private void initAnimation() {
        animation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(250);
        animation.setFillAfter(true);//
        //
        reverseAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        reverseAnimation.setInterpolator(new LinearInterpolator());
        reverseAnimation.setDuration(250);
        reverseAnimation.setFillAfter(true);
    }

    /**
     * 父布局传递给子布局的布局要求
     */
    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    // 监听手指的触摸事件
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN:
                doActionDown(ev);
                break;
            // 手指移动
            case MotionEvent.ACTION_MOVE:
                doActionMove(ev);
                break;
            // ̧手指提起
            case MotionEvent.ACTION_UP:
                doActionUp(ev);
                break;
            default:
                break;
        }

        if (isScroller) {
            return super.onTouchEvent(ev);
        } else {
            return true;
        }

    }

    void doActionDown(MotionEvent event) {
        if (mIsRecord == false && mFirstItemIndex == 0) {
            mStartY = (int) event.getY();
            mIsRecord = true;
        }
    }

    void doActionMove(MotionEvent event) {
        mMoveY = (int) event.getY();
        //
        if (mIsRecord == false && mFirstItemIndex == 0) {
            mStartY = (int) event.getY();
            mIsRecord = true;
        }

        if (mIsRecord == false || mlistViewState == DListViewState.LV_LOADING) {
            return;
        }
        //
        int offset = (mMoveY - mStartY) / RATIO;

        switch (mlistViewState) {
            case LV_NORMAL: {
                //
                if (offset > 0) {

                    mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
                    switchViewState(DListViewState.LV_PULL_REFRESH);
                }

            }
            break;

            case LV_PULL_REFRESH: {
                setSelection(0);
                mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
                if (offset < 0) {

                    isScroller = false;
                    switchViewState(DListViewState.LV_NORMAL);
                } else if (offset > mHeadViewHeight) {
                    switchViewState(DListViewState.LV_RELEASE_REFRESH);
                }
            }
            break;

            case LV_RELEASE_REFRESH: {
                setSelection(0);
                mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);
                if (offset >= 0 && offset <= mHeadViewHeight) {
                    mBack = true;
                    switchViewState(DListViewState.LV_PULL_REFRESH);
                } else if (offset < 0) {
                    switchViewState(DListViewState.LV_NORMAL);
                } else {

                }
            }
            break;
            default:
                return;
        }
    }

    public void doActionUp(MotionEvent event) {
        mIsRecord = false;
        isScroller = true;
        mBack = false;
        if (mlistViewState == DListViewState.LV_LOADING) {
            return;
        }
        switch (mlistViewState) {
            case LV_NORMAL:

                break;
            case LV_PULL_REFRESH:
                mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
                switchViewState(DListViewState.LV_NORMAL);
                break;
            case LV_RELEASE_REFRESH:
                mHeadView.setPadding(0, 0, 0, 0);
                switchViewState(DListViewState.LV_LOADING);
                onRefresh();
                break;
            default:
                break;
        }
    }

    private void switchViewState(DListViewState state) {

        switch (state) {
            case LV_NORMAL: {
                mArrowImageView.clearAnimation();
                mArrowImageView.setImageResource(R.drawable.arrow);
            }
            break;
            case LV_PULL_REFRESH: {
                mHeadProgressBar.setVisibility(View.GONE);
                mArrowImageView.setVisibility(View.VISIBLE);
                mRefreshTextview.setText(getResources().getString(R.string.refresh_pull));
                mArrowImageView.clearAnimation();

                if (mBack) {
                    mBack = false;
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(reverseAnimation);//
                }
            }
            break;
            case LV_RELEASE_REFRESH: {
                mHeadProgressBar.setVisibility(View.GONE);
                mArrowImageView.setVisibility(View.VISIBLE);
                mRefreshTextview.setText(getResources().getString(R.string.refresh_release_to_update));
                mArrowImageView.clearAnimation();
                mArrowImageView.startAnimation(animation);
            }
            break;
            case LV_LOADING: {
                mHeadProgressBar.setVisibility(View.VISIBLE);
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(View.GONE);
                mRefreshTextview.setText(getResources().getString(R.string.refresh_loading));
            }
            break;
            default:
                return;
        }
        mlistViewState = state;
        refreshUpdatedAtValue();
    }

    public void onRefresh() {
        if (onRefreshLoadingMoreListener != null) {
            onRefreshLoadingMoreListener.onRefresh();
        }
        setSelection(0);
    }

    public void onRefreshComplete() {
        mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
        switchViewState(DListViewState.LV_NORMAL);
        preferences.edit().putLong(UPDATED_AT + mId, System.currentTimeMillis()).commit();
    }

    public void startRefresh() {
        mHeadView.setPadding(0, 0, 0, 0);
        switchViewState(DListViewState.LV_LOADING);
        if (onRefreshLoadingMoreListener != null) {
            onRefreshLoadingMoreListener.onRefresh();
        }
        setSelection(0);
    }

    public void onLoadMoreComplete(boolean loadFinish) {
        if (loadFinish) {
            updateLoadMoreViewState(DListViewLoadingMore.LV_OVER);
        } else {
            updateLoadMoreViewState(DListViewLoadingMore.LV_NORMAL);
        }
    }

    private void updateLoadMoreViewState(DListViewLoadingMore state) {
        switch (state) {
            case LV_NORMAL:
                mLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText(getResources().getString(R.string.load_more));
                break;

            case LV_LOADING:
                mLoadingView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setVisibility(View.GONE);
                break;

            case LV_OVER:
                mLoadingView.setVisibility(View.GONE);
                mLoadMoreTextView.setVisibility(View.VISIBLE);
                mLoadMoreTextView.setText(getResources().getString(R.string.no_more_data));
                break;
            default:
                break;
        }
        loadingMoreState = state;
    }

    private void refreshUpdatedAtValue() {
        lastUpdateTime = preferences.getLong(UPDATED_AT + mId, -1);
        long currentTime = System.currentTimeMillis();
        long timePassed = currentTime - lastUpdateTime;
        long timeIntoFormat;
        String updateAtValue;
        if (lastUpdateTime == -1) {
            updateAtValue = getResources().getString(R.string.not_updated_yet);
        } else if (timePassed < 0) {
            updateAtValue = getResources().getString(R.string.time_error);
        } else if (timePassed < ONE_MINUTE) {
            updateAtValue = getResources().getString(R.string.updated_just_now);
        } else if (timePassed < ONE_HOUR) {
            timeIntoFormat = timePassed / ONE_MINUTE;
            String value = timeIntoFormat + getResources().getString(R.string.minute);
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_DAY) {
            timeIntoFormat = timePassed / ONE_HOUR;
            String value = timeIntoFormat + getResources().getString(R.string.hour);
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_MONTH) {
            timeIntoFormat = timePassed / ONE_DAY;
            String value = timeIntoFormat + getResources().getString(R.string.day);
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else if (timePassed < ONE_YEAR) {
            timeIntoFormat = timePassed / ONE_MONTH;
            String value = timeIntoFormat + getResources().getString(R.string.unit_month);
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        } else {
            timeIntoFormat = timePassed / ONE_YEAR;
            String value = timeIntoFormat + getResources().getString(R.string.year);
            updateAtValue = String.format(getResources().getString(R.string.updated_at), value);
        }
        // 设置更新时间
        mLastUpdateTextView.setText(updateAtValue);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public SparseArrayCompat recordSp = new SparseArrayCompat<>(0);

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // Log.d("--zy--", "AbsListView onScroll() firstVisibleItem: " +
        // firstVisibleItem + " visibleItemCount: " + visibleItemCount + "
        // totalItemCount: " + totalItemCount);
        mFirstItemIndex = firstVisibleItem;
        View firstView = view.getChildAt(0);
        if (null != firstView) {
            ItemRecod itemRecord = (ItemRecod) recordSp.get(firstVisibleItem);
            if (null == itemRecord) {
                itemRecord = new ItemRecod();
            }
            itemRecord.height = firstView.getHeight();
            itemRecord.top = firstView.getTop();
            recordSp.append(firstVisibleItem, itemRecord);
            //滚动距离
            scrollY = scrollY();
            if (onScrollYListener != null) {
                onScrollYListener.setY(scrollY);
            }
        }

        int i = getLastVisiblePosition();
        if (visibleItemCount < getCount()) {
            if (loadMoreAble)
                mFootView.setVisibility(View.VISIBLE);
            if (i > 0 && i == totalItemCount - 1) {
                if (onRefreshLoadingMoreListener != null && loadingMoreState == DListViewLoadingMore.LV_NORMAL) {
                    updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
                    onRefreshLoadingMoreListener.onLoadMore();
                }
            }
        } else {
            mFootView.setVisibility(View.GONE);
        }
    }

    public interface OnRefreshLoadingMoreListener {

        void onRefresh();

        void onLoadMore();
    }

    // 设置监听事件
    public void setOnScrollYListener(OnScrollYListener onScrollYListener) {
        this.onScrollYListener = onScrollYListener;
    }

    public interface OnScrollYListener {

        void setY(int scrollY);
    }


    public void dismissFooterView() {
        loadMoreAble = false;
        removeFooterView(mFootView);
        mFootView.setVisibility(View.GONE);
    }

    public int scrollY() {
        int height = 0;
        for (int i = 0; i < mFirstItemIndex; i++) {
            ItemRecod itemRecod = (ItemRecod) recordSp.get(i);
            if (itemRecod != null) {
                height += itemRecod.height;
            }
        }
        ItemRecod itemRecod = (ItemRecod) recordSp.get(mFirstItemIndex);
        if (null == itemRecod) {
            itemRecod = new ItemRecod();
        }
        return height - itemRecod.top;
    }

    static class ItemRecod {
        int height = 0;
        int top = 0;
    }
}
