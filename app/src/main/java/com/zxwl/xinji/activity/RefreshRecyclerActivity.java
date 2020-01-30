package com.zxwl.xinji.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.DisplayUtil;
import com.zxwl.commonlibrary.utils.KeyBoardUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.AdvisoryBean;
import com.zxwl.network.bean.response.BfhdzsBean;
import com.zxwl.network.bean.response.BfzrrglBean;
import com.zxwl.network.bean.response.CmswdbBean;
import com.zxwl.network.bean.response.CwgkBean;
import com.zxwl.network.bean.response.DepartmentBean;
import com.zxwl.network.bean.response.DzzhjBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.NoteBean;
import com.zxwl.network.bean.response.ProblemBean;
import com.zxwl.network.bean.response.ServicePhoneBean;
import com.zxwl.network.bean.response.SgdzBean;
import com.zxwl.network.bean.response.SjdbBean;
import com.zxwl.network.bean.response.TsbcBean;
import com.zxwl.network.bean.response.WxyBean;
import com.zxwl.network.bean.response.XspbBean;
import com.zxwl.network.bean.response.XwqlqdBean;
import com.zxwl.network.bean.response.ZtjyBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.EventSupervisorAdapter;
import com.zxwl.xinji.adapter.MyConsultationAdapter;
import com.zxwl.xinji.adapter.PartyBranchAdapter;
import com.zxwl.xinji.adapter.ProblemAdapter;
import com.zxwl.xinji.adapter.ServicePhoneAdapter;
import com.zxwl.xinji.adapter.ThemePatryCurrencyAdapter;
import com.zxwl.xinji.adapter.TsbcAdapter;
import com.zxwl.xinji.adapter.item.ProblemChildItem;
import com.zxwl.xinji.adapter.item.ProblemParentItem;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.widget.ScreenCityPopupWindow;
import com.zxwl.xinji.widget.decoration.MyRecyclerViewDivider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * 村务公开
 * 党务公开,
 * 党员联系户,
 * 微心愿,
 * 政务服务电话,
 * 帮扶责任人管理,
 * 常见问题,
 * 线上评比
 * <p>
 * 列表activity
 */
