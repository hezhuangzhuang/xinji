package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.PersonnelBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.SelectPersonnelAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.utils.PinyinUtils;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.indexbar.IndexBar;
import com.zxwl.xinji.widget.indexbar.IndexLayout;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import qdx.stickyheaderdecoration.NormalDecoration;

/**
 * 选择列席人员
 */
public class SelectPersonnelActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private TextView tvHintInfo;
    private IndexLayout indexLayout;

    private RecyclerView rvList;
    private LinearLayoutManager layoutManager;
    private SelectPersonnelAdapter selectPersonnelAdapter;

    private TextView tvAllSelect;

    public static final String TYPE = "TYPE";
    public static final String LIST = "LIST";

    public static final String SELECT_ATTEND = "选择列席人员";
    public static final String SELECT_PARTICIPANTS = "选择参会人员";

    public static final String SELECT_RATING = "选择候选人员";

    private String title;

    //选择列席人员
    private boolean isSelectAttend = true;

    /**
     * 已选的会场
     */
    private List<PersonnelBean> selectData;

    private LoginBean.AccountBean accountBean;

    private boolean isAllSelect = false;//是否全选

    /**
     * selectAttend和selectParticipants同时存在或同时不存在，从发布页跳转到选择页面
     * 只有selectAttend存在，从选择列席人员跳转到选择参会人员
     *
     * @param context
     * @param title
     * @param selectList 已选的列席人员
     */
    public static void startActivity(Context context, String title, List<PersonnelBean> selectList) {
        Intent intent = new Intent(context, SelectPersonnelActivity.class);
        intent.putExtra(TYPE, title);
        intent.putExtra(LIST, (Serializable) selectList);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvHintInfo = (TextView) findViewById(R.id.tv_hint_info);
        tvAllSelect = (TextView) findViewById(R.id.tv_all_select);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        indexLayout = (IndexLayout) findViewById(R.id.index_layout);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        accountBean = PreferenceUtil.getUserInfo(this);

        title = getIntent().getStringExtra(TYPE);

        selectData = (List<PersonnelBean>) getIntent().getSerializableExtra(LIST);

        if (null == selectData) {
            selectData = new ArrayList<>();
        }

        tvTopTitle.setText(title);

        initView();

        //是否选择列席人员
        isSelectAttend = SELECT_ATTEND.equals(title) || SELECT_RATING.equals(title);

        initRecycler(new ArrayList<>());

        getPersonnelsRequest();
    }
    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(this);
        tvRightOperate.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_personnel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //返回
            case R.id.iv_back_operate:
                finish();
                break;

            //返回
            case R.id.tv_all_select:
                isAllSelect();

                setAllSelectStatus(!isAllSelect);

                setAllTextStatus(!isAllSelect);
                break;

            case R.id.tv_right_operate:
                //选择列席人员
                if (SELECT_ATTEND.equals(title)) {
                    //判断是否有选列席人员
                    List<PersonnelBean> attends = new ArrayList<>();

                    for (int i = 0; i < selectPersonnelAdapter.getItemCount(); i++) {
                        if (selectPersonnelAdapter.getItem(i).isAttend) {
                            attends.add(selectPersonnelAdapter.getItem(i));
                        }
                    }

                    if (attends.size() <= 0) {
                        ToastHelper.showShort("请选择列席人员");
                        return;
                    }

                    //初始化选择状态
                    for (int i = 0; i < attends.size(); i++) {
                        PersonnelBean personnelBean = attends.get(i);
                        personnelBean.isCheck = personnelBean.isParticipants;
                    }

                    tvTopTitle.setText("选择参会人员");
                    tvRightOperate.setText("确定");
                    tvHintInfo.setText("以下均为已选的列席人员，勾选标记为参会人员，点击确定保存");
                    isSelectAttend = false;

                    //选择参会人员
                    title = SELECT_PARTICIPANTS;

                    Collections.sort(attends);

                    //更新recycler数据
                    updateRecyclerDate(attends);

                    initIndexBar(attends);

                    //是否全选
                    isAllSelect();

                    //设置全选样式
                    setAllTextStatus(isAllSelect);
                }
                //选择人员完毕
                else if (SELECT_PARTICIPANTS.equals(title)) {
                    List<PersonnelBean> selects = new ArrayList<>();

                    //列席人员数量
                    int selectAdd = 0;
                    //参会人员数量
                    int selectConf = 0;

                    //选出
                    for (int i = 0; i < selectPersonnelAdapter.getItemCount(); i++) {
                        PersonnelBean personnelBean = selectPersonnelAdapter.getItem(i);
                        if (personnelBean.isAttend) {
                            selects.add(personnelBean);
                            selectAdd++;
                        }

                        if (personnelBean.isParticipants) {
                            selectConf++;
                        }
                        personnelBean.isCheck = false;
                    }
                    //判断参会人员是否为空
                    if (selectConf <= 0) {
                        ToastHelper.showShort("参会人员不能为空");
                        return;
                    }

                    EventBus.getDefault().post(new EventMessage(Messages.SELECT_PERSONNEL, selects));
                    finish();
                }
                //选择评比人员
                else {
                    List<PersonnelBean> selects = new ArrayList<>();

                    int selectRating = 0;
                    //选出
                    for (int i = 0; i < selectPersonnelAdapter.getItemCount(); i++) {
                        PersonnelBean personnelBean = selectPersonnelAdapter.getItem(i);
                        if (personnelBean.isAttend) {
                            selects.add(personnelBean);
                            selectRating++;
                        }
                    }

                    //判断参会人员是否为空
                    if (selectRating <= 0) {
                        ToastHelper.showShort("候选人员不能为空");
                        return;
                    }

                    EventBus.getDefault().post(new EventMessage(Messages.SELECT_RATING, selects));
                    finish();
                }
                break;

            default:
                break;
        }
    }

    private void initView() {
        switch (title) {
            // 选择列席人员
            case SELECT_ATTEND:
                tvRightOperate.setText("下一步");
                tvRightOperate.setVisibility(View.VISIBLE);

                tvHintInfo.setText("请勾选列席人员，点击下一步可在已勾选的列席人员中选择参会人员");
                break;

            //  选择参会人员
            case SELECT_PARTICIPANTS:
                tvRightOperate.setText("确定");
                tvRightOperate.setVisibility(View.VISIBLE);

                tvHintInfo.setText("以下均为已选的列席人员，勾选标记为参会人员，点击确定保存");

                break;

//            选择评比人员
            case SELECT_RATING:
                tvRightOperate.setText("确定");
                tvRightOperate.setVisibility(View.VISIBLE);

                tvHintInfo.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 获取党员
     */
    private void getPersonnelsRequest() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryAllOrg(accountBean.partyBranchId, accountBean.partyBranchId)
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<PersonnelBean>>() {
                    @Override
                    public void onSuccess(BaseData<PersonnelBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            Collections.sort(baseData.dataList);

                            setSelectStyle(baseData.dataList);

                            //更新recycler数据
                            updateRecyclerDate(baseData.dataList);

                            initIndexBar(baseData.dataList);

                            //判断是否全选
                            isAllSelect();

                            //设置全选框的状态
                            setAllTextStatus(isAllSelect);

                            tvAllSelect.setOnClickListener(SelectPersonnelActivity.this);
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("onError:" + responeThrowable.getCause().getMessage());
                    }
                });
    }

    /**
     * 设置已选的会场样式
     */
    private void setSelectStyle(List<PersonnelBean> beanList) {
        for (int i = 0; i < selectData.size(); i++) {
            //获取当前的view
            PersonnelBean currentBean = selectData.get(i);
            for (int j = 0; j < beanList.size(); j++) {
                PersonnelBean personnelBean = beanList.get(j);
                if (currentBean.name.equals(personnelBean.name)) {
                    personnelBean.isAttend = currentBean.isAttend;
                    personnelBean.isParticipants = currentBean.isParticipants;
                    personnelBean.isCheck = personnelBean.isAttend;
                }
            }
        }
    }

    /**
     * 设置按钮状态
     *
     * @param allSelect
     */
    public void setAllTextStatus(boolean allSelect) {
        tvAllSelect.setCompoundDrawablesRelativeWithIntrinsicBounds(allSelect ? R.mipmap.ic_select_person_true : R.mipmap.ic_select_person_false, 0, 0, 0);
    }

    private void updateRecyclerDate(List<PersonnelBean> selectData) {
        selectPersonnelAdapter.setNewData(selectData);
        decoration.onDestory();
        rvList.removeItemDecorationAt(0);

        decoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                //获取第一个字母
                String headName = PinyinUtils.getFirstPinYinHeadChar(selectData.get(pos).name);
                return headName;
            }
        };

        rvList.addItemDecoration(decoration);
    }

    private NormalDecoration decoration;

    /**
     * 初始化recycler
     */
    private void initRecycler(List<PersonnelBean> list) {
        selectPersonnelAdapter = new SelectPersonnelAdapter(R.layout.item_select_personnel, list);

        selectPersonnelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PersonnelBean personnelBean = selectPersonnelAdapter.getItem(position);
                //选择状态取反
                personnelBean.isCheck = !personnelBean.isCheck;

                //选择列席人员
                if (isSelectAttend) {
                    //如果状态未非选中，则设置参会人员属性也为false
                    //因为如果是从选择界面跳转过来，有可能参会人员属性为true
                    if (!personnelBean.isCheck) {
                        personnelBean.isParticipants = false;
                    }

                    //设置列席人员参数
                    personnelBean.isAttend = personnelBean.isCheck;
                }//选择参会人员
                else {
                    //设置参会人员参数
                    personnelBean.isParticipants = personnelBean.isCheck;
                }

                //是否是全选
                isAllSelect();

                //设置全选按钮样式
                setAllTextStatus(isAllSelect);

                selectPersonnelAdapter.notifyDataSetChanged();
            }
        });

        decoration = new NormalDecoration() {
            @Override
            public String getHeaderName(int pos) {
                //获取第一个字母
                String headName = PinyinUtils.getFirstPinYinHeadChar(list.get(pos).name);
                return headName;
            }
        };

        layoutManager = new LinearLayoutManager(this);
        rvList.addItemDecoration(decoration, 0);
        rvList.setLayoutManager(layoutManager);
        rvList.setAdapter(selectPersonnelAdapter);
    }

    /**
     * 初始化右边bar
     */
    private void initIndexBar(List<PersonnelBean> list) {
        //侧边导航栏数据
        List<String> heads = new ArrayList<>();
        //遍历列表数据里的
        for (PersonnelBean personnelBean : list) {
            if (!heads.contains(PinyinUtils.getFirstPinYinHeadChar(personnelBean.name))) {
                heads.add(PinyinUtils.getFirstPinYinHeadChar(personnelBean.name));
            }
        }

        indexLayout.setIndexBarHeightRatio(0.9f);
        indexLayout.getIndexBar().setIndexsList(heads);
        indexLayout.setCircleTextColor(Color.WHITE);
        indexLayout.setCircleRadius(200);
        indexLayout.setCirCleTextSize(150);
        indexLayout.setCircleColor(ContextCompat.getColor(this, R.color.color_999));
        indexLayout.getIndexBar().setIndexChangeListener(new IndexBar.IndexChangeListener() {
            @Override
            public void indexChanged(String indexName) {
                for (int i = 0; i < list.size(); i++) {
                    if (indexName.equals(PinyinUtils.getFirstPinYinHeadChar(list.get(i).name))) {
                        layoutManager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
    }


    /**
     * 获取全选的列表
     */
    private List<Integer> isAllSelect() {
        //选择的数量
        List<Integer> selectIndexs = new ArrayList<>();

        for (int i = 0; i < selectPersonnelAdapter.getItemCount(); i++) {
            switch (title) {
                //选择列席人员
                case SELECT_ATTEND:
                    if (selectPersonnelAdapter.getItem(i).isAttend) {
                        selectIndexs.add(i);
                    }
                    break;

                //选择参会人员
                case SELECT_PARTICIPANTS:
                    if (selectPersonnelAdapter.getItem(i).isParticipants) {
                        selectIndexs.add(i);
                    }
                    break;

                //选择候选人员
                case SELECT_RATING:
                    if (selectPersonnelAdapter.getItem(i).isCheck) {
                        selectIndexs.add(i);
                    }
                    break;
            }
        }

        isAllSelect = (selectIndexs.size() == selectPersonnelAdapter.getItemCount());
        return selectIndexs;
    }

    /**
     * 设置全部的状态
     */
    private void setAllSelectStatus(boolean select) {
        for (int i = 0; i < selectPersonnelAdapter.getItemCount(); i++) {
            setSingleStatus(selectPersonnelAdapter.getItem(i), select);
        }

        selectPersonnelAdapter.notifyDataSetChanged();
    }

    /**
     * 设置单个的选中状态
     *
     * @param personnelBean
     * @param select
     */
    private void setSingleStatus(PersonnelBean personnelBean, boolean select) {
        personnelBean.isCheck = select;

        switch (title) {
            //选择列席人员
            case SELECT_ATTEND:
                personnelBean.isAttend = select;

                if (!select) {
                    personnelBean.isParticipants = select;
                }
                break;

            //选择参会人员
            case SELECT_PARTICIPANTS:
                personnelBean.isParticipants = select;
                break;

            //选择候选人员
            case SELECT_RATING:
                personnelBean.isAttend = select;
                break;

        }
    }
}
