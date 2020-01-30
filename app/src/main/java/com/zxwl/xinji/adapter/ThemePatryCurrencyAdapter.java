package com.zxwl.xinji.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.network.bean.response.BfhdzsBean;
import com.zxwl.network.bean.response.BfzrrglBean;
import com.zxwl.network.bean.response.CmswdbBean;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.CwgkBean;
import com.zxwl.network.bean.response.DzzhjBean;
import com.zxwl.network.bean.response.NoteBean;
import com.zxwl.network.bean.response.SgdzBean;
import com.zxwl.network.bean.response.ThemePartyBean;
import com.zxwl.network.bean.response.WxyBean;
import com.zxwl.network.bean.response.XspbBean;
import com.zxwl.network.bean.response.XwqlqdBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.RefreshRecyclerActivity;
import com.zxwl.xinji.fragment.RefreshRecyclerFragment;

import java.util.List;

/**
 * 主题党日-->
 * 民主评议-->
 * 组织生活会-->
 * 其他-->
 * 政治理论-->
 * 党务公开-->
 * 微心愿-->
 * 线上评比-->
 * 帮扶责任人管理-->
 * <p>
 * <p>
 * 列表适配器
 */
public class ThemePatryCurrencyAdapter extends BaseQuickAdapter<CurrencyBean, BaseViewHolder> {
    private String type;

    public ThemePatryCurrencyAdapter(int layoutResId, @Nullable List<CurrencyBean> data) {
        super(layoutResId, data);
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, CurrencyBean item) {
        TextView tvTilte = helper.getView(R.id.tv_tilte);
        TextView tvContent = helper.getView(R.id.tv_content);
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvWebsite = helper.getView(R.id.tv_website);

        TextView tvClaimStatus = null;

        CwgkBean cwgkBean = null;
        DzzhjBean dzzhjBean = null;
        XwqlqdBean xwqlqdBean = null;

        String startTime = "";
        String endTime = "";
        boolean isYearEqual = false;

        String unitName = "辛集市";
        String createName = "";

        switch (type) {
            //记事本
            case RefreshRecyclerActivity
                    .TYPE_JSB:
                NoteBean noteBean = ((NoteBean) item);
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvTilte.setText(noteBean.title);
                tvWebsite.setText(DateUtil.longToString(noteBean.createTime, DateUtil.FORMAT_DATE_TIME));
                tvContent.setText(noteBean.context);
                tvContent.setSingleLine(false);
                tvContent.setMaxLines(2);
                tvContent.setEllipsize(TextUtils.TruncateAt.END);
                tvTime.setVisibility(View.GONE);
                break;

            //党务公开
            case RefreshRecyclerActivity
                    .TYPE_DWGK:
                cwgkBean = ((CwgkBean) item);
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_dwgk, 0, 0, 0);
                tvTilte.setText(cwgkBean.theme);
                tvTime.setText(DateUtil.longToString(cwgkBean.createtime, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(cwgkBean.villagename)) {
                    unitName = cwgkBean.villagename;
                } else if (!TextUtils.isEmpty(cwgkBean.vtownsname)) {
                    unitName = cwgkBean.vtownsname;
                }

                createName = TextUtils.isEmpty(cwgkBean.creatorName) ? "" : cwgkBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                startTime = DateUtil.longToString(cwgkBean.publicityTime, DateUtil.FORMAT_DATE_CHINA);
                endTime = DateUtil.longToString(cwgkBean.outtime, DateUtil.FORMAT_DATE_CHINA);

                isYearEqual = startTime.startsWith(endTime.substring(0, 4));

                //如果年相同
                if (isYearEqual) {
                    tvContent.setText("公示时间:" + startTime + "至" + endTime.substring(5));
                } else {
                    tvContent.setText("公示时间:" + startTime + "至" + endTime);
                }
                break;

            //村务公开
            case RefreshRecyclerActivity
                    .TYPE_CWGK:
                cwgkBean = (CwgkBean) item;

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_cwgk, 0, 0, 0);

                tvTilte.setText(cwgkBean.theme);
                tvTime.setText(DateUtil.longToString(cwgkBean.createtime, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(cwgkBean.villagename)) {
                    unitName = cwgkBean.villagename;
                } else if (!TextUtils.isEmpty(cwgkBean.vtownsname)) {
                    unitName = cwgkBean.vtownsname;
                }

                createName = TextUtils.isEmpty(cwgkBean.creatorName) ? "" : cwgkBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                startTime = DateUtil.longToString(cwgkBean.publicityTime, DateUtil.FORMAT_DATE_CHINA);
                endTime = DateUtil.longToString(cwgkBean.outtime, DateUtil.FORMAT_DATE_CHINA);
                isYearEqual = startTime.startsWith(endTime.substring(0, 4));

                //如果年相同
                if (isYearEqual) {
                    tvContent.setText("公示时间:" + startTime + "至" + endTime.substring(5));
                } else {
                    tvContent.setText("公示时间:" + startTime + "至" + endTime);
                }
                break;

            //党组织换届
            case RefreshRecyclerActivity
                    .TYPE_DZZHJ:
                dzzhjBean = (DzzhjBean) item;

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_dzzhj, 0, 0, 0);

                tvTilte.setText(dzzhjBean.theme);
                tvTime.setText(DateUtil.longToString(dzzhjBean.createtime, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(dzzhjBean.villagename)) {
                    unitName = dzzhjBean.villagename;
                } else if (!TextUtils.isEmpty(dzzhjBean.vtownsname)) {
                    unitName = dzzhjBean.vtownsname;
                }

                createName = TextUtils.isEmpty(dzzhjBean.creatorName) ? "" : dzzhjBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                startTime = DateUtil.longToString(dzzhjBean.publicityTime, DateUtil.FORMAT_DATE_CHINA);

                tvContent.setText("选举时间:" + startTime);

//                endTime = DateUtil.longToString(cwgkBean.outtime, DateUtil.FORMAT_DATE_CHINA);
//                isYearEqual = startTime.startsWith(endTime.substring(0, 4));
//
//                //如果年相同
//                if (isYearEqual) {
//                    tvContent.setText("公示时间:" + startTime + "至" + endTime.substring(5));
//                } else {
//                    tvContent.setText("公示时间:" + startTime + "至" + endTime);
//                }
                break;

            //村委会换届
            case RefreshRecyclerActivity
                    .TYPE_CWHHJ:
                dzzhjBean = (DzzhjBean) item;

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_cwhhj, 0, 0, 0);

                tvTilte.setText(dzzhjBean.theme);
                tvTime.setText(DateUtil.longToString(dzzhjBean.createtime, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(dzzhjBean.villagename)) {
                    unitName = dzzhjBean.villagename;
                } else if (!TextUtils.isEmpty(dzzhjBean.vtownsname)) {
                    unitName = dzzhjBean.vtownsname;
                }

                createName = TextUtils.isEmpty(dzzhjBean.creatorName) ? "" : dzzhjBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                startTime = DateUtil.longToString(dzzhjBean.publicityTime, DateUtil.FORMAT_DATE_CHINA);

                tvContent.setText("选举时间:" + startTime);
                break;

            case RefreshRecyclerActivity
                    .TYPE_FZDYGC:
            case RefreshRecyclerActivity
                    .TYPE_ZZGXZJ:
                xwqlqdBean = ((XwqlqdBean) item);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_cwqlqd, 0, 0, 0);
                tvTilte.setText(xwqlqdBean.theme);

                tvContent.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(xwqlqdBean.villagename)) {
                    unitName = xwqlqdBean.villagename;
                } else if (!TextUtils.isEmpty(xwqlqdBean.vtownsname)) {
                    unitName = xwqlqdBean.vtownsname;
                }

