package com.zxwl.xinji.adapter;

import android.text.Layout;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseSectionMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.siyamed.shapeimageview.RoundedImageView;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.widget.banner.BannerView;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.TBXActivity;
import com.zxwl.xinji.adapter.item.MultipleItem;
import com.zxwl.xinji.adapter.item.NewsListItem;

import java.util.List;

/**
 * 首页适配器
 */
public class NewsListAdapter extends BaseSectionMultiItemQuickAdapter<NewsListItem, BaseViewHolder> {

    public NewsListAdapter(int sectionHeadResId, List<NewsListItem> data) {
        super(sectionHeadResId, data);
        addItemType(MultipleItem.TEXT, R.layout.item_text);
        addItemType(MultipleItem.TEXT_SMALL_IMG, R.layout.item_text_and_small_img);
        addItemType(MultipleItem.TEXT_SMALL_VIDEO, R.layout.item_text_and_small_img);
        addItemType(MultipleItem.TEXT_BIG_IMG, R.layout.item_text_and_big_img);
        addItemType(MultipleItem.TEXT_MULTIPLE_IMG, R.layout.item_text_and_multiple_img);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, NewsListItem item) {
        BannerView bannerView = helper.getView(R.id.banner);

        if (null != item.list) {
            bannerView.delayTime(5).build(item.list);
            bannerView.setVisibility(View.VISIBLE);
            bannerView.setOnItemClickListener(new BannerView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    TBXActivity.startActivity(mContext, item.list.get(position).link);
                }
            });
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsListItem item) {
        helper.setText(R.id.tv_title, item.newsBean.title);
        helper.setText(R.id.tv_website, item.newsBean.announcer);
        helper.setText(R.id.tv_time, DateUtil.longToString(Long.valueOf(item.newsBean.createDate), DateUtil.FORMAT_DATE));

        TextView tv_website = helper.getView(R.id.tv_website);

        TextView tvTime = helper.getView(R.id.tv_time);
        ViewTreeObserver vto = tvTime.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tvTime.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                Layout layout = tvTime.getLayout();
                if (layout != null) {
                    int lines = layout.getLineCount();
                    if (layout.getEllipsisCount(lines - 1) > 0) { //有省略
                        tvTime.setVisibility(View.GONE);
                    } else {
                        tvTime.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        RoundedImageView imageView = null;

        switch (helper.getItemViewType()) {
            case MultipleItem.TEXT:
                break;

            case MultipleItem.TEXT_SMALL_IMG:
                imageView = helper.getView(R.id.iv_img);

                Glide.with(mContext)
                        .asBitmap()
                        .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
                        .load(item.newsBean.pic1)
                        .into(imageView);

                helper.setVisible(R.id.tv_video_time, false);
                break;

            case MultipleItem.TEXT_SMALL_VIDEO:
                imageView = helper.getView(R.id.iv_img);

                Glide.with(mContext)
                        .asBitmap()
                        .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
                        .load(item.newsBean.videoThumbnailUrl)
                        .into(imageView);

                helper.setVisible(R.id.tv_video_time, false);
                break;

            case MultipleItem.TEXT_BIG_IMG:
                imageView = helper.getView(R.id.iv_img);

                Glide.with(mContext)
                        .asBitmap()
                        .apply(new RequestOptions().placeholder(R.mipmap.image_load_err).error(R.mipmap.image_load_err))
                        .load(item.newsBean.pic1)
                        .into(imageView);

                helper.setVisible(R.id.tv_video_time, false);
                break;

            case MultipleItem.TEXT_MULTIPLE_IMG:
                RoundedImageView ivOne = helper.getView(R.id.iv_one);
                RoundedImageView ivTwo = helper.getView(R.id.iv_two);
                RoundedImageView ivThree = helper.getView(R.id.iv_three);

                Glide.with(mContext)
                        .asBitmap()
                        .apply(new RequestOptions().placeholder(R.color.tran).error(R.color.tran))
                        .load(item.newsBean.pic1)
                        .into(ivOne);

                Glide.with(mContext)
                        .asBitmap()
                        .apply(new RequestOptions().placeholder(R.color.tran).error(R.color.tran))
                        .load(item.newsBean.pic2)
                        .into(ivTwo);

                Glide.with(mContext)
                        .asBitmap()
                        .apply(new RequestOptions().placeholder(R.color.tran).error(R.color.tran))
                        .load(item.newsBean.pic3)
                        .into(ivThree);
                break;

            default:
                break;
        }
    }
}
