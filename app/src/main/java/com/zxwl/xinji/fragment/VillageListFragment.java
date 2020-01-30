package com.zxwl.xinji.fragment;


import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.zxwl.commonlibrary.BaseLazyFragment;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.CurrencyBean;
import com.zxwl.network.bean.response.DzzcyBean;
import com.zxwl.network.bean.response.OrgInfoCountBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.activity.VillageListActivity;
import com.zxwl.xinji.adapter.VillageOrganAdapter;
import com.zxwl.xinji.adapter.VillagePeopleAdapter;
import com.zxwl.xinji.adapter.item.StudyListItem;
import com.zxwl.xinji.bean.StudyHeadBean;
import com.zxwl.xinji.bean.VillageOrgan;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func4;

/**
 * 村级组织的列表
 */
public class VillageListFragment extends BaseLazyFragment {
    private RecyclerView rvList;

    public static final String TYPE_CJZZ = "村级组织";
    public static final String TYPE_DZZCY = "村党组织成员";
    public static final String TYPE_CWHCY = "村委会成员";
    public static final String TYPE_CJHCY = "村监会成员";
    public static final String TYPE_HZZZCY = "合作组织成员";

    public static final String UNIT_ID = "UNIT_ID";
    public static final String IS_AUTHORITY = "IS_AUTHORITY";
    public static final String LEVEL = "LEVEL";

    //村级组织适配器
    private VillageOrganAdapter villageOrganAdapter;

    //村级人员适配器
    private VillagePeopleAdapter villagePeopleAdapter;

    //请求的url
    private int requestUnitId;

    //查看的等级
    private int level;

    private View emptyView;
    private View errorView;

    //是否有权限查看
    private boolean isAuthority = false;

    //只有管理员可以查看，管理员可以看本级，并且可以查看下级的数据

    public VillageListFragment() {
    }