                createName = TextUtils.isEmpty(xwqlqdBean.creatorName) ? "" : xwqlqdBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                tvTime.setText(DateUtil.longToString(xwqlqdBean.createtime, DateUtil.FORMAT_DATE));
                break;

            //小微权力清单
            case RefreshRecyclerActivity
                    .TYPE_XWQLQD:
                xwqlqdBean = ((XwqlqdBean) item);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_cwqlqd, 0, 0, 0);
                tvTilte.setText(xwqlqdBean.theme);

                tvContent.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(xwqlqdBean.villagename)) {
                    unitName = xwqlqdBean.villagename;
                } else if (!TextUtils.isEmpty(xwqlqdBean.vtownsname)) {
                    unitName = xwqlqdBean.vtownsname;
                }

                createName = TextUtils.isEmpty(xwqlqdBean.creatorName) ? "" : xwqlqdBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                tvTime.setText(DateUtil.longToString(xwqlqdBean.smalltime, DateUtil.FORMAT_DATE));
                break;

            //村民事务代办
            case RefreshRecyclerActivity
                    .TYPE_CMSWDB:
                CmswdbBean cmswdbBean = ((CmswdbBean) item);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_cmswdb, 0, 0, 0);
                tvTilte.setText("申请人:" + cmswdbBean.applicant);

                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(cmswdbBean.content);

                if (!TextUtils.isEmpty(cmswdbBean.village)) {
                    unitName = cmswdbBean.village;
                }
//                else if (!TextUtils.isEmpty(cmswdbBean.vtownsname)) {
//                    unitName = cmswdbBean.vtownsname;
//                }