public class RefreshRecyclerActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvRightOperate;
    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;
    private EditText etContent;

    private ImageView ivRightOperate;

    private RelativeLayout rlScreen;
    private TextView tvCity;
    private TextView tvScreen;

    //标题
    public static final String TITLE = "TITLE";
    //标题
    private String title;

    private int pageNum = 0;
    private int PAGE_SIZE = 10;

    private View emptyView;
    private View errorView;

    /**
     * 村务公开
     * 主题党日-->
     * 民主评议-->
     * 组织生活会-->
     * 其他-->
     * 政治理论-->
     * 党务公开-->
     * 微心愿-->
     * 线上评比-->
     * 帮扶责任人管理-->
     */
    private ThemePatryCurrencyAdapter currencyAdapter;

    public static final String TYPE_DWGK = "党务公开";

    public static final String TYPE_CWGK = "党务村务公开";

    public static final String TYPE_CMSWDB = "村民事务代办";

    public static final String TYPE_WXY = "微心愿";

    public static final String TYPE_BFZRRGL = "帮扶责任人管理";

    //TODO:之前的帮扶活动展示
    public static final String TYPE_ZYFW = "志愿服务";

    public static final String TYPE_XSPB = "在线评比";

    public static final String TYPE_XXJY = "学习教育";

    public static final String TYPE_DCYJ = "调查研究";

    public static final String TYPE_JSWT = "检视问题";

    public static final String TYPE_ZGLS = "整改落实";

    public static final String TYPE_XWQLQD = "小微权力清单";

    public static final String TYPE_DYLXH = "党员联系户";

    public static final String TYPE_DZZHJ = "党组织换届";

    public static final String TYPE_CWHHJ = "村委会换届";

    public static final String TYPE_JSB = "记事本";

    //其他样式适配器
    public static final String TYPE_ZWFWDH = "政务服务电话";

    public static final String TYPE_CJWT = "常见问题";

    public static final String TYPE_SJDB = "事件督办";

    public static final String TYPE_WYZX = "咨询列表";

    public static final String TYPE_FZDYGC = "发展党员规程";

    public static final String TYPE_ZZGXZJ = "组织关系转接";

    public static final String TYPE_SGDZ = "设岗定责";

    public static final String TYPE_DZB = "党支部";

    //图说本村
    public static final String TYPE_JTJJ = "集体经济";
    public static final String TYPE_LDGZ = "亮点工作";
    public static final String TYPE_RYBZ = "荣誉表彰";
    public static final String TYPE_TSBC = "图说本村";

    //图说本村适配器
    private TsbcAdapter tsbcAdapter;

    //事件督办适配器
    private EventSupervisorAdapter eventSupervisorAdapter;

    //服务电话适配器
    private ServicePhoneAdapter servicePhoneAdapter;

    //我要咨询适配器
    private MyConsultationAdapter myConsultationAdapter;

    //党支部的适配器
    private PartyBranchAdapter partyBranchAdapter;

    //常见问题适配器-start
    private ProblemAdapter problemAdapter;

    private LoginBean.AccountBean accountBean;

    private int lastPosition = -1;//最后一次点击的下标

    //主题教育的类型
    private int eductype = -1;

    private boolean checkAdmin = false;//是否是管理员

    //常见问题的搜索内容
    private String searchContent;

    //请求的url
    private String requestUrl;

    public static void startActivity(Context context, String title) {
        Intent intent = new Intent(context, RefreshRecyclerActivity.class);
        intent.putExtra(TITLE, title);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvRightOperate = (TextView) findViewById(R.id.tv_right_operate);

        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        rvList = (RecyclerView) findViewById(R.id.rv_list);

        etContent = (EditText) findViewById(R.id.et_content);

        rlScreen = (RelativeLayout) findViewById(R.id.rl_screen);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvScreen = (TextView) findViewById(R.id.tv_screen);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });

        errorView = getLayoutInflater().inflate(R.layout.include_error, (ViewGroup) rvList.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshLayout.autoRefresh();
            }
        });
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);

        tvTopTitle.setText(title);

        boolean isCanNull =
                title.equals(TYPE_XWQLQD) ||
                        title.equals(TYPE_ZWFWDH) ||
                        title.equals(TYPE_FZDYGC) ||
                        title.equals(TYPE_ZZGXZJ) ||
                        title.equals(TYPE_CJWT);
        //是否登录了
        if (isLogin()) {
            initAccountBean();
        }

        //不可以为空
        if (!isCanNull) {
            initAccountBean();
        }

        if (null == accountBean && !isCanNull) {
            return;
        }

        //初始化适配器
        initAdapter();

        //获取数据
        getData(1);
    }

    private void initAccountBean() {
        accountBean = PreferenceUtil.getUserInfo(this);
        if (null != accountBean) {
            checkAdmin = (1 == accountBean.checkAdmin);

            requestUnitId = accountBean.unitId;

            tvCity.setText(accountBean.flag);

            //不等于微心愿才这样取值
            if (!TYPE_WXY.equals(title)) {
                //行政级别,1：市级,2：乡镇,3：村级
                if (2 == accountBean.level) {
                    townshipId = Integer.valueOf(accountBean.townId);
                } else if (3 == accountBean.level) {
                    townshipId = Integer.valueOf(accountBean.townId);
                    villageId = Integer.valueOf(accountBean.villageId);
                }
            }
        }
    }

    /**
     * 是否有登录
     *
     * @return true：登录，false：没登录
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是村镇
                if (null != accountBean && 2 == accountBean.level) {
                    if (null == screenCityDialog) {
                        //如果左边的列表等于null，代表第一次请求
                        leftDepartments = new ArrayList<>();
                        DepartmentBean departmentBean = null;

                        departmentBean = new DepartmentBean();
                        departmentBean.departmentName = accountBean.unitName;
                        departmentBean.id = accountBean.unitId;
                        leftDepartments.add(departmentBean);

                        showScreenDialog(leftDepartments, new ArrayList<>());
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                } //如果是市级账号
                else if (null != accountBean && 1 == accountBean.level) {
                    if (null == screenCityDialog) {
                        getDepartmentList(1);
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                }//没有登录
                else {
                    if (null == screenCityDialog) {
                        getDepartmentList(1);
                    } else {
                        screenCityDialog.showPopupWindow(rlScreen);
                    }
                }
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                KeyBoardUtil.closeKeybord(etContent, etContent.getContext());
                getData(pageNum + 1);
            }
        });

        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_party_contacts;
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        switch (title) {
            //小微权力清单
            case TYPE_XWQLQD:
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //记事本
            case TYPE_JSB:
                setRightOperateShow(View.VISIBLE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseNoteActivity.startActivity(RefreshRecyclerActivity.this, null);
                    }
                });

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //村务公开
            case TYPE_CWGK:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseThemePartyDayActivity.startActivity(RefreshRecyclerActivity.this, TYPE_CWGK);
                    }
                });
                //显示筛选框
                setScreenShow();

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;


            //党务公开
            case TYPE_DWGK:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseThemePartyDayActivity.startActivity(RefreshRecyclerActivity.this, TYPE_DWGK);
                    }
                });
                //显示筛选框
                setScreenShow();

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //党组织换届
            case TYPE_DZZHJ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_DZZHJ);
                    }
                });
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //村委会换届
            case TYPE_CWHHJ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_CWHHJ);
                    }
                });
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //党员联系户
            case TYPE_DYLXH:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseSgdzActivity.startActivity(RefreshRecyclerActivity.this, ReleaseSgdzActivity.TYPE_DYLXH);
                    }
                });

                setScreenShow();

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //微心愿
            case TYPE_WXY:
                setRightOperateShow(View.VISIBLE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseWxyActivity.startActivity(RefreshRecyclerActivity.this);
                    }
                });
                //显示筛选框
                rlScreen.setVisibility(View.VISIBLE);

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //帮扶活动展示
            case TYPE_ZYFW:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_ZYFW);
                    }
                });

                //显示筛选框
                setScreenShow();

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //帮扶责任人管理
            case TYPE_BFZRRGL:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddBfzrrglActivity.startActivity(RefreshRecyclerActivity.this);
                    }
                });
                //显示筛选框
                setScreenShow();

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //线上评比
            case TYPE_XSPB:
                setRightOperateShow(View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_XSPB);
                    }
                });

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //学习教育
            case TYPE_XXJY:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_XXJY);
                    }
                });

                eductype = 1;
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //调查研究
            case TYPE_DCYJ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_DCYJ);
                    }
                });

                eductype = 2;
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //检视问题
            case TYPE_JSWT:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_JSWT);
                    }
                });

                eductype = 3;
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //整改落实
            case TYPE_ZGLS:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseConfActivity.startActivity(RefreshRecyclerActivity.this, TYPE_ZGLS);
                    }
                });

                eductype = 4;
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //政务服务电话
            case TYPE_ZWFWDH:
                //没有添加按钮
                initZwfwdhAdapter();

                showSkeletonSceen(servicePhoneAdapter);
                break;

            //常见问题
            case TYPE_CJWT:
                etContent.setVisibility(View.VISIBLE);
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
                                    getCjwtData(searchContent, 1);
                                }
                            }, 500);
                        } else {
                            searchContent = "";
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    getCjwtData("", 1);
                                }
                            }, 500);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                //没有添加按钮
                initCjwtAdapter();

                showSkeletonSceen(problemAdapter);
                break;

            //事件督办
            case TYPE_SJDB:
                setRightOperateShow(View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToastHelper.showShort("事件督办");
                    }
                });

                initSjdbAdapter();

                showSkeletonSceen(eventSupervisorAdapter);

                rvList.setBackgroundColor(ContextCompat.getColor(this, R.color.color_f8f8f8));
                break;

            //村民事务代办
            case TYPE_CMSWDB:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddCmswdbActivity.startActivity(RefreshRecyclerActivity.this);
                    }
                });

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //我要咨询
            case TYPE_WYZX:
                setRightOperateShow(View.VISIBLE, R.mipmap.ic_add_consultation);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConsultationDetailsActivity.startActivity(RefreshRecyclerActivity.this, true);
                    }
                });

                initWyzxAdapter();

                showSkeletonSceen(myConsultationAdapter);
                break;

            //发展党员规矩程
            case TYPE_FZDYGC:
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //组织关系转接
            case TYPE_ZZGXZJ:
                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            //党支部
            case TYPE_DZB:
                townshipId = 1;
                villageId = 1;

                rlScreen.setVisibility(View.VISIBLE);
                initPartyAdapter();
                break;

            //设岗定责
            case TYPE_SGDZ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReleaseSgdzActivity.startActivity(RefreshRecyclerActivity.this, ReleaseSgdzActivity.TYPE_SGDZ);
                    }
                });

                setScreenShow();

                initCurrencyAdapter();

                showSkeletonSceen(currencyAdapter);
                break;

            case TYPE_JTJJ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddTsbcActivity.startActivity(RefreshRecyclerActivity.this, title);
                    }
                });

                //显示筛选框
//                setScreenShow();

                requestUrl = Urls.QUERY_JTJJ_LIST;

                initTsbcAdapter();
                showSkeletonSceen(tsbcAdapter);
                break;

            case TYPE_LDGZ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddTsbcActivity.startActivity(RefreshRecyclerActivity.this, title);
                    }
                });

                //显示筛选框
//                setScreenShow();

                requestUrl = Urls.QUERY_LDGZ_LIST;
                initTsbcAdapter();
                showSkeletonSceen(tsbcAdapter);
                break;

            case TYPE_RYBZ:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddTsbcActivity.startActivity(RefreshRecyclerActivity.this, title);
                    }
                });

                //显示筛选框
//                setScreenShow();

                requestUrl = Urls.QUERY_RYBZ_LIST;
                initTsbcAdapter();
                showSkeletonSceen(tsbcAdapter);
                break;

            case TYPE_TSBC:
                setRightOperateShow(checkAdmin ? View.VISIBLE : View.GONE, R.mipmap.ic_wxy_add);
                tvRightOperate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AddTsbcActivity.startActivity(RefreshRecyclerActivity.this, title);
                    }
                });

                //显示筛选框
//                setScreenShow();
                requestUrl = Urls.QUERY_TSBC_LIST;

                initTsbcAdapter();

                showSkeletonSceen(tsbcAdapter);
                break;
        }
    }

    private void setRightOperateShow(int show, int imageRes) {
        ivRightOperate = findViewById(R.id.iv_right_operate);
        ivRightOperate.setVisibility(View.GONE);
        ivRightOperate.setImageResource(imageRes);

        if (TYPE_WYZX.equals(title)) {
            tvRightOperate.setText("咨询");
        } else if (TYPE_JSB.equals(title)) {
            tvRightOperate.setText("新增");
        } else {
            tvRightOperate.setText("添加");
        }
        tvRightOperate.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, imageRes, 0);
        if (TYPE_CWGK.equals(title)
                || TYPE_DWGK.equals(title)
                || TYPE_BFZRRGL.equals(title)
                || TYPE_SGDZ.equals(title)
                || TYPE_DYLXH.equals(title)
        ) {
            if (3 == accountBean.level) {
                tvRightOperate.setVisibility(show);
            } else {
                tvRightOperate.setVisibility(View.GONE);
            }
        } else {
            tvRightOperate.setVisibility(show);
        }
    }

    /**
     * 党支部的适配器
     */
    private void initPartyAdapter() {
        if (!TextUtils.isEmpty(accountBean.townId)) {
            townshipId = Integer.valueOf(accountBean.townId);
        }

        if (!TextUtils.isEmpty(accountBean.villageId)) {
            villageId = Integer.valueOf(accountBean.villageId);
        }
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        List<String> partyData = new ArrayList<>();
        partyData.add(PartyBranchPersonActivity.TYPE_ZBSJ);
        partyData.add(PartyBranchPersonActivity.TYPE_ZBWY);
        partyData.add(PartyBranchPersonActivity.TYPE_ZBDY);

        partyBranchAdapter = new PartyBranchAdapter(R.layout.item_party_organization, partyData);
        partyBranchAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (0 == position) {
                    PersonnelDetailsActivity.startActivity(RefreshRecyclerActivity.this, requestUnitId, true);
                } else {
                    PartyBranchPersonActivity.startActivity(RefreshRecyclerActivity.this, partyBranchAdapter.getItem(position), requestUnitId);
                }
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(partyBranchAdapter);
    }

    private void setScreenShow() {
//        1：市级,2：乡镇,3：村级
        if (null != accountBean) {
            rlScreen.setVisibility(accountBean.level == 3 ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * 通用适配器
     */
    private void initCurrencyAdapter() {
        currencyAdapter = new ThemePatryCurrencyAdapter(R.layout.item_theme_party_day, new ArrayList<>());
        currencyAdapter.setType(title);
        currencyAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                itemClick(position);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(currencyAdapter);
    }

    /**
     * 适配器点击事件
     *
     * @param position
     */
    private void itemClick(int position) {
        List<String> urls = new ArrayList<>();
        ContentDetailsBean contentDetailsBean = null;
        StringBuffer stringBuffer = null;
        List<String> strings = null;

        String unitName = "辛集市";
        String startTime = "";
        String endTime = "";
        boolean isYearEqual = false;

        SgdzBean sgdzBean = null;
        switch (title) {
            //记事本
            case TYPE_JSB:
                NoteBean noteBean = (NoteBean) currencyAdapter.getItem(position);
                ReleaseNoteActivity.startActivity(RefreshRecyclerActivity.this, noteBean);
                break;

            //村务公开
            case TYPE_CWGK:
                CwgkBean cwgkBean = (CwgkBean) currencyAdapter.getItem(position);

                if (!TextUtils.isEmpty(cwgkBean.villagename)) {
                    unitName = cwgkBean.villagename;
                } else if (!TextUtils.isEmpty(cwgkBean.vtownsname)) {
                    unitName = cwgkBean.vtownsname;
                }


                urls.addAll(Arrays.asList(
                        cwgkBean.pic1,
                        cwgkBean.pic2,
                        cwgkBean.pic3,
                        cwgkBean.pic4,
                        cwgkBean.pic5,
                        cwgkBean.pic6,
                        cwgkBean.pic7,
                        cwgkBean.pic8,
                        cwgkBean.pic9
                ));

                contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                        cwgkBean.theme,
                        cwgkBean.context,
                        urls,
                        "",
                        "",
                        cwgkBean.createtime,
                        unitName
                );

                stringBuffer = new StringBuffer();

                strings = new ArrayList<>();

                if (!TextUtils.isEmpty(cwgkBean.remark1)) {
                    strings.add(cwgkBean.remark1);
                }

                if (!TextUtils.isEmpty(cwgkBean.remark2)) {
                    strings.add(cwgkBean.remark2);
                }

                for (int i = 0; i < strings.size(); i++) {
                    if (i == strings.size() - 1) {
                        stringBuffer.append((i + 1) + "." + strings.get(i));
                    } else {
                        stringBuffer.append((i + 1) + "." + strings.get(i) + "\n");
                    }
                }

                startTime = DateUtil.longToString(cwgkBean.publicityTime, DateUtil.FORMAT_DATE_CHINA);
                endTime = DateUtil.longToString(cwgkBean.outtime, DateUtil.FORMAT_DATE_CHINA);
                isYearEqual = startTime.startsWith(endTime.substring(0, 4));

                //如果年相同
                if (isYearEqual) {
                    contentDetailsBean.one =
//                            "公示时间:" +
                            startTime + "至" + endTime.substring(5);
                } else {
                    contentDetailsBean.one =
//                            "公示时间:" +
                            startTime + "至" + endTime;
                }

                contentDetailsBean.two = TextUtils.isEmpty(stringBuffer.toString()) ? "暂无" : stringBuffer.toString();

                contentDetailsBean.oneLable = "一、公示时间";
                contentDetailsBean.twoLable = "二、备注";

                ContentDetailsActivity.startActivity(RefreshRecyclerActivity.this, contentDetailsBean);
                break;

            //党务公开
            case TYPE_DWGK:
                CwgkBean dwgkBean = (CwgkBean) currencyAdapter.getItem(position);

                if (!TextUtils.isEmpty(dwgkBean.villagename)) {
                    unitName = dwgkBean.villagename;
                } else if (!TextUtils.isEmpty(dwgkBean.vtownsname)) {
                    unitName = dwgkBean.vtownsname;
                }

                urls.addAll(Arrays.asList(
                        dwgkBean.pic1,
                        dwgkBean.pic2,
                        dwgkBean.pic3,
                        dwgkBean.pic4,
                        dwgkBean.pic5,
                        dwgkBean.pic6,
                        dwgkBean.pic7,
                        dwgkBean.pic8,
                        dwgkBean.pic9
                ));

                contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(dwgkBean.theme,
                        dwgkBean.context,
                        urls,
                        "",
                        "",
                        dwgkBean.createtime,
                        unitName);

                stringBuffer = new StringBuffer();
                strings = new ArrayList<>();

                if (!TextUtils.isEmpty(dwgkBean.remark1)) {
                    strings.add(dwgkBean.remark1);
                }

                if (!TextUtils.isEmpty(dwgkBean.remark2)) {
                    strings.add(dwgkBean.remark2);
                }

                for (int i = 0; i < strings.size(); i++) {
                    if (i == strings.size() - 1) {
                        stringBuffer.append((i + 1) + "." + strings.get(i));
                    } else {
                        stringBuffer.append((i + 1) + "." + strings.get(i) + "\n");
                    }
                }

                startTime = DateUtil.longToString(dwgkBean.publicityTime, DateUtil.FORMAT_DATE_CHINA);
                endTime = DateUtil.longToString(dwgkBean.outtime, DateUtil.FORMAT_DATE_CHINA);
                isYearEqual = startTime.startsWith(endTime.substring(0, 4));

                //如果年相同
                if (isYearEqual) {
                    contentDetailsBean.one =
//                            "公示时间:" +
                            startTime + "至" + endTime.substring(5);
                } else {
                    contentDetailsBean.one =
//                            "公示时间:" +
                            startTime + "至" + endTime;
                }

                contentDetailsBean.two = TextUtils.isEmpty(stringBuffer.toString()) ? "暂无" : stringBuffer.toString();

                contentDetailsBean.oneLable = "一、公示时间";
                contentDetailsBean.twoLable = "二、备注";

                ContentDetailsActivity.startActivity(RefreshRecyclerActivity.this, contentDetailsBean);
                break;

            //党员联系户
            case TYPE_DYLXH:
                sgdzBean = (SgdzBean) currencyAdapter.getItem(position);

                SgdzDetailsActivity.startActivity(RefreshRecyclerActivity.this, SgdzDetailsActivity.TYPE_DYLXH, sgdzBean);
                break;

            //微心愿
            case TYPE_WXY:
                WxyBean wxyBean = (WxyBean) currencyAdapter.getItem(position);
                WxyDetailsActivity.startActivity(RefreshRecyclerActivity.this, wxyBean);
                break;

            //帮扶活动展示
            case TYPE_ZYFW:
                BfhdzsBean bfhdzsBean = (BfhdzsBean) currencyAdapter.getItem(position);

                urls.addAll(Arrays.asList(
                        bfhdzsBean.pic1,
                        bfhdzsBean.pic2,
                        bfhdzsBean.pic3,
                        bfhdzsBean.pic4,
                        bfhdzsBean.pic5,
                        bfhdzsBean.pic6,
                        bfhdzsBean.pic7,
                        bfhdzsBean.pic8,
                        bfhdzsBean.pic9
                ));

                contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                        bfhdzsBean.title,
                        bfhdzsBean.context,
                        urls,
                        bfhdzsBean.videoUrl,
                        bfhdzsBean.videoThumbnailUrl,
                        bfhdzsBean.createDate,
                        bfhdzsBean.villageName);

                contentDetailsBean.oneLable = "一、时间";
                contentDetailsBean.one = DateUtil.longToString(bfhdzsBean.activityDate, DateUtil.FORMAT_DATE);

                contentDetailsBean.twoLable = "";
                contentDetailsBean.two = "";

                ContentDetailsActivity.startActivity(RefreshRecyclerActivity.this, contentDetailsBean);
                break;

            //帮扶责任人管理
            case TYPE_BFZRRGL:
                BfzrrglBean bfzrrglBean = (BfzrrglBean) currencyAdapter.getItem(position);
                BfzrrglDetailsActivity.startActivity(RefreshRecyclerActivity.this, bfzrrglBean);
                break;

            //线上评比
            case TYPE_XSPB:
                lastPosition = position;
                XspbBean xspbBean = (XspbBean) currencyAdapter.getItem(position);

                //进行中
                if (0 == xspbBean.state) {
                    OnlineEvaluationActivity.startActivity(RefreshRecyclerActivity.this, xspbBean.voteId);
                }
                break;

            //村民事务代办
            case TYPE_CMSWDB:
                CmswdbBean cmswdbBean = (CmswdbBean) currencyAdapter.getItem(position);

                CmswdbDetailsActivity.startActivity(RefreshRecyclerActivity.this, cmswdbBean);
                break;

            //小微权力清单
            case TYPE_XWQLQD:
            case TYPE_FZDYGC:
            case TYPE_ZZGXZJ:
                XwqlqdBean xwqlqdBean = (XwqlqdBean) currencyAdapter.getItem(position);

                if (!TextUtils.isEmpty(xwqlqdBean.villagename)) {
                    unitName = xwqlqdBean.villagename;
                } else if (!TextUtils.isEmpty(xwqlqdBean.vtownsname)) {
                    unitName = xwqlqdBean.vtownsname;
                }

                urls.addAll(Arrays.asList(
                        xwqlqdBean.pic1,
                        xwqlqdBean.pic2,
                        xwqlqdBean.pic3,
                        xwqlqdBean.pic4,
                        xwqlqdBean.pic5,
                        xwqlqdBean.pic6,
                        xwqlqdBean.pic7,
                        xwqlqdBean.pic8,
                        xwqlqdBean.pic9
                ));

                contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                        xwqlqdBean.theme,
                        xwqlqdBean.content,
                        urls,
                        xwqlqdBean.videoUrl,
                        xwqlqdBean.videoThumbnailUrl,
                        xwqlqdBean.createtime,
                        unitName,
                        xwqlqdBean.pdfUrl,
                        xwqlqdBean.pdfrealname);


                stringBuffer = new StringBuffer();
                strings = new ArrayList<>();

                if (!TextUtils.isEmpty(xwqlqdBean.remark1)) {
                    strings.add(xwqlqdBean.remark1);
                }

                if (!TextUtils.isEmpty(xwqlqdBean.remark2)) {
                    strings.add(xwqlqdBean.remark2);
                }

                for (int i = 0; i < strings.size(); i++) {
                    if (i == strings.size() - 1) {
                        stringBuffer.append((i + 1) + "." + strings.get(i));
                    } else {
                        stringBuffer.append((i + 1) + "." + strings.get(i) + "\n");
                    }
                }
                //不显示数据
                contentDetailsBean.oneLable = ContentDetailsActivity.NOT_SHOW;

                ContentDetailsActivity.startActivity(RefreshRecyclerActivity.this, contentDetailsBean);
                break;

            case TYPE_XXJY:
            case TYPE_DCYJ:
            case TYPE_JSWT:
            case TYPE_ZGLS:
                ZtjyBean ztjyBean = (ZtjyBean) currencyAdapter.getItem(position);

                urls.addAll(Arrays.asList(
                        ztjyBean.pic1,
                        ztjyBean.pic2,
                        ztjyBean.pic3,
                        ztjyBean.pic4,
                        ztjyBean.pic5,
                        ztjyBean.pic6,
                        ztjyBean.pic7,
                        ztjyBean.pic8,
                        ztjyBean.pic9
                ));

                if (!TextUtils.isEmpty(ztjyBean.villagename)) {
                    unitName = ztjyBean.villagename;
                } else if (!TextUtils.isEmpty(ztjyBean.vtownsname)) {
                    unitName = ztjyBean.vtownsname;
                }

                contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                        ztjyBean.theme,
                        ztjyBean.content,
                        urls,
                        ztjyBean.videoUrl,
                        ztjyBean.videoThumbnailUrl,
                        ztjyBean.createDate,
                        unitName);

                contentDetailsBean.one = ztjyBean.staff;
                contentDetailsBean.two = ztjyBean.site;

                contentDetailsBean.oneLable = "一、人员信息";

                contentDetailsBean.twoLable = "二、活动地点";

                ContentDetailsActivity.startActivity(RefreshRecyclerActivity.this, contentDetailsBean);
                break;

            //党组织换届
            case TYPE_DZZHJ:
            case TYPE_CWHHJ:
                DzzhjBean dzzhjBean = (DzzhjBean) currencyAdapter.getItem(position);

                urls.addAll(Arrays.asList(
                        dzzhjBean.pic1,
                        dzzhjBean.pic2,
                        dzzhjBean.pic3,
                        dzzhjBean.pic4,
                        dzzhjBean.pic5,
                        dzzhjBean.pic6,
                        dzzhjBean.pic7,
                        dzzhjBean.pic8,
                        dzzhjBean.pic9
                ));

                if (!TextUtils.isEmpty(dzzhjBean.villagename)) {
                    unitName = dzzhjBean.villagename;
                } else if (!TextUtils.isEmpty(dzzhjBean.vtownsname)) {
                    unitName = dzzhjBean.vtownsname;
                }

                contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                        dzzhjBean.theme,
                        dzzhjBean.context,
                        urls,
                        "",
                        "",
                        dzzhjBean.createtime,
                        unitName);

                contentDetailsBean.oneLable = "一、选举时间";
                contentDetailsBean.one = DateUtil.longToString(dzzhjBean.publicityTime, DateUtil.FORMAT_DATE);
                contentDetailsBean.twoLable = "二、选举信息";
                if (TYPE_DZZHJ.equals(title)) {
                    contentDetailsBean.two = "1. 应到党员人数:" + dzzhjBean.totalNum + "\n2. 参选党员数量:" + dzzhjBean.electNum;
                } else {
                    contentDetailsBean.two = "1. 应到选民人数:" + dzzhjBean.totalNum + "\n2. 参选选民数量:" + dzzhjBean.electNum;
                }

                contentDetailsBean.threeLable = "三、选举结果";
                contentDetailsBean.three = dzzhjBean.typeVal;

                contentDetailsBean.fourLable = "";
                contentDetailsBean.four = "";

                ContentDetailsActivity.startActivity(RefreshRecyclerActivity.this, contentDetailsBean);
                break;

            //设岗定责
            case TYPE_SGDZ:
                sgdzBean = (SgdzBean) currencyAdapter.getItem(position);

                SgdzDetailsActivity.startActivity(RefreshRecyclerActivity.this, SgdzDetailsActivity.TYPE_SGDZ, sgdzBean);
                break;
        }
    }

    /**
     * 常见问题适配器
     */
    private void initCjwtAdapter() {
        List<MultiItemEntity> problemList = new ArrayList<>();

        problemAdapter = new ProblemAdapter(problemList);
        rvList.addItemDecoration(
                new MyRecyclerViewDivider(
                        this, LinearLayoutManager.VERTICAL, 10, ContextCompat.getColor(this, R.color.color_eee))
        );
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(problemAdapter);
    }

    /**
     * 政务服务电话
     */
    private void initZwfwdhAdapter() {
        servicePhoneAdapter = new ServicePhoneAdapter(R.layout.item_service_phone, new ArrayList<>());
        servicePhoneAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                ServicePhoneBean servicePhoneBean = servicePhoneAdapter.getData().get(position);
//                phone = servicePhoneBean.tel;
//                showCallDialog();
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(servicePhoneAdapter);
    }

    /**
     * 获取事件督办
     */
    public void initSjdbAdapter() {
        List<SjdbBean> sjdbBeans = new ArrayList<>();

        eventSupervisorAdapter = new EventSupervisorAdapter(R.layout.item_event_supervisor, sjdbBeans);
        eventSupervisorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SjdbBean sjdbBean = eventSupervisorAdapter.getItem(position);
                EventSupervisorActivity.startActivity(RefreshRecyclerActivity.this, sjdbBean.id);
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(eventSupervisorAdapter);
    }

    /**
     * 初始化图说本村适配器
     */
    public void initTsbcAdapter() {
        tsbcAdapter = new TsbcAdapter(R.layout.item_tsbc, new ArrayList<>());
        tsbcAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(tsbcAdapter);
    }

    /**
     * 初始化我要咨询适配器
     */
    public void initWyzxAdapter() {
        myConsultationAdapter = new MyConsultationAdapter(R.layout.item_consultation, new ArrayList<>());
        myConsultationAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                lastPosition = position;
                ConsultationDetailsActivity.startActivity(RefreshRecyclerActivity.this, myConsultationAdapter.getItem(position).id);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(myConsultationAdapter);
    }

    /**
     * 设置数据
     */
    private void initListBeans(List newsList, BaseQuickAdapter adapter) {
        //隐藏骨架图
        hideSkeletonScreen();

        if (1 == pageNum) {
            refreshLayout.setEnableLoadMore(true);
            //刷新加载更多状态
            refreshLayout.resetNoMoreData();
            adapter.replaceData(newsList);
        } else {
            adapter.addData(newsList);
        }
        refreshLayout.setEnableLoadMore(true);
    }

    /**
     * 设置页数
     *
     * @param pageNum
     */
    private void setPageNum(int pageNum) {
        this.pageNum = pageNum;
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

    /**
     * 关闭动画
     *
     * @param pageNum
     */
    private void finishRefresh(int pageNum, boolean success) {
        if (1 == pageNum) {
            refreshLayout.finishRefresh(success);
            refreshLayout.setEnableFooterFollowWhenNoMoreData(true);
        } else {
            refreshLayout.finishLoadMore(success);
        }

        if (1 == pageNum && !success) {
            refreshLayout.setEnableLoadMore(false);
        }
    }

    //当前拨打的电话
    private String phone;

    private MaterialDialog callDialog = null;

    /**
     * 显示电话框
     */
    private void showCallDialog() {
        View inflate = View.inflate(this, R.layout.dialog_call, null);
        TextView tvNumber = inflate.findViewById(R.id.tv_title);
        TextView tvCall = inflate.findViewById(R.id.tv_ok);
        TextView tvCancle = inflate.findViewById(R.id.tv_cancle);

        tvNumber.setText(phone);

        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callDialog) {
                    callDialog.dismiss();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(RefreshRecyclerActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        PermissionGen.with(RefreshRecyclerActivity.this)
                                .addRequestCode(1)
                                .permissions(
                                        Manifest.permission.CALL_PHONE
                                ).request();
                    } else {
                        callPhone(phone);
                    }
                } else {
                    callPhone(phone);
                }
            }
        });

        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != callDialog) {
                    callDialog.dismiss();
                }
            }
        });

        callDialog = new MaterialDialog.Builder(this)
                .customView(inflate, false)
                .build();
        //点击对话框以外的地方，对话框不消失
        callDialog.setCanceledOnTouchOutside(false);
        //点击对话框以外的地方和返回键，对话框都不消失
        //callDialog.setCancelable(false);
        callDialog.show();
    }

    public void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 得到拨号权限
     */
    @PermissionSuccess(requestCode = 1)
    public void takePhoto() {
        callPhone(phone);
    }

    /**
     * 没得到拨号权限
     */
    @PermissionFail(requestCode = 1)
    public void take() {
        ToastHelper.showShort("拨号需要获取权限");
    }

    /**
     * 获取数据
     *
     * @param pageNum
     */
    private void getData(int pageNum) {
        switch (title) {
            //记事本
            case TYPE_JSB:
                getJsbData(pageNum);
                break;

            //村务公开
            case TYPE_CWGK:
                getCwgkData(pageNum);
                break;

            //党务公开
            case TYPE_DWGK:
                getCwgkData(pageNum);
                break;

            //党组织换届
            case TYPE_DZZHJ:
                getDzzhjData(pageNum, Urls.QUERY_PARTY_ELECTION_ACTION);
                break;

            //村委会换届
            case TYPE_CWHHJ:
                getDzzhjData(pageNum, Urls.QUERY_HAMLET_ELECTION_ACTION);
                break;

            //党员联系户
            case TYPE_DYLXH:
                getDylxhData(pageNum);
                break;

            //微心愿
            case TYPE_WXY:
                break;

            //帮扶活动展示
            case TYPE_ZYFW:
                getBfhdzsData(pageNum);
                break;

            //政务服务电话
            case TYPE_ZWFWDH:
                getZwfwdhData(pageNum);
                break;

            //帮扶责任人管理
            case TYPE_BFZRRGL:
                getBfzrrglData(pageNum);
                break;

            //常见问题
            case TYPE_CJWT:
                getCjwtData(searchContent, pageNum);
                break;

            //线上评比
            case TYPE_XSPB:
                getXspbData(pageNum);
                break;

            //事件督办
            case TYPE_SJDB:
                getSjdbData(pageNum);
                break;

            //村民事务代办
            case TYPE_CMSWDB:
                getCmswdbData(pageNum);
                break;

            //我要咨询
            case TYPE_WYZX:
                getWyzxData(pageNum);
                break;

            //小微权力清单
            case TYPE_XWQLQD:
                getXwqlqdData(pageNum);
                break;

            //学习教育
            case TYPE_XXJY:
                //调查研究
            case TYPE_DCYJ:
                //检视问题
            case TYPE_JSWT:
                //整改落实
            case TYPE_ZGLS:
                getZtjyData(pageNum);
                break;

            //发展党员规程
            case TYPE_FZDYGC:
                getFzdygcData(pageNum);
                break;

            //组织关系转接
            case TYPE_ZZGXZJ:
                getZzgxzjData(pageNum);
                break;

            //设岗定责
            case TYPE_SGDZ:
                getSgdzData(pageNum);
                break;

            //图说本村
            case TYPE_JTJJ:
            case TYPE_LDGZ:
            case TYPE_RYBZ:
            case TYPE_TSBC:
                getTsbcData(pageNum);
                break;
        }
    }

    /**
     * 获取记事本数据
     *
     * @param pageNum
     */
    private void getJsbData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .getNoteList(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<NoteBean>>() {
                    @Override
                    public void onSuccess(BaseData<NoteBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<NoteBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    //设置之后，将不会再触发加载事件
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 图说本村
     */
    private void getTsbcData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryTsbcList(requestUrl, pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<TsbcBean>>() {
                    @Override
                    public void onSuccess(BaseData<TsbcBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<TsbcBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, tsbcAdapter);
                            } else {
                                if (1 == pageNum) {
                                    tsbcAdapter.replaceData(new ArrayList<>());
                                    tsbcAdapter.setEmptyView(emptyView);
                                } else {
                                    //设置之后，将不会再触发加载事件
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            tsbcAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 设岗定责
     */
    private void getSgdzData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .querySgdz(pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<SgdzBean>>() {
                    @Override
                    public void onSuccess(BaseData<SgdzBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<SgdzBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    //设置之后，将不会再触发加载事件
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取主题教育
     *
     * @param pageNum
     */
    private void getZtjyData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryZtjys(pageNum, PAGE_SIZE, Integer.valueOf(accountBean.unitId), eductype)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ZtjyBean>>() {
                    @Override
                    public void onSuccess(BaseData<ZtjyBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<ZtjyBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    //设置之后，将不会再触发加载事件
                                    refreshLayout.finishLoadMoreWithNoMoreData();
                                }
                                hideSkeletonScreen();
                                refreshLayout.setEnableLoadMore(false);
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);
                            ToastHelper.showShort(baseData.message);

                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    //获取常见问题-start
    private void getCjwtData(String content, int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryCjwt(pageNum, PAGE_SIZE, content)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ProblemBean>>() {
                    @Override
                    public void onSuccess(BaseData<ProblemBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<ProblemBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                ProblemParentItem parentItem = null;
                                ProblemChildItem childItem = null;

                                List<ProblemParentItem> problemList = new ArrayList<>();

                                for (int i = 0; i < dataList.size(); i++) {
                                    //创建问题
                                    parentItem = new ProblemParentItem(dataList.get(i).title);
                                    //创建答案
                                    childItem = new ProblemChildItem(dataList.get(i).content);
                                    parentItem.addSubItem(childItem);
                                    problemList.add(parentItem);
                                }

                                //刷新数据
                                initListBeans(problemList, problemAdapter);
                            } else {
                                if (1 == pageNum) {
                                    problemAdapter.replaceData(new ArrayList<>());
                                    problemAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }
                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            problemAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());

                    }
                });
    }
    //常见问题适配器-end

    /**
     * 获取村务公开的数据
     */
    private void getCwgkData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryCwgks(pageNum, PAGE_SIZE, TYPE_DWGK.equals(title) ? 1 : 2, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<CwgkBean>>() {
                    @Override
                    public void onSuccess(BaseData<CwgkBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<CwgkBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }
                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取线上评比的数据
     */
    private void getXspbData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryXspbs(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<AdvisoryBean>>() {
                    @Override
                    public void onSuccess(BaseData<AdvisoryBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<AdvisoryBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取我要咨询的数据
     *
     * @param pageNum
     */
    private void getWyzxData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryWyzxs(pageNum, PAGE_SIZE, Integer.valueOf(accountBean.id))
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<AdvisoryBean>>() {
                    @Override
                    public void onSuccess(BaseData<AdvisoryBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<AdvisoryBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, myConsultationAdapter);
                            } else {
                                if (1 == pageNum) {
                                    myConsultationAdapter.replaceData(new ArrayList<>());
                                    myConsultationAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            myConsultationAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取小微权力清单的数据
     *
     * @param pageNum
     */
    private void getXwqlqdData(int pageNum) {
//        HttpUtils.getInstance(this)
//                .getRetofitClinet()
//                .setBaseUrl(Urls.BASE_URL)
//                .builder(StudyApi.class)
//                .queryXwqlqds(pageNum, PAGE_SIZE,0,0)
//                .compose(this.bindToLifecycle())
////                .retryWhen(new RetryWithDelay(3, 300))
//                .compose(new CustomCompose())
//                .subscribe(new RxSubscriber<BaseData<XwqlqdBean>>() {
//                    @Override
//                    public void onSuccess(BaseData<XwqlqdBean> baseData) {
//                        if (BaseData.SUCCESS.equals(baseData.result)) {
//                            List<XwqlqdBean> dataList = baseData.dataList;
//                            if (null != dataList && dataList.size() > 0) {
//                                if (1 == pageNum) {
//                                    setPageNum(1);
//                                } else {
//                                    setPageNum(pageNum);
//                                }
//
//                                //刷新数据
//                                initListBeans(dataList, currencyAdapter);
//                            } else {
//                                if (1 == pageNum) {
//                                    currencyAdapter.replaceData(new ArrayList<>());
//                                    currencyAdapter.setEmptyView(emptyView);
//                                } else {
//                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
//                                }
//                                hideSkeletonScreen();
//                            }
//
//                            //结束刷新
//                            finishRefresh(pageNum, true);
//                        } else {
//                            //隐藏骨架图
//                            hideSkeletonScreen();
//
//                            //结束刷新
//                            finishRefresh(pageNum, false);
//
//                            ToastHelper.showShort(baseData.message);
//                        }
//                    }
//
//                    @Override
//                    protected void onError(ResponeThrowable responeThrowable) {
//                        //隐藏骨架图
//                        hideSkeletonScreen();
//
//                        finishRefresh(pageNum, false);
//
//                        if (1 == pageNum) {
//                            currencyAdapter.setEmptyView(errorView);
//                        }
//                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
//                    }
//                });
    }

    /**
     * 获取发展党员规程的数据
     *
     * @param pageNum
     */
    private void getFzdygcData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryFzdygc(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<XwqlqdBean>>() {
                    @Override
                    public void onSuccess(BaseData<XwqlqdBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<XwqlqdBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取组织关系转接的数据
     *
     * @param pageNum
     */
    private void getZzgxzjData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryZzgxzj(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<XwqlqdBean>>() {
                    @Override
                    public void onSuccess(BaseData<XwqlqdBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<XwqlqdBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取村民事务代办
     *
     * @param pageNum
     */
    private void getCmswdbData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryCmswdbs(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<CmswdbBean>>() {
                    @Override
                    public void onSuccess(BaseData<CmswdbBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<CmswdbBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 获取事件督办的数据
     *
     * @param pageNum
     */
    private void getSjdbData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryUrgeDetails(pageNum, PAGE_SIZE, Integer.valueOf(accountBean.id))
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<SjdbBean>>() {
                    @Override
                    public void onSuccess(BaseData<SjdbBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<SjdbBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, eventSupervisorAdapter);
                            } else {
                                if (1 == pageNum) {
                                    eventSupervisorAdapter.replaceData(new ArrayList<>());
                                    eventSupervisorAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            eventSupervisorAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 帮扶活动展示
     */
    private void getBfzrrglData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryBfzrrs(pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<BfzrrglBean>>() {
                    @Override
                    public void onSuccess(BaseData<BfzrrglBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<BfzrrglBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 帮扶活动展示
     */
    private void getBfhdzsData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryBfhdzss(pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<BfhdzsBean>>() {
                    @Override
                    public void onSuccess(BaseData<BfhdzsBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<BfhdzsBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }

                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 党员联系户
     */
    private void getDylxhData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryPartyContacts(pageNum, PAGE_SIZE, requestUnitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<SgdzBean>>() {
                    @Override
                    public void onSuccess(BaseData<SgdzBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<SgdzBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }
                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 政务服务电话
     */
    private void getZwfwdhData(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryServicePhones(pageNum, PAGE_SIZE)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<ServicePhoneBean>>() {
                    @Override
                    public void onSuccess(BaseData<ServicePhoneBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<ServicePhoneBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                //刷新数据
                                initListBeans(dataList, servicePhoneAdapter);
                            } else {
                                if (1 == pageNum) {
                                    servicePhoneAdapter.replaceData(new ArrayList<>());
                                    servicePhoneAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }
                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();
                        finishRefresh(pageNum, false);
                        if (1 == pageNum) {
                            servicePhoneAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 党组织换届
     */
    private void getDzzhjData(int pageNum, String url) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDzzhjs(url, pageNum, PAGE_SIZE, accountBean.unitId)
                .compose(this.bindToLifecycle())
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DzzhjBean>>() {
                    @Override
                    public void onSuccess(BaseData<DzzhjBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DzzhjBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }

                                //刷新数据
                                initListBeans(dataList, currencyAdapter);
                            } else {
                                if (1 == pageNum) {
                                    currencyAdapter.replaceData(new ArrayList<>());
                                    currencyAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                hideSkeletonScreen();
                            }
                            //结束刷新
                            finishRefresh(pageNum, true);
                        } else {
                            //隐藏骨架图
                            hideSkeletonScreen();

                            //结束刷新
                            finishRefresh(pageNum, false);

                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        //隐藏骨架图
                        hideSkeletonScreen();

                        finishRefresh(pageNum, false);

                        if (1 == pageNum) {
                            currencyAdapter.setEmptyView(errorView);
                        }
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        register();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (TYPE_SJDB.equals(title)) {
            EventBus.getDefault().post(new EventMessage(Messages.NEW_SJDB, ""));
        }

        unRegister();
    }

    private void register() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //注册EventBsus,注意参数是this，传入activity会报错
            EventBus.getDefault().register(this);
        }
    }

    private void unRegister() {
        if (EventBus.getDefault().isRegistered(this)) {
            //注册EventBus,注意参数是this，传入activity会报错
            EventBus.getDefault().unregister(this);
        }
    }

    //EventBus的处理事件函数  该方法可以随意取名 必须为public 必须添加注解并指定线程模型
    //之后EventBus会自动扫描到此函数，进行数据传递
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void processMessages(EventMessage eventMessage) {
        int updatePosition = -1;
        switch (eventMessage.message) {
            //更新我要咨询列表
            case Messages.UPDATE_WYZX:
                //最后改变的bean对象
                updatePosition = -1;

                AdvisoryBean advisoryBean = (AdvisoryBean) eventMessage.t;

                for (int i = 0; i < myConsultationAdapter.getData().size(); i++) {
                    if (advisoryBean.id == myConsultationAdapter.getItem(i).id) {
                        updatePosition = i;
                    }
                }

                //更新列表
                if (-1 != updatePosition) {
                    myConsultationAdapter.setData(updatePosition, advisoryBean);
                    myConsultationAdapter.notifyItemChanged(lastPosition);
                    myConsultationAdapter.notifyDataSetChanged();
                    rvList.scrollToPosition(lastPosition);
                } else {
                    myConsultationAdapter.addData(advisoryBean);
                }
                break;

            //更新线上评比
            case Messages.UPDATE_XSPB:
                //最后改变的bean对象
                updatePosition = -1;

                XspbBean bean = (XspbBean) eventMessage.t;

                for (int i = 0; i < currencyAdapter.getData().size(); i++) {
                    XspbBean xspbBean = (XspbBean) currencyAdapter.getItem(i);
                    if (bean.id == xspbBean.id) {
                        updatePosition = i;
                    }
                }

                //更新列表
                if (-1 != updatePosition) {
                    currencyAdapter.setData(updatePosition, bean);
                    currencyAdapter.notifyItemChanged(lastPosition);
                    currencyAdapter.notifyDataSetChanged();
                    rvList.scrollToPosition(lastPosition);
                }
                break;


            //刷新列表
            case Messages.REFRESH_RECYCLER:
                refreshLayout.autoRefresh();
                break;
        }
    }

    /*********************************************地址的筛选框---start******************************/
    //乡镇ID
    private int townshipId;
    //街村ID
    private int villageId;

    //当前最小的id
    private int requestUnitId;

    //乡镇名称
    private String townshipName;
    //街村名称
    private String villageName;

    //呼叫的号码
    private String callNumber;

    //左边全辛集市的id
    private int LEFT_ALL_ID = 0x1111;

    //右边全部的id
    private int RIGHT_ALL_ID = 0x1112;

    private List<DepartmentBean> leftDepartments;

    /**
     * 获取组织信息
     */
    private void getDepartmentList(int currentUnitId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryDepartment(currentUnitId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DepartmentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DepartmentBean> dataList = baseData.dataList;
                            if (null != dataList && dataList.size() > 0) {
                                DepartmentBean departmentBean = null;

                                //如果左边的列表等于null，代表第一次请求
                                if (null == leftDepartments) {
                                    leftDepartments = dataList;


                                    //添加辛集市在左边
                                    departmentBean = new DepartmentBean();
                                    departmentBean.id = 1;
                                    departmentBean.departmentName = "辛集市";
                                    leftDepartments.add(0, departmentBean);

                                    //如果是用户bean为空
                                    showScreenDialog(leftDepartments, new ArrayList<>());
                                } else {
                                    //代表点击的辛集市
                                    if (1 == townshipId) {
                                        //添加辛集市在左边
                                        departmentBean = new DepartmentBean();
                                        departmentBean.id = 1;
                                        departmentBean.departmentName = "辛集市";
                                        dataList.add(0, departmentBean);
                                    }
                                    screenCityDialog.setRightNewData(dataList);
                                }
                            } else {
                                if (null != leftDepartments) {
                                    screenCityDialog.setRightNewData(new ArrayList<>());
                                }
                            }
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    private ScreenCityPopupWindow screenCityDialog;

    /**
     * 显示筛选对话框
     */
    private void showScreenDialog(List<DepartmentBean> leftData,
                                  List<DepartmentBean> rightData) {
        if (null == screenCityDialog) {
            screenCityDialog = new ScreenCityPopupWindow(
                    this,
                    DisplayUtil.getScreenWidth(),
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    leftData,
                    rightData,
                    true
            );

            screenCityDialog.setOnScreenClick(new ScreenCityPopupWindow.onScreenClick() {
                @Override
                public void onLeftClick(int cityId, String departmentName) {
                    townshipName = departmentName;
                    if (LEFT_ALL_ID == cityId) {
                        if (isLogin() && 2 == accountBean.level) {
                            townshipId = accountBean.unitId;
                        } else {
                            townshipId = 0;
                        }
                        villageId = 0;
                        villageName = "";
                        tvScreen.setText(townshipName + villageName);

                        screenCityDialog.setRightNewData(new ArrayList<>());
                        screenCityDialog.dismiss();
                        refreshLayout.autoRefresh();
                    } else {
                        townshipId = cityId;
                        getDepartmentList(cityId);
                    }
                }

                @Override
                public void onRightClick(int cityId, String departmentName, String terUri) {
                    if (RIGHT_ALL_ID == cityId) {
                        villageId = 0;
                        villageName = "";
                        screenCityDialog.dismiss();
                    } else {
                        if (RIGHT_ALL_ID == cityId) {
                            villageId = 0;

                            villageName = "";

                            screenCityDialog.dismiss();
                            refreshLayout.autoRefresh();
                        } else {
                            //如果没登录，并且是微心愿
                            villageId = cityId;

                            requestUnitId = villageId;

                            villageName = departmentName;

                            screenCityDialog.dismiss();
                            refreshLayout.autoRefresh();
                        }
                        if (townshipName.equals(villageName)) {
                            tvScreen.setText(villageName);
                        } else {
                            tvScreen.setText(townshipName + villageName);
                        }
                    }
                }
            });
        }
        showCityDialog();
    }

    private void showCityDialog() {
        screenCityDialog.setAlignBackground(false);
        screenCityDialog.showPopupWindow(rlScreen);
//        screenCityDialog.setPopupGravity(Gravity.BOTTOM);
    }
    /*********************************************地址的筛选框---end******************************/


//    //乡镇ID
//    private int townshipId;
//    //街村ID
//    private int villageId;
//
//    //乡镇名称
//    private String townshipName;
//    //街村名称
//    private String villageName;
//
//    //左边全辛集市的id
//    private int LEFT_ALL_ID = 0x1111;
//
//    //右边全部的id
//    private int RIGHT_ALL_ID = 0x1112;

//    private List<DepartmentBean> leftDepartments;
//
//    /**
//     * 获取组织信息
//     */
//    private void getDepartmentList(int currentUnitId) {
//        HttpUtils.getInstance(this)
//                .getRetofitClinet()
//                .setBaseUrl(Urls.BASE_URL)
//                .builder(StudyApi.class)
//                .queryDepartment(currentUnitId)
//                .compose(this.bindToLifecycle())
////                .retryWhen(new RetryWithDelay(3, 300))
//                .compose(new CustomCompose())
//                .subscribe(new RxSubscriber<BaseData<DepartmentBean>>() {
//                    @Override
//                    public void onSuccess(BaseData<DepartmentBean> baseData) {
//                        if (BaseData.SUCCESS.equals(baseData.result)) {
//                            List<DepartmentBean> dataList = baseData.dataList;
//                            if (null != dataList && dataList.size() > 0) {
//                                DepartmentBean departmentBean = new DepartmentBean();
//
//                                //如果左边的列表等于null，代表第一次请求
//                                if (null == leftDepartments) {
//                                    leftDepartments = dataList;
//                                    //如果是用户bean为空
//                                    departmentBean.departmentName = "全辛集市";
//                                    departmentBean.id = LEFT_ALL_ID;
//                                    leftDepartments.add(0, departmentBean);
//                                    showScreenDialog(leftDepartments, new ArrayList<>());
//                                } else {
//                                    departmentBean.departmentName = "全部";
//                                    departmentBean.id = RIGHT_ALL_ID;
//                                    dataList.add(0, departmentBean);
//                                    screenCityDialog.setRightNewData(dataList);
//                                }
//                            } else {
//                                if (null != leftDepartments) {
//                                    screenCityDialog.setRightNewData(new ArrayList<>());
//                                }
//                            }
//                        } else {
//                            ToastHelper.showShort(baseData.message);
//                        }
//                    }
//
//                    @Override
//                    protected void onError(ResponeThrowable responeThrowable) {
//                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
//                    }
//                });
//    }
//
//    private ScreenCityPopupWindow screenCityDialog;
//
//    /**
//     * 显示筛选对话框
//     */
//    private void showScreenDialog(List<DepartmentBean> leftData,
//                                  List<DepartmentBean> rightData) {
//        if (null == screenCityDialog) {
//            screenCityDialog = new ScreenCityPopupWindow(
//                    this,
//                    DisplayUtil.getScreenWidth(),
//                    DisplayUtil.getScreenHeight() / 2,
//                    leftData,
//                    rightData
//            );
//
//            screenCityDialog.setOnScreenClick(new ScreenCityPopupWindow.onScreenClick() {
//                @Override
//                public void onLeftClick(int cityId, String departmentName) {
//                    townshipName = departmentName;
//                    if (LEFT_ALL_ID == cityId) {
//                        if (isLogin() && 2 == accountBean.level) {
//                            townshipId = accountBean.unitId;
//                        } else {
//                            townshipId = 0;
//                        }
//                        villageId = 0;
//
//                        villageName = "";
//
//                        tvScreen.setText(townshipName + villageName);
//
//                        screenCityDialog.setRightNewData(new ArrayList<>());
//                        screenCityDialog.dismiss();
//                        refreshLayout.autoRefresh();
//                    } else {
//                        townshipId = cityId;
//                        getDepartmentList(cityId);
//                    }
//                }
//
//                @Override
//                public void onRightClick(int cityId, String departmentName, String terUri) {
//                    if (RIGHT_ALL_ID == cityId) {
//                        villageId = 0;
//
//                        villageName = "";
//
//                        screenCityDialog.dismiss();
//                        refreshLayout.autoRefresh();
//                    } else {
//                        //如果没登录，并且是微心愿
//                        villageId = cityId;
//
//                        villageName = departmentName;
//
//                        screenCityDialog.dismiss();
//                        refreshLayout.autoRefresh();
//                    }
//                    tvScreen.setText(townshipName + villageName);
//                }
//            });
//        }
//        screenCityDialog.setAlignBackground(true);
//        screenCityDialog.showPopupWindow(rlScreen);
//    }
}
