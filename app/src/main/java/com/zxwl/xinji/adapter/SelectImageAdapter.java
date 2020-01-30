package com.zxwl.xinji.adapter;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.DateUtils;
import com.luck.picture.lib.tools.StringUtils;
import com.zxwl.xinji.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.pictureselector.adapter
 * email：893855882@qq.com
 * data：16/7/27
 * 图片展示适配器
 */
public class SelectImageAdapter extends BaseQuickAdapter<LocalMedia, BaseViewHolder> {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 3;

    private boolean select = true;//是否选择

    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public SelectImageAdapter(int layoutResId, @Nullable List<LocalMedia> data, onAddPicClickListener mOnAddPicClickListener) {
        super(layoutResId, data);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        LinearLayout ll_del;
        TextView tv_duration;

        public ViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        if (mData.size() < selectMax) {
            return mData.size() + 1;
        } else {
            return mData.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalMedia item) {
        ImageView imageView = helper.getView(R.id.fiv);
        LinearLayout llDel = helper.getView(R.id.ll_del);
        TextView tvDuration = helper.getView(R.id.tv_duration);

        //少于8张，显示继续添加的图标
        if (getItemViewType(helper.getPosition()) == TYPE_CAMERA) {
            imageView.setImageResource(R.mipmap.ic_select_add);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnAddPicClickListener) {
                        mOnAddPicClickListener.onAddPicClick();
                    }
                }
            });
            llDel.setVisibility(View.INVISIBLE);
        } else {
            //如果只是作为显示用
            llDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = helper.getPosition();
                    // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                    // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                    if (index != RecyclerView.NO_POSITION) {
//                        LocalMedia remove = list.remove(index);
                        LocalMedia remove = mData.remove(index);
                        notifyItemRemoved(index);
//                        notifyItemRangeChanged(index, list.size());
                        notifyItemRangeChanged(index, mData.size());

                        if (null != mDelClickListener) {
                            mDelClickListener.onItemClick(remove);
                        }
                    }
                }
            });

            LocalMedia media = mData.get(helper.getPosition());
            int mimeType = media.getMimeType();
            String path = "";
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }
            // 图片
            if (media.isCompressed()) {
                Log.i("compress image result:", new File(media.getCompressPath()).length() / 1024 + "k");
                Log.i("压缩地址::", media.getCompressPath());
            }

            Log.i("原图地址::", media.getPath());
            int pictureType = PictureMimeType.isPictureType(media.getPictureType());
            if (media.isCut()) {
                Log.i("裁剪地址::", media.getCutPath());
            }
            long duration = media.getDuration();
            tvDuration.setVisibility(pictureType == PictureConfig.TYPE_VIDEO
                    ? View.VISIBLE : View.GONE);
            if (mimeType == PictureMimeType.ofAudio()) {
                tvDuration.setVisibility(View.VISIBLE);
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.picture_audio);
                StringUtils.modifyTextViewDrawable(tvDuration, drawable, 0);
            } else {
                Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.video_icon);
                StringUtils.modifyTextViewDrawable(tvDuration, drawable, 0);
            }
            tvDuration.setText(DateUtils.timeParse(duration));
            if (mimeType == PictureMimeType.ofAudio()) {
                imageView.setImageResource(R.drawable.audio_placeholder);
            } else {
                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.color.color_f6f6f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);
                Glide.with(mContext)
                        .load(path)
                        .apply(options)
                        .into(imageView);
            }
        }
    }

    private boolean isShowAddItem(int position) {
        int size = 0 == mData.size() ? 0 : mData.size();
        return position == size;
    }

    protected OnDelClickListener mDelClickListener;

    public interface OnDelClickListener {
        //        void onItemClick(int position, View v);
        void onItemClick(LocalMedia localMedia);
    }

    public void setOnDelClickListener(OnDelClickListener listener) {
        this.mDelClickListener = listener;
    }
}