//                createName = TextUtils.isEmpty(cmswdbBean.creatorName) ? "" : cmswdbBean.creatorName;

                tvWebsite.setText(unitName);

                tvTime.setText(DateUtil.longToString(cmswdbBean.createTime, DateUtil.FORMAT_DATE));
                break;

            //党员联系户
            case RefreshRecyclerActivity
                    .TYPE_DYLXH:
                SgdzBean dylxhBean = ((SgdzBean) item);
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_dylxh, 0, 0, 0);
                tvTilte.setText(dylxhBean.name);
                tvWebsite.setText("联系户名单:" + dylxhBean.cname);

                tvContent.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                break;

            //微心愿
            case RefreshRecyclerActivity
                    .TYPE_WXY:
                tvClaimStatus = helper.getView(R.id.tv_claim_status);

                WxyBean wxyBean = ((WxyBean) item);
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_wxy, 0, 0, 0);
                tvTilte.setText(wxyBean.content);

                tvClaimStatus.setVisibility(View.VISIBLE);
                // 0待审核，1审核通过，2驳回
                if ("0".equals(wxyBean.reviewState)) {
                    tvClaimStatus.setText("未审核");
                    tvClaimStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_999));
                    tvClaimStatus.setBackgroundResource(R.drawable.shape_claim_examine_status);
                } else if ("2".equals(wxyBean.reviewState)) {
                    tvClaimStatus.setText("驳回");
                    tvClaimStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_999));
                    tvClaimStatus.setBackgroundResource(R.drawable.shape_claim_examine_status);
                } else {
                    tvClaimStatus.setText(1 == wxyBean.claim ? "已认领" : "未认领");
                    tvClaimStatus.setTextColor(ContextCompat.getColor(mContext, 1 == wxyBean.claim ? R.color.color_11cf8f : R.color.color_e85541));
                    tvClaimStatus.setBackgroundResource(1 == wxyBean.claim ? R.drawable.shape_claim_status_true : R.drawable.shape_claim_status_false);
                }

                helper.setTextColor(R.id.tv_tilte, "2".equals(wxyBean.reviewState) ? ContextCompat.getColor(mContext, R.color.color_999) : ContextCompat.getColor(mContext, R.color.color_333));

                tvContent.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(wxyBean.villagename)) {
                    unitName = wxyBean.villagename;
                } else if (!TextUtils.isEmpty(wxyBean.vtownsname)) {
                    unitName = wxyBean.vtownsname;
                }

                createName = TextUtils.isEmpty(wxyBean.creatorName) ? "" : wxyBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

