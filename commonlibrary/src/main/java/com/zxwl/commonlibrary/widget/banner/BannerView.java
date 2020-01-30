package com.zxwl.commonlibrary.widget.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.zxwl.commonlibrary.R;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.network.bean.response.BannerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hcc on 16/8/4 21:18
 * 100332338@qq.com
 * <p/>
 * 自定义Banner无限轮播控件
 */
public class BannerView extends RelativeLayout implements BannerAdapter.ViewPagerOnItemClickListener {
    //    private ViewPager viewPager;
    private NotScrollViewPager viewPager;

    private LinearLayout points;
    private View viewLine;

    private CompositeSubscription compositeSubscription;
    //默认轮播时间，10s
    private int delayTime = 5;
    private List<LinearLayout> imageViewList;
    private List<BannerEntity> bannerList;

    //选中显示Indicator
    private int selectRes = R.drawable.shape_dots_select;

    //非选中显示Indicator
    private int unSelcetRes = R.drawable.shape_dots_unselect;

    //当前页的下标
    private int currentPos;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_banner, this, true);

        viewPager = (NotScrollViewPager) inflate.findViewById(R.id.layout_banner_viewpager);
        points = (LinearLayout) inflate.findViewById(R.id.layout_banner_points_group);
        imageViewList = new ArrayList<>();
    }

    /**
     * 设置轮播间隔时间
     *
     * @param time 轮播间隔时间，单位秒
     */
    public BannerView delayTime(int time) {
        this.delayTime = time;
        return this;
    }


    /**
     * 设置Points资源 Res
     *
     * @param selectRes   选中状态
     * @param unselcetRes 非选中状态
     */
    public void setPointsRes(int selectRes, int unselcetRes) {
        this.selectRes = selectRes;
        this.unSelcetRes = unselcetRes;
    }

    private int pointWidth;//point的宽度

    /**
     * 图片轮播需要传入参数
     */
    public void build(List<BannerEntity> list) {
        destroy();

        if (list.size() == 0) {
            this.setVisibility(GONE);
            return;
        }

        imageViewList.clear();

        bannerList = new ArrayList<>();
        bannerList.addAll(list);
        final int pointSize;
        pointSize = bannerList.size();

        if (pointSize == 2) {
            bannerList.addAll(list);
        }
        //判断是否清空 指示器点
        if (points.getChildCount() != 0) {
            points.removeAllViewsInLayout();
        }

        //初始化与个数相同的指示器点
        for (int i = 0; i < pointSize; i++) {
            View dot = new View(getContext());
            dot.setBackgroundResource(unSelcetRes);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    DisplayUtil.dp2px(0 == i ? 25 : 6),
                    DisplayUtil.dp2px(6));
            params.leftMargin = 10;
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            points.addView(dot);
        }

        final View childAt = (View) points.getChildAt(0);
        childAt.setBackgroundResource(selectRes);

        ViewTreeObserver vto = childAt.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                childAt.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                pointWidth = childAt.getWidth();
            }
        });

        for (int i = 0; i < bannerList.size(); i++) {
            //初始化图片
            LinearLayout linearLayout = (LinearLayout) View.inflate(getContext(), R.layout.item_banner, null);
            RoundedImageView imageView = (RoundedImageView) linearLayout.findViewById(R.id.iv_img);
            TextView textView = (TextView) linearLayout.findViewById(R.id.tv_tilte);
            Glide.with(getContext())
                    .asBitmap()
                    .apply(new RequestOptions().error(R.mipmap.image_load_err))
                    .load(bannerList.get(i).url)
//                    .load(Urls.BASE_URL + bannerList.get(i).url)
                    .into(imageView);

            textView.setText(bannerList.get(i).name);

            final int finalI = i;
            linearLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(finalI);
                }
            });

            imageViewList.add(linearLayout);
        }

        //监听图片轮播，改变指示器状态
        viewPager.clearOnPageChangeListeners();

        //图片大于1才允许滑动
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int pos) {
                pos = pos % pointSize;
                currentPos = pos;
                for (int i = 0; i < points.getChildCount(); i++) {
                    View indexChild = points.getChildAt(i);
                    indexChild.setBackgroundResource(unSelcetRes);
                    ViewGroup.LayoutParams layoutParams = indexChild.getLayoutParams();
                    layoutParams.width = DisplayUtil.dp2px ( 6);
                    layoutParams.height = DisplayUtil.dp2px( 6);
                    indexChild.setLayoutParams(layoutParams);
                }

                View selectView = points.getChildAt(pos);
                selectView.setBackgroundResource(selectRes);
                ViewGroup.LayoutParams layoutParams = selectView.getLayoutParams();
                layoutParams.width = DisplayUtil.dp2px(  20);
                layoutParams.height = DisplayUtil.dp2px( 6);
                selectView.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (isStopScroll) {
                            startScroll();
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        stopScroll();
                        if (null != compositeSubscription) {
                            compositeSubscription.unsubscribe();
                        }
                        break;
                }
            }
        });

        viewPager.setScroll(imageViewList.size()>1);

        BannerAdapter bannerAdapter = new BannerAdapter(imageViewList);
        viewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        bannerAdapter.setmViewPagerOnItemClickListener(this);

        //图片开始轮播
        startScroll();
    }

    private boolean isStopScroll = false;

    /**
     * 图片开始轮播
     */
    private void startScroll() {
        //图片大于1才轮播
        if (imageViewList.size() > 1) {
            compositeSubscription = new CompositeSubscription();
            isStopScroll = false;
            Subscription subscription = Observable.timer(delayTime, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (isStopScroll) {
                                return;
                            }
                            isStopScroll = true;
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        }
                    });
            compositeSubscription.add(subscription);
        }
    }

    /**
     * 图片停止轮播
     */
    private void stopScroll() {
        isStopScroll = true;
    }


    public void destroy() {
        if (compositeSubscription != null) {
            compositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (null != onItemClickListener) {
            onItemClickListener.onItemClick(position);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