    public static VillageListFragment newInstance(int requestUnitId, boolean isAuthority, int level) {
        VillageListFragment fragment = new VillageListFragment();
        Bundle args = new Bundle();
        args.putInt(UNIT_ID, requestUnitId);
        args.putBoolean(IS_AUTHORITY, isAuthority);
        args.putInt(LEVEL, level);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View inflateContentView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_village_list, container, false);
    }

    @Override
    protected void findViews(View view) {
        rvList = (RecyclerView) view.findViewById(R.id.rv_list);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAuthority) {
                    getListData();
                }
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAuthority) {
                    getListData();
                }
            }
        });
    }

    @Override
    protected void addListeners() {

    }

    @Override
    protected void initData() {
        requestUnitId = getArguments().getInt(UNIT_ID);
        level = getArguments().getInt(LEVEL);
        isAuthority = getArguments().getBoolean(IS_AUTHORITY);

        if (isVillageLeavel()) {
            initVillagePeopleAdapter();
        } else {
            initVillageOrganAdapter();
        }

        //是否有权限查看
        if (isAuthority) {
            getListData();
        } else {
            ImageView ivEmpty = emptyView.findViewById(R.id.iv_empty_img);
            ivEmpty.setImageResource(R.mipmap.ic_no_authority);
            ((TextView) emptyView.findViewById(R.id.tv_empty_content)).setText("");
            emptyView.findViewById(R.id.tv_retry).setVisibility(View.GONE);

            hideSkeletonScreen();

            //级别3为村镇
            if (isVillageLeavel()) {
                villagePeopleAdapter.setEmptyView(emptyView);
            } else {
                villageOrganAdapter.setEmptyView(emptyView);
            }
        }
    }

    /**
     * 初始化村级组织适配器
     */
    public void initVillageOrganAdapter() {
        villageOrganAdapter = new VillageOrganAdapter(R.layout.item_village_organ, new ArrayList<>());
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(villageOrganAdapter);
    }

    /**
     * 初始化村级人员适配器
     */
    public void initVillagePeopleAdapter() {
        rvList.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_f8f8f8));
        villagePeopleAdapter = new VillagePeopleAdapter(R.layout.item_village_people_head, new ArrayList<>());
        villagePeopleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CurrencyBean currencyBean = villagePeopleAdapter.getItem(position).newsBean;
                if (currencyBean instanceof StudyHeadBean) {
                    StudyHeadBean studyHeadBean = (StudyHeadBean) currencyBean;
                    switch (studyHeadBean.title) {
                        case TYPE_DZZCY:
                            VillageListActivity.startActivity(getActivity(), TYPE_DZZCY, requestUnitId);
                            break;

                        case TYPE_CWHCY:
                            VillageListActivity.startActivity(getActivity(), TYPE_CWHCY, requestUnitId);
                            break;

                        case TYPE_CJHCY:
                            VillageListActivity.startActivity(getActivity(), TYPE_CJHCY, requestUnitId);
                            break;

                        case TYPE_HZZZCY:
                            VillageListActivity.startActivity(getActivity(), TYPE_HZZZCY, requestUnitId);
                            break;
                    }
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvList.setAdapter(villagePeopleAdapter);
    }

    /**
     * 获取列表数据
     */
    private void getListData() {
        //村级组织
        if (isVillageLeavel()) {
            getHotDataRequest();
        } else {
            queryPartyCount();
        }
    }

    /**
     * 是否是村级别
     *
     * @return
     */
    private boolean isVillageLeavel() {
        return level == 3;
    }

    /**
     * 获取村级组织人员
     */
    private void getHotDataRequest() {
        Observable.zip(
                queryDzzcyList(1, 1, 3),
                queryDzzcyList(2, 1, 3),
                queryCjhcyList(1, 3),
                queryHzzzcyList(1, 3),
                new Func4<BaseData<DzzcyBean>,
                        BaseData<DzzcyBean>,
                        BaseData<DzzcyBean>,
                        BaseData<DzzcyBean>,
                        List<StudyListItem>>() {
                    @Override
                    public List<StudyListItem> call(
                            BaseData<DzzcyBean> dzzcyList,
                            BaseData<DzzcyBean> cwhcyList,
                            BaseData<DzzcyBean> cjhList,
                            BaseData<DzzcyBean> hzhbList) {
                        List<StudyListItem> newList = new ArrayList<>();
                        StudyListItem studyListItem = null;

                        getStudyItemList(dzzcyList, newList, TYPE_DZZCY);
                        getStudyItemList(cwhcyList, newList, TYPE_CWHCY);
                        getStudyItemList(cjhList, newList, TYPE_CJHCY);
                        getStudyItemList(hzhbList, newList, TYPE_HZZZCY);
                        return newList;
                    }
                })
                .subscribe(new RxSubscriber<List<StudyListItem>>() {
                    @Override
                    public void onSuccess(List<StudyListItem> newsBeans) {
                        hideSkeletonScreen();
                        villagePeopleAdapter.replaceData(newsBeans);
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        hideSkeletonScreen();
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    public void getStudyItemList(BaseData<DzzcyBean> ztdtList, List<StudyListItem> newList, String title) {
        StudyListItem studyListItem = null;
        if (BaseData.SUCCESS.equals(ztdtList.result) && ztdtList.dataList.size() > 0) {
            studyListItem = new StudyListItem(new StudyHeadBean(title, getHeadRes(title)));
            newList.add(studyListItem);
            for (int i = 0, len = ztdtList.dataList.size(); i < len; i++) {
                DzzcyBean themePartyBean = ztdtList.dataList.get(i);
                themePartyBean.itemTyep = title;
                if (i == len - 1) {
                    themePartyBean.isLast = true;
                }
                newList.add(new StudyListItem(themePartyBean));
            }
        }
    }

    private int getHeadRes(String title) {
        switch (title) {
            //村党组织成员
            case VillageListActivity.TYPE_DZZCY:
                return R.mipmap.ic_people_cdzzcy;

            //村委会成员
            case VillageListActivity.TYPE_CWHCY:
                return R.mipmap.ic_people_cwhcy;

            //村监会成员
            case VillageListActivity.TYPE_CJHCY:
                return R.mipmap.ic_people_cjhcy;

            //合作组织成员
            case VillageListActivity.TYPE_HZZZCY:
                return R.mipmap.ic_people_hzzzcy;

            default:
                return R.mipmap.ic_people_cdzzcy;
        }
    }

    /**
     * 获取党组织成员
     *
     * @param type type为1：获取党组织成员 type为2：获取村委会成员
     */
    private Observable<BaseData<DzzcyBean>> queryDzzcyList(int type, int pageNum, int pageSize) {
        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryOrgPeopleList(pageNum, pageSize, requestUnitId, type)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }

    /**
     * 获取村监会成员
     */
    private Observable<BaseData<DzzcyBean>> queryCjhcyList(int pageNum, int pageSize) {
        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .querySupervisionList(pageNum, pageSize, requestUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }

    /**
     * 获取合作组织成员
     */
    private Observable<BaseData<DzzcyBean>> queryHzzzcyList(int pageNum, int pageSize) {
        return HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryCooperationList(pageNum, pageSize, requestUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose());
    }


    /**
     * 查询村级组织数量
     */
    private void queryPartyCount() {
        HttpUtils.getInstance(getActivity())
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryPartyCount(requestUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<OrgInfoCountBean>() {
                    @Override
                    public void onSuccess(OrgInfoCountBean baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<VillageOrgan> list = new ArrayList<>();
                            VillageOrgan villageOrgan = null;

                            villageOrgan = new VillageOrgan("村党组织成员", baseData.sum1);
                            list.add(villageOrgan);

                            villageOrgan = new VillageOrgan("村委会成员", baseData.sum2);
                            list.add(villageOrgan);

                            villageOrgan = new VillageOrgan("村监会成员", baseData.sum3);
                            list.add(villageOrgan);

                            villageOrgan = new VillageOrgan("合作组织成员", baseData.sum4);
                            list.add(villageOrgan);

                            villageOrganAdapter.replaceData(list);

                            //隐藏骨架图
                            hideSkeletonScreen();
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        villageOrganAdapter.setEmptyView(errorView);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private SkeletonScreen skeletonScreen;

    /**
     * 显示骨架图
     */
    private void showSkeletonSceen(RecyclerView.Adapter adapter) {
        skeletonScreen = Skeleton.bind(rvList)
                .adapter(adapter)
                .shimmer(true)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(10)
                .load(R.layout.item_skeleton_news)
                .show(); //default count is 10
    }

    /**
     * 隐藏骨架图
     */
    private void hideSkeletonScreen() {
        if (null != skeletonScreen) {
            skeletonScreen.hide();
            skeletonScreen = null;
        }
    }
}
