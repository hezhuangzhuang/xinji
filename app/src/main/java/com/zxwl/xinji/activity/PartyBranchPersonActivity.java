package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.jaeger.library.StatusBarUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.PartyBranchPersonBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.PartyBranchPersonAdapter;
import com.zxwl.xinji.base.BaseActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import qdx.stickyheaderdecoration.NormalDecoration;

/**
 * 党支部人员
 */
public class PartyBranchPersonActivity extends BaseActivity {
    public static final String TYPE_ZBSJ = "支部书记";
    public static final String TYPE_ZBWY = "支部委员";
    public static final String TYPE_ZBDY = "支部党员";

    public static final String TITLE = "TITLE";
    public static final String UNIT_ID = "TOWNSHIP_ID";
    public static final String VILLAGE_ID = "VILLAGE_ID";
    private String title;

    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private EditText etContent;
    private RecyclerView rvList;

    private NormalDecoration decoration;
    private PartyBranchPersonAdapter adapter;

    private int unitId;

    private String requestUrl;

    public static void startActivity(Context context,
                                     String title,
                                     int unitId) {
        Intent intent = new Intent(context, PartyBranchPersonActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(UNIT_ID, unitId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        etContent = (EditText) findViewById(R.id.et_content);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);
        tvTopTitle.setText(title);

        unitId = getIntent().getIntExtra(UNIT_ID, 0);

        initRequestUrl();

        initAdapter();

        getPersonData();
    }

    private void initRequestUrl() {
        switch (title) {
            case TYPE_ZBSJ:
                requestUrl = Urls.QUERY_ZBSJ_LIST;
                break;

            case TYPE_ZBWY:
                requestUrl = Urls.QUERY_ZBWY_LIST;
                break;

            case TYPE_ZBDY:
                requestUrl = Urls.QUERY_ZBDY_LIST;
                break;
        }
    }

    private void getPersonData() {
        Map<String, String> hashMap = new HashMap<>();
        if (0 != unitId) {
            hashMap.put("unitId", String.valueOf(unitId));
        }
        if (!TextUtils.isEmpty(searchContent)) {
            hashMap.put("name", searchContent);
        }

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryPartyPerson(requestUrl, hashMap)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<PartyBranchPersonBean>>() {
                    @Override
                    public void onSuccess(BaseData<PartyBranchPersonBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<PartyBranchPersonBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (null != decoration) {
                                    //移除之前的decoration
                                    decoration.onDestory();
                                    rvList.removeItemDecorationAt(0);
                                }
                                adapter.replaceData(dataList);
                                decoration = new NormalDecoration() {
                                    @Override
                                    public String getHeaderName(int pos) {
                                        return adapter.getItem(pos).firstChar;
                                    }
                                };
                                rvList.addItemDecoration(decoration);
                            }else {
                                adapter.replaceData(new ArrayList<>());
                            }
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
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private void initAdapter() {
        adapter = new PartyBranchPersonAdapter(R.layout.item_party_organization_person, new ArrayList<>());
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PersonnelDetailsActivity.startActivity(PartyBranchPersonActivity.this, (PartyBranchPersonBean) adapter.getItem(position), TYPE_ZBDY.equals(title));
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(adapter);

        showSkeletonSceen(adapter);
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

    //搜索
    private String searchContent;

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchContent = s.toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getPersonData();
                        }
                    }, 500);
                } else {
                    searchContent = "";
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getPersonData();
                        }
                    }, 500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_party_branch_person;
    }

}
