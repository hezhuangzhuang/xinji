package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jaeger.library.StatusBarUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.zxwl.commonlibrary.utils.KeyBoardUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.response.DocumentBean;
import com.zxwl.network.bean.response.SearchBean;
import com.zxwl.network.bean.response.StudyBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.DocumentAdapter;
import com.zxwl.xinji.adapter.SearchAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.widget.ContainsEmojiEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 搜索界面
 */
public class SearchActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private ContainsEmojiEditText etContent;
    private RecyclerView rvList;

    private SmartRefreshLayout refreshLayout;

    private SearchAdapter searchAdapter;

    private DocumentAdapter documentAdapter;


    private String content;
    private View emptyView;

    private int pageNum;
    private int PAGE_SIZE = 10;

    public static final String TITLE = "TITLE";
    public static final String TYPE = "TYPE";

    public static final String TYPE_ZGWJ = "TYPE_ZGWJ";

    private String title;
    private int type;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, String title, int type) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        etContent = (ContainsEmojiEditText) findViewById(R.id.et_content);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);

        emptyView = getLayoutInflater().inflate(R.layout.include_empty, (ViewGroup) rvList.getParent(), false);
        emptyView.findViewById(R.id.tv_retry).setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        title = getIntent().getStringExtra(TITLE);
        type = getIntent().getIntExtra(TYPE, -1);

        if (TYPE_ZGWJ.equals(title)) {
            initDocumentAdapter();
        } else {
            initListAdapter();
        }
    }

    private void initListAdapter() {
        searchAdapter = new SearchAdapter(R.layout.item_text, new ArrayList<>());
        searchAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SearchBean item = (SearchBean) adapter.getItem(position);
                getStudyDetails(item.id);
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(searchAdapter);
    }

    private void initDocumentAdapter() {
        documentAdapter = new DocumentAdapter(R.layout.item_document, new ArrayList<>());
        documentAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DocumentBean item = (DocumentBean) documentAdapter.getItem(position);
                DocumentDetailsActivity.startActivity(SearchActivity.this, item);
            }
        });
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(documentAdapter);
    }

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
                    content = s.toString();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getData(content, 1);
                        }
                    }, 500);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapterClear();
                        }
                    }, 500);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableFooterFollowWhenNoMoreData(true);

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                KeyBoardUtil.closeKeybord(etContent, SearchActivity.this);
                getData(content, pageNum + 1);
            }
        });
        refreshLayout.setEnableLoadMore(false);
    }

    public void adapterClear() {
        if (TYPE_ZGWJ.equals(title)) {
            documentAdapter.getData().clear();
            documentAdapter.notifyDataSetChanged();
        } else {
            searchAdapter.getData().clear();
            searchAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    private void getData(String content, int pageNum) {
        if (TYPE_ZGWJ.equals(title)) {
            searchDocument(content, pageNum);
        } else {
            searchContent(content, pageNum);
        }
    }

    /**
     * 搜索组工文件
     *
     * @param content
     * @param pageNum
     */
    private void searchDocument(String content, int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .searchDocument(StringUtils.encoder(content), pageNum, PAGE_SIZE, type)
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<DocumentBean>>() {
                    @Override
                    public void onSuccess(BaseData<DocumentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<DocumentBean> dataList = baseData.dataList;

                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                    initListBeans(documentAdapter, dataList, true, content);
                                } else {
                                    setPageNum(pageNum);
                                    initListBeans(documentAdapter, dataList, false, content);
                                }
                            } else {
                                if (1 == pageNum) {
                                    documentAdapter.replaceData(new ArrayList<>());
                                    documentAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                            }
                            finishRefresh(pageNum, true);
                        } else {
                            finishRefresh(pageNum, false);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        finishRefresh(pageNum, false);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 搜索内容
     *
     * @param content
     * @param pageNum
     */
    private void searchContent(String content, int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .searchInfo(StringUtils.encoder(content), pageNum, PAGE_SIZE, "")
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<SearchBean>>() {
                    @Override
                    public void onSuccess(BaseData<SearchBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<SearchBean> dataList = baseData.dataList;

                            if (null != dataList && dataList.size() > 0) {
                                if (1 == pageNum) {
                                    setPageNum(1);
                                    initListBeans(searchAdapter, dataList, true, content);
                                } else {
                                    setPageNum(pageNum);
                                    initListBeans(searchAdapter, dataList, false, content);
                                }
                            } else {
                                if (1 == pageNum) {
                                    searchAdapter.replaceData(new ArrayList<>());
                                    searchAdapter.setEmptyView(emptyView);
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                            }
                            finishRefresh(pageNum, true);
                        } else {
                            finishRefresh(pageNum, false);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        finishRefresh(pageNum, false);
                        ToastHelper.showShort("请求失败,error：" + responeThrowable.getCause().toString());
                    }
                });
    }

    /**
     * 关闭动画
     *
     * @param pageNum
     */
    private void finishRefresh(int pageNum, boolean success) {
        if (1 == pageNum) {
            refreshLayout.finishRefresh(success);
        } else {
            refreshLayout.finishLoadMore(success);
        }
    }

    private void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    private void initListBeans(BaseQuickAdapter adapter, List dataList, boolean isRefresh, String searchContetn) {
        //如果是刷新则清空之前的数据
        if (isRefresh) {
            refreshLayout.setEnableLoadMore(true);
            refreshLayout.resetNoMoreData();
            adapter.getData().clear();
        }

        if (adapter instanceof SearchAdapter) {
            searchAdapter.setSearchContent(searchContetn);
        } else {
            documentAdapter.setSearchContent(searchContetn);
        }

        adapter.getData().addAll(dataList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取文章详情
     *
     * @param itemId
     */
    private void getNewsDetails(String itemId) {
//        HttpUtils.getInstance(this)
//                .getRetofitClinet()
//                .setBaseUrl(Urls.BASE_URL)
//                .builder(StudyApi.class)
//                .querycNewsDetails(0, Integer.valueOf(itemId))
////                .retryWhen(new RetryWithDelay(3, 300))
//                .compose(new CustomCompose())
//                .compose(bindToLifecycle())
//                .subscribe(new RxSubscriber<CollectionNewsBean>() {
//                    @Override
//                    public void onSuccess(CollectionNewsBean baseData) {
//                        if (BaseData.SUCCESS.equals(baseData.result)) {
////                            ArticleDetailsActivity.startActivity(SearchActivity.this, baseData.data);
//                        } else {
//                            ToastHelper.showShort(baseData.message);
//                        }
//                    }
//
//                    @Override
//                    protected void onError(ResponeThrowable responeThrowable) {
//                    }
//                });
    }

    /**
     * 获取视频详情
     *
     * @param itemId
     */
    private void getStudyDetails(String itemId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryStudyDetails(itemId)
//              .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<StudyBean>() {
                    @Override
                    public void onSuccess(StudyBean baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            StudyBean.DataBean dataBean = baseData.data;

                            List<String> urls = new ArrayList<>();

                            urls.addAll(Arrays.asList(
                                    dataBean.pic1,
                                    dataBean.pic2,
                                    dataBean.pic3,
                                    dataBean.pic4,
                                    dataBean.pic5,
                                    dataBean.pic6,
                                    dataBean.pic7,
                                    dataBean.pic8,
                                    dataBean.pic9
                            ));

                            ContentDetailsBean contentDetailsBean = ContentDetailsBean.ContentDetailsBeanFactory(
                                    dataBean.title,
                                    dataBean.context,
                                    urls,
                                    dataBean.videoUrl,
                                    dataBean.videoThumbnailUrl,
                                    dataBean.createDate,
                                    dataBean.announcer
                            );
                            //不显示
                            contentDetailsBean.oneLable = ContentDetailsActivity.NOT_SHOW;

                            //TODO:详情是否有评论
                            contentDetailsBean.isComment = true;

                            contentDetailsBean.id = dataBean.id;

                            ContentDetailsActivity.startActivity(SearchActivity.this, contentDetailsBean);
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("查询视频详情失败" + responeThrowable.getCause().toString());
                    }
                });
    }
}