//                tvTime.setText(DateUtil.longToString(wxyBean.createtime, DateUtil.FORMAT_DATE));
                tvTime.setText(wxyBean.createtime.substring(0, 10));
                break;

            //帮扶责任人管理
            case RefreshRecyclerActivity
                    .TYPE_BFZRRGL:
                BfzrrglBean bfzrrglBean = ((BfzrrglBean) item);
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_bfzrrgl, 0, 0, 0);
                tvTilte.setText(bfzrrglBean.dutyPerson);

                tvContent.setVisibility(View.GONE);

                tvWebsite.setText("派驻乡村:" + bfzrrglBean.boroughName + bfzrrglBean.villageName);
                tvTime.setText("帮扶对象:" + bfzrrglBean.name);
                break;

            //帮扶活动展示
            case RefreshRecyclerActivity
                    .TYPE_ZYFW:
                BfhdzsBean bfhdzsBean = ((BfhdzsBean) item);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_bfhdzs, 0, 0, 0);
                tvTilte.setText(bfhdzsBean.title);

                tvContent.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(bfhdzsBean.villageName)) {
                    unitName = bfhdzsBean.villageName;
                } else if (!TextUtils.isEmpty(bfhdzsBean.townName)) {
                    unitName = bfhdzsBean.townName;
                }

                createName = TextUtils.isEmpty(bfhdzsBean.creatorName) ? "" : bfhdzsBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                tvTime.setText(DateUtil.longToString(bfhdzsBean.activityDate, DateUtil.FORMAT_DATE));
                break;

            //线上评比
            case RefreshRecyclerActivity
                    .TYPE_XSPB:
                XspbBean xspbBean = ((XspbBean) item);

                tvContent.setVisibility(View.GONE);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_xspb, 0, 0, 0);
                tvTilte.setText(xspbBean.title);

                //未投票
                if (0 == xspbBean.voteState) {
                    tvWebsite.setTextColor(ContextCompat.getColor(mContext, R.color.color_e85541));
                } else {
                    tvWebsite.setTextColor(ContextCompat.getColor(mContext, R.color.color_999));
                }

                //线上评比状态
                tvWebsite.setText(xspbBean.voteStateValue);

                //状态：0进行中 1已过期
                if (0 == xspbBean.state) {
                    tvTime.setText(DateUtil.longToString(xspbBean.deadTime, DateUtil.FORMAT_DATE_TIME) + "截止");
                } else {
                    tvTime.setText("已过期");
                }
                break;

            //他山之石
            case RefreshRecyclerFragment
                    .TYPE_TSZS:
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_xxjy, 0, 0, 0);

                ZtjyBean xxjyBean = ((ZtjyBean) item);
                tvTilte.setText(xxjyBean.theme);

                tvContent.setVisibility(View.GONE);

                tvTime.setText(DateUtil.longToString(xxjyBean.eduDate, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(xxjyBean.villagename)) {
                    unitName = xxjyBean.villagename;
                } else if (!TextUtils.isEmpty(xxjyBean.townName)) {
                    unitName = xxjyBean.townName;
                }

                createName = TextUtils.isEmpty(xxjyBean.creatorName) ? "" : xxjyBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);

                break;

            //乡村动态
            case RefreshRecyclerFragment
                    .TYPE_XCDT:
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_dcyj, 0, 0, 0);
                ZtjyBean dcyjBean = ((ZtjyBean) item);
                tvTilte.setText(dcyjBean.theme);

                tvContent.setVisibility(View.GONE);

                tvTime.setText(DateUtil.longToString(dcyjBean.eduDate, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(dcyjBean.villagename)) {
                    unitName = dcyjBean.villagename;
                } else if (!TextUtils.isEmpty(dcyjBean.townName)) {
                    unitName = dcyjBean.townName;
                }

                createName = TextUtils.isEmpty(dcyjBean.creatorName) ? "" : dcyjBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);
                break;

            //先进典型
            case RefreshRecyclerFragment
                    .TYPE_DXJY:
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_jswt, 0, 0, 0);

                ZtjyBean jswtBean = ((ZtjyBean) item);
                tvTilte.setText(jswtBean.theme);

                tvContent.setVisibility(View.GONE);

                tvTime.setText(DateUtil.longToString(jswtBean.eduDate, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(jswtBean.villagename)) {
                    unitName = jswtBean.villagename;
                } else if (!TextUtils.isEmpty(jswtBean.townName)) {
                    unitName = jswtBean.townName;
                }

                createName = TextUtils.isEmpty(jswtBean.creatorName) ? "" : jswtBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);
                break;

            //党建资讯
            case RefreshRecyclerFragment
                    .TYPE_DJZX:
                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_djzx, 0, 0, 0);

                ZtjyBean zglsBean = ((ZtjyBean) item);
                tvTilte.setText(zglsBean.theme);
                tvContent.setVisibility(View.GONE);
                tvTime.setText(DateUtil.longToString(zglsBean.eduDate, DateUtil.FORMAT_DATE));

                if (!TextUtils.isEmpty(zglsBean.villagename)) {
                    unitName = zglsBean.villagename;
                } else if (!TextUtils.isEmpty(zglsBean.townName)) {
                    unitName = zglsBean.townName;
                }

                createName = TextUtils.isEmpty(zglsBean.creatorName) ? "" : zglsBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);
                break;

            //主题党日
            case RefreshRecyclerFragment
                    .TYPE_ZTDR:
                //民主评议
            case RefreshRecyclerFragment
                    .TYPE_MZPY:
                //组织生活会
            case RefreshRecyclerFragment
                    .TYPE_ZZSHH:
                //其他
            case RefreshRecyclerFragment
                    .TYPE_MORE:
                ThemePartyBean partyBean = ((ThemePartyBean) item);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(partyBean.iconRes, 0, 0, 0);

                tvContent.setVisibility(View.GONE);

                tvTilte.setText(partyBean.title);

                if (!TextUtils.isEmpty(partyBean.villageName)) {
                    unitName = partyBean.villageName;
                } else if (!TextUtils.isEmpty(partyBean.townName)) {
                    unitName = partyBean.townName;
                }

                createName = TextUtils.isEmpty(partyBean.creatorName) ? "" : partyBean.creatorName;

                tvWebsite.setText(unitName + " " + createName);
                tvTime.setText(DateUtil.longToString(partyBean.activityDate, DateUtil.FORMAT_DATE));
                break;

            //设岗定责
            case RefreshRecyclerActivity
                    .TYPE_SGDZ:
                SgdzBean sgdzBean = ((SgdzBean) item);

                tvTilte.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_item_sgdz, 0, 0, 0);
                tvTilte.setText(sgdzBean.name);

                tvWebsite.setText("优势特长:" + sgdzBean.specialty);
                tvContent.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);

                tvClaimStatus = helper.getView(R.id.tv_claim_status);
                tvClaimStatus.setVisibility(View.VISIBLE);
                tvClaimStatus.setBackgroundResource(R.drawable.shape_bg_f5f5f5_25);
                tvClaimStatus.setText(sgdzBean.jobVal);
                tvClaimStatus.setTextColor(ContextCompat.getColor(mContext, R.color.color_666));
                break;

            default:
                break;
        }
    }
}
