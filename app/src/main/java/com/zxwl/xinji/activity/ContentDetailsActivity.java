package com.zxwl.xinji.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.immersionbar.ImmersionBar;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.NotificationHelper;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.Urls;
import com.zxwl.network.api.StudyApi;
import com.zxwl.network.bean.BaseData;
import com.zxwl.network.bean.EventMessage;
import com.zxwl.network.bean.response.CommentBean;
import com.zxwl.network.bean.response.LoginBean;
import com.zxwl.network.bean.response.StudyNewsBean;
import com.zxwl.network.callback.RxSubscriber;
import com.zxwl.network.exception.ResponeThrowable;
import com.zxwl.network.func.CustomCompose;
import com.zxwl.network.http.HttpUtils;
import com.zxwl.network.utils.Messages;
import com.zxwl.xinji.R;
import com.zxwl.xinji.adapter.CommentAdapter;
import com.zxwl.xinji.adapter.FullyGridLayoutManager;
import com.zxwl.xinji.adapter.ShowImageAdapter;
import com.zxwl.xinji.base.BaseActivity;
import com.zxwl.xinji.bean.ContentDetailsBean;
import com.zxwl.xinji.utils.Constant;
import com.zxwl.xinji.utils.PreferenceUtil;
import com.zxwl.xinji.utils.StringUtils;
import com.zxwl.xinji.utils.WeChatUtil;
import com.zxwl.xinji.widget.CustomInputDialog;
import com.zxwl.xinji.widget.ShareSelectDialog;
import com.zxwl.xinji.widget.VideoListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 内容详情
 */
public class ContentDetailsActivity extends BaseActivity implements View.OnClickListener, CancelAdapt {
    private ContentDetailsBean contentDetailsBean;

    private WeakReference<ContentDetailsActivity> weakReference;

    public static final String CONTENT_BEAN = "CONTENT_BEAN";

    private SmartRefreshLayout refreshLayout;
    private RecyclerView rvList;

    private ShowImageAdapter selectImageAdapter;
    private List<LocalMedia> selectListImage;

    private ImageView ivBackOperate;
    private RelativeLayout rlTopTitle;
    private TextView tvTopTitle;

    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvWebsite;

    private WebView webView;

    private List<LocalMedia> webImages;

    private View headView;

    private ImageView ivRightOperate;

    private LinearLayout llAddress;
    private TextView tvAddress;
    private TextView tvAddressLable;
    private TextView tvPersonnelLable;
    private TextView tvPersonnel;

    private TextView tvThreeLable;
    private TextView tvThree;
    private TextView tvFourLable;
    private TextView tvFour;
    private TextView tvFiveLable;
    private TextView tvFive;
    private TextView tvSixLable;
    private TextView tvSix;

    private NotificationHelper notificationHelper;

    public static final String NOT_SHOW = "不显示";

    //视频地址为空
    private boolean videoUrlNull = false;

    /**
     * 是否有评论
     * 1.有评论则显示评论
     * 2.否则显示图片
     */
    private boolean isComment = false;

    /**
     * 评论的适配器
     */
    private int pageNum;
    private CommentAdapter commentAdapter;
    private static final int PAGE_SIZE = 10;

    private View emptyView;
    private View errorView;

    private LinearLayout llComment;
    private RelativeLayout rlComment;

    private TextView tvCommentNumber;
    private TextView tvSmallCommentNumber;
    private ImageView ivShare;
    private ImageView ivCollect;
    private FrameLayout flComment;
    private TextView etComment;

    private LoginBean.AccountBean accountBean;

    //收藏状态
    private int collectState;
    //收藏id
    private int collectionId;
    //评论数
    private int commentNum;

    public static final String NEWS_ID = "NEWS_ID";
    private int newsId;

    public static void startActivity(Context context, ContentDetailsBean contentDetailsBean) {
        Intent intent = new Intent(context, ContentDetailsActivity.class);
        intent.putExtra(CONTENT_BEAN, contentDetailsBean);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, int newsId) {
        Intent intent = new Intent(context, ContentDetailsActivity.class);
        intent.putExtra(NEWS_ID, newsId);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        rlComment = (RelativeLayout) findViewById(R.id.rl_comment);

        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        rlTopTitle = (RelativeLayout) findViewById(R.id.rl_top_title);

        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);

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

        emptyView.findViewById(R.id.tv_retry).setVisibility(View.GONE);
        errorView.findViewById(R.id.tv_retry).setVisibility(View.GONE);

        headView = LayoutInflater.from(this).inflate(R.layout.head_content, (ViewGroup) rvList.getParent(), false);

        llComment = (LinearLayout) headView.findViewById(R.id.ll_comment);
        tvCommentNumber = (TextView) headView.findViewById(R.id.tv_comment_number);

        webView = (WebView) headView.findViewById(R.id.webview);
        tvTitle = (TextView) headView.findViewById(R.id.tv_title);
        tvTime = (TextView) headView.findViewById(R.id.tv_time);
        tvWebsite = (TextView) headView.findViewById(R.id.tv_website);

        llAddress = (LinearLayout) headView.findViewById(R.id.ll_address);

        tvAddress = (TextView) headView.findViewById(R.id.tv_address);
        tvAddressLable = (TextView) headView.findViewById(R.id.tv_address_lable);

        tvPersonnelLable = (TextView) headView.findViewById(R.id.tv_personnel_lable);
        tvPersonnel = (TextView) headView.findViewById(R.id.tv_personnel);

        tvThreeLable = (TextView) headView.findViewById(R.id.tv_three_lable);
        tvThree = (TextView) headView.findViewById(R.id.tv_three);

        tvFourLable = (TextView) headView.findViewById(R.id.tv_four_lable);
        tvFour = (TextView) headView.findViewById(R.id.tv_four);

        tvFiveLable = (TextView) headView.findViewById(R.id.tv_five_lable);
        tvFive = (TextView) headView.findViewById(R.id.tv_five);

        tvSixLable = (TextView) headView.findViewById(R.id.tv_six_lable);
        tvSix = (TextView) headView.findViewById(R.id.tv_six);

        mVideoView = (StandardGSYVideoPlayer) findViewById(R.id.videoView);

        etComment = (TextView) findViewById(R.id.et_comment);
        ivShare = (ImageView) findViewById(R.id.iv_share);
        ivCollect = (ImageView) findViewById(R.id.iv_collect);
        flComment = (FrameLayout) findViewById(R.id.fl_comment);

        tvSmallCommentNumber = (TextView) findViewById(R.id.tv_small_comment_number);
    }

    @Override
    protected void initData() {
        weakReference = new WeakReference<ContentDetailsActivity>(this);

        FileDownloader.setup(weakReference.get());

        notificationHelper = new NotificationHelper(weakReference.get());

        tvTopTitle.setText("详情");

        contentDetailsBean = (ContentDetailsBean) getIntent().getSerializableExtra(CONTENT_BEAN);

        Log.i("ContentDetailsActivity", "ContentDetailsActivity-->" + contentDetailsBean.videoUrl);

        newsId = getIntent().getIntExtra(NEWS_ID, -1);

        if (-1 == newsId) {
            newsId = contentDetailsBean.id;
        }

        if (null == contentDetailsBean) {
            //通过id查询新闻状态
            queryNewsById(newsId);
        } else {
            setNewsContent(contentDetailsBean);
        }
    }

    private void setNewsContent(ContentDetailsBean contentDetailsBean) {
        //是否有评论
        isComment = contentDetailsBean.isComment;

        //是否有pdf
        if (!TextUtils.isEmpty(contentDetailsBean.pdfUrl)) {
            ivRightOperate.setVisibility(View.VISIBLE);
            ivRightOperate.setImageResource(R.mipmap.ic_download);
        }

        /**
         * 有评论
         */
        if (isComment) {
            if (isLogin()) {
                accountBean = PreferenceUtil.getUserInfo(this);
            }
            initLinearRecycler();
            getCommentList(1);
            llComment.setVisibility(View.VISIBLE);
            rlComment.setVisibility(View.VISIBLE);

            //通过id查询新闻状态
            queryNewsById(contentDetailsBean.id);
        } else {
            getImageList(contentDetailsBean.imageUrls, false);

            initGridRecycler();

            llComment.setVisibility(View.GONE);
            rlComment.setVisibility(View.GONE);
        }

        initWebView();

        videoUrlNull = TextUtils.isEmpty(contentDetailsBean.videoUrl);

        //有视频
        if (!videoUrlNull) {
            initVideo();
            mVideoView.setVisibility(View.VISIBLE);
            rlTopTitle.setVisibility(View.GONE);
        } else {
            rlTopTitle.setVisibility(View.VISIBLE);
            mVideoView.setVisibility(View.GONE);
            ImmersionBar.with(weakReference.get())
                    .fitsSystemWindows(true)
                    .statusBarColor(R.color.color_E64A3A)
                    .init();
            //设置沉浸式
//            ImmersionBar.setTitleBar(this, rlTopTitle) ;
//            StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);
        }

        tvTitle.setText(contentDetailsBean.title);
        tvTime.setText(DateUtil.longToString(contentDetailsBean.time, DateUtil.FORMAT_DATE));
        tvWebsite.setText(TextUtils.isEmpty(contentDetailsBean.unitName) ? "辛集市" : contentDetailsBean.unitName);

        //设置附带的内容
        setMarkeText();

        //设置webview内容
        setWebContent();
    }

    /**
     * 设置附带的内容
     */
    private void setMarkeText() {
        if (NOT_SHOW.equals(contentDetailsBean.oneLable)) {
            llAddress.setVisibility(View.GONE);
        } else {
            llAddress.setVisibility(View.VISIBLE);

            if (TextUtils.isEmpty(contentDetailsBean.twoLable)) {
                tvAddressLable.setVisibility(View.GONE);
                tvAddress.setVisibility(View.GONE);
            } else {
                tvAddressLable.setVisibility(View.VISIBLE);
                tvAddress.setVisibility(View.VISIBLE);

                tvAddressLable.setText(contentDetailsBean.twoLable);
                tvAddress.setText(contentDetailsBean.two);
            }
            tvPersonnelLable.setText(TextUtils.isEmpty(contentDetailsBean.oneLable) ? "备注一" : contentDetailsBean.oneLable);
            tvPersonnel.setText(TextUtils.isEmpty(contentDetailsBean.one) ? "暂无" : contentDetailsBean.one);
        }

        if (TextUtils.isEmpty(contentDetailsBean.threeLable)) {
            tvThreeLable.setVisibility(View.GONE);
            tvThree.setVisibility(View.GONE);
            tvFourLable.setVisibility(View.GONE);
            tvFour.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(contentDetailsBean.fourLable)) {
            tvThreeLable.setText(contentDetailsBean.threeLable);
            tvThree.setText(contentDetailsBean.three);
            tvFourLable.setVisibility(View.GONE);
            tvFour.setVisibility(View.GONE);
        } else {
            tvThreeLable.setVisibility(View.VISIBLE);
            tvThree.setVisibility(View.VISIBLE);
            tvFourLable.setVisibility(View.VISIBLE);
            tvFour.setVisibility(View.VISIBLE);

            tvThreeLable.setText(contentDetailsBean.threeLable);
            tvThree.setText(TextUtils.isEmpty(contentDetailsBean.three) ? "暂无" : contentDetailsBean.three);
            tvFourLable.setText(contentDetailsBean.fourLable);
            tvFour.setText(TextUtils.isEmpty(contentDetailsBean.four) ? "暂无" : contentDetailsBean.four);
        }

        if (TextUtils.isEmpty(contentDetailsBean.fiveLable)) {
            tvFiveLable.setVisibility(View.GONE);
            tvFive.setVisibility(View.GONE);
        } else {
            tvFiveLable.setVisibility(View.VISIBLE);
            tvFive.setVisibility(View.VISIBLE);

            tvFiveLable.setText(contentDetailsBean.fiveLable);
            tvFive.setText(contentDetailsBean.five);
        }

        if (TextUtils.isEmpty(contentDetailsBean.sixLable)) {
            tvSixLable.setVisibility(View.GONE);
            tvSix.setVisibility(View.GONE);
        } else {
            tvSixLable.setVisibility(View.VISIBLE);
            tvSix.setVisibility(View.VISIBLE);

            tvSixLable.setText(contentDetailsBean.sixLable);
            tvSix.setText(contentDetailsBean.six);
        }
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        // 开启 DOM storage API 功能
        webView.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        webView.getSettings().setDatabaseEnabled(true);
        //开启 Application Caches 功能
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setLoadsImagesAutomatically(true); //自动加载图片
        webView.getSettings().setPluginState(WebSettings.PluginState.OFF);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        webView.addJavascriptInterface(new JavascriptInterface(this), "imagelistner");
        webView.setWebViewClient(new MyWebViewClient());
    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，
        //函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                " var array=new Array(); " +
                " for(var j=0;j<objs.length;j++){ array[j]=objs[j].src; }" +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src,array);  " +
                "    }  " +
                "}" +
                "})()");
    }

    private List<String> imageList;

    public class JavascriptInterface {
        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img, String[] imgs) {
            if (null == imageList || imageList.size() <= 0) {
                imageList = new ArrayList<>(Arrays.asList(imgs));
            }
            getImageList(imageList, true);

            int currentIndex = imageList.indexOf(img);

            PictureSelector.create(ContentDetailsActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(currentIndex, webImages);
        }
    }

    /**
     * 获取网络图片
     *
     * @param imageUrls
     * @param isWeb     是否是获取网络图片
     */
    private void getImageList(List<String> imageUrls, boolean isWeb) {
        if (isWeb) {
            webImages = new ArrayList<>();
        } else {
            selectListImage = new ArrayList<>();
        }
        LocalMedia localMedia = null;
        for (String url : imageUrls) {
            if (!TextUtils.isEmpty(url)) {
                localMedia = new LocalMedia();
                localMedia.setPath(url);
                if (isWeb) {
                    webImages.add(localMedia);
                } else {
                    selectListImage.add(localMedia);
                }
            }
        }
    }

    /**
     * 初始化recycler
     */
    private void initLinearRecycler() {
        commentAdapter = new CommentAdapter(R.layout.item_comment, new ArrayList<>());
        //添加头部
        commentAdapter.addHeaderView(headView);
        //默认情况下无数据时只显示emptyview，调用下面两个方法，无数据也可以显示头部
        commentAdapter.setHeaderAndEmpty(true);
        commentAdapter.setHeaderFooterEmpty(true, true);

        commentAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_del) {
                    delCommentRequest(position);
                }
            }
        });

        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(commentAdapter);
    }

    /**
     * 初始化recycler
     */
    private void initGridRecycler() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvList.setLayoutManager(manager);
        selectImageAdapter = new ShowImageAdapter(R.layout.gv_filter_image, selectListImage);

        //添加头部
        selectImageAdapter.addHeaderView(headView);
        //默认情况下无数据时只显示emptyview，调用下面两个方法，无数据也可以显示头部
        selectImageAdapter.setHeaderAndEmpty(true);
        selectImageAdapter.setHeaderFooterEmpty(true, true);

        selectImageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (selectImageAdapter.getData().size() > 0) {
                    LocalMedia media = (LocalMedia) selectImageAdapter.getItem(position);
                    String pictureType = media.getPictureType();

                    int mediaType = PictureMimeType.pictureToVideo(pictureType);

                    //判断类型
                    switch (mediaType) {
                        case PictureConfig.TYPE_IMAGE:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).themeStyle(themeId).externalPicturePreview(position, "/custom_file", selectList);
                            PictureSelector.create(ContentDetailsActivity.this).themeStyle(R.style.picture_default_style).openExternalPreview(position, selectListImage);
                            break;

                        case PictureConfig.TYPE_VIDEO:
                            // 预览视频
                            PictureSelector.create(ContentDetailsActivity.this).externalPictureVideo(media.getPath());
                            break;

                        case 3:
                            // 预览音频
                            PictureSelector.create(ContentDetailsActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        rvList.setAdapter(selectImageAdapter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_content_details;
    }

    @Override
    protected void setListener() {
        ivBackOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivRightOperate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(ContentDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        PermissionGen.with(ContentDetailsActivity.this)
                                .addRequestCode(1)
                                .permissions(
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                ).request();
                    } else {
                        downPdf(contentDetailsBean.pdfUrl, contentDetailsBean.pdfName);
                    }
                } else {
                    downPdf(contentDetailsBean.pdfUrl, contentDetailsBean.pdfName);
                }
            }
        });
        etComment.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivCollect.setOnClickListener(this);
        flComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rvList.smoothScrollToPosition(1);
            }
        });

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getCommentList(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                getCommentList(pageNum + 1);
            }
        });

        //如果没有评论则不准刷新
        if (!isComment) {
            refreshLayout.setEnableRefresh(false);
        }

        refreshLayout.setEnableLoadMore(false);
    }

    @Override
    public void onClick(View v) {
        if (!isLogin()) {
            ToastHelper.showShort("请登录后操作");
            LoginActivity.startActivity(this);
            return;
        }

        switch (v.getId()) {
            case R.id.iv_share:
                showShareDialog();
                break;

            case R.id.iv_collect:
                //未收藏
                if (0 == collectState) {
                    addCollectiontRequest();
                } else {
                    delCollectiontRequest();
                }
                break;

            //评论
            case R.id.et_comment:
                showCommentDialog();
                break;

            default:
                break;
        }
    }

    private void setWebContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(getHtmlData(contentDetailsBean.content));
        webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    /**
     * 设置黑色字体
     */
    private void setBlackFont(boolean fits) {
        ImmersionBar.with(weakReference.get())
                .transparentStatusBar()  //透明状态栏，不写默认透明色
//                .transparentNavigationBar()  //透明导航栏，不写默认黑色(设置此方法，fullScreen()方法自动为true)
//                .transparentBar()             //透明状态栏和导航栏，不写默认状态栏为透明色，导航栏为黑色（设置此方法，fullScreen()方法自动为true）
//                .statusBarColor(R.color.color_E42417)     //状态栏颜色，不写默认透明色
//                .navigationBarColor(R.color.colorPrimary) //导航栏颜色，不写默认黑色
//                .barColor(R.color.colorPrimary)  //同时自定义状态栏和导航栏颜色，不写默认状态栏为透明色，导航栏为黑色
                .fitsSystemWindows(fits)
                .statusBarAlpha(0.3f)  //状态栏透明度，不写默认0.0f
                .navigationBarAlpha(0.4f)  //导航栏透明度，不写默认0.0F
                .barAlpha(0.3f)  //状态栏和导航栏透明度，不写默认0.0f
                .statusBarDarkFont(true)   //状态栏字体是深色，不写默认为亮色
                .navigationBarDarkIcon(true) //导航栏图标是深色，不写默认为亮色
                .autoDarkModeEnable(true) //自动状态栏字体和导航栏图标变色，必须指定状态栏颜色和导航栏颜色才可以自动变色哦
                .autoStatusBarDarkModeEnable(true, 0.2f) //自动状态栏字体变色，必须指定状态栏颜色才可以自动变色哦
                .autoNavigationBarDarkModeEnable(true, 0.2f) //自动导航栏图标变色，必须指定导航栏颜色才可以自动变色哦
                .flymeOSStatusBarFontColor(R.color.black)  //修改flyme OS状态栏字体颜色
//                .reset()  //重置所以沉浸式参数
                .init();  //必须调用方可应用以上所配置的参数
    }

    @Override
    public void onResume() {
        super.onResume();

        //地址不为空
        if (!videoUrlNull) {
            setBlackFont(true);
            getCurPlay().onVideoResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!videoUrlNull) {
            getCurPlay().onVideoPause();
        }
    }

    @Override
    protected void onDestroy() {
        AllenVersionChecker.getInstance().cancelAllMission(this);
        super.onDestroy();

        if (!videoUrlNull) {
            getCurPlay().release();
            orientationUtils.releaseListener();
        }

        //如果是非收藏状态
        if (0 == collectState) {
            EventBus.getDefault().post(new EventMessage(Messages.UPDATE_NEWS, ""));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (isPlay && !isPause) {
            mVideoView.onConfigurationChanged(this, newConfig, orientationUtils);
        }
        if (!videoUrlNull) {
            //如果是横屏
            if (Configuration.ORIENTATION_LANDSCAPE == newConfig.orientation) {
                setBlackFont(false);
            } else {
                setBlackFont(true);
            }
        } else {
            //设置沉浸式
//            ImmersionBar.setTitleBar(this, rlTopTitle);
        }
    }

    /**
     * 下载文件
     *
     * @param url
     */
    private void downPdf(String url, String fileName) {
        String filePath = Environment.getExternalStorageDirectory() + "/xinji/pdf/" + fileName;
        FileDownloader.getImpl()
                .create(url)
                .setPath(filePath)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i("FileDownloader", "pending--》总大小:" + totalBytes + ",当前大小" + soFarBytes);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        ToastHelper.showShort("开始下载");
                        Log.i("FileDownloader", "connected--》总大小:" + totalBytes + ",当前大小" + soFarBytes);

                        notificationHelper.showNotification(R.mipmap.ic_launcher, "开始下载", "", new Intent());
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                        double progress = ((double) soFarBytes / totalBytes) * 100;

                        Log.i("FileDownloader", "progress--》" + progress);

                        notificationHelper.updateNotification((int) progress);
                    }

                    @Override
                    protected void blockComplete(BaseDownloadTask task) {
                        Log.i("FileDownloader", "blockComplete");
                    }

                    @Override
                    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
                        Log.i("FileDownloader", "retry");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        ToastHelper.showLong("下载完成" + filePath);
                        notificationHelper.showNotification(R.mipmap.ic_launcher, "开始下载", "下载完成", new Intent());

                        openFile(filePath);
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i("FileDownloader", "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.i("FileDownloader", "error");
                        notificationHelper.onDestroy();
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.i("FileDownloader", "warn");
                        ToastHelper.showShort("下载出错");
                        notificationHelper.onDestroy();
                    }
                }).
                start();
    }

    /**
     * 打开文件
     *
     * @param filePath
     */
    private void openFile(String filePath) {
        Intent intent = new Intent();
        // 这是比较流氓的方法，绕过7.0的文件权限检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        File file = new File(filePath);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//设置标记
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);//动作，查看
//                        intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));//设置类型
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");//设置类型
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 得到存储权限
     */
    @PermissionSuccess(requestCode = 1)
    public void getPermission() {
        downPdf(contentDetailsBean.pdfUrl, contentDetailsBean.pdfName);
    }

    /**
     * 没得到拨号权限
     */
    @PermissionFail(requestCode = 1)
    public void take() {
        ToastHelper.showShort("下载文件需要获取权限");
    }

    private StandardGSYVideoPlayer mVideoView;
    private OrientationUtils orientationUtils;

    /**
     * 是否播放
     */
    private boolean isPlay = false;

    /**
     * 是否暂停
     */
    private boolean isPause = false;

    /**
     * 初始化videoview的配置
     */
    private void initVideo() {
        //设置旋转
        orientationUtils = new OrientationUtils(this, mVideoView);
        //是否开启自动旋转
        mVideoView.setRotateViewAuto(false);

        //是否可以滑动调整
        mVideoView.setIsTouchWiget(true);

        //添加封面
        //mVideoView.setThumbImageView();

        mVideoView.setVideoAllCallBack(new VideoListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                isPlay = true;
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onEnterFullscreen(String url, Object... objects) {
                super.onEnterFullscreen(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                Logger.d("***** onQuitFullscreen **** ");
                //列表返回的样式判断
                orientationUtils.backToProtVideo();
            }

            @Override
            public void onPlayError(String url, Object... objects) {
                super.onPlayError(url, objects);
                ToastHelper.showShort("播放失败");
            }
        });

        //设置返回按键功能
        mVideoView.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //设置全屏按键功能
        mVideoView.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横盘
                orientationUtils.resolveByClick();
                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                mVideoView.startWindowFullscreen(ContentDetailsActivity.this, true, true);
            }
        });

        //锁屏事件
        mVideoView.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                //配合下方的onConfigurationChanged
                orientationUtils.setEnable(!lock);
            }
        });

        startPlay(contentDetailsBean.videoUrl);
    }

    private void startPlay(String videoUrl) {
        mVideoView.setUp(videoUrl, false, "");
        //开始自动播放
        mVideoView.startPlayLogic();
    }

    private GSYVideoPlayer getCurPlay() {
        if (mVideoView.getFullWindowPlayer() != null) {
            return mVideoView.getFullWindowPlayer();
        } else {
            return mVideoView;
        }
    }

    @Override
    public void onBackPressed() {
        if (!videoUrlNull) {
            orientationUtils.backToProtVideo();
            if (GSYVideoManager.backFromWindowFull(this)) {
                return;
            }
            //释放所有
            mVideoView.setVideoAllCallBack(null);
            getCurPlay().release();
        }
        finish();
    }

    /**
     * 是否登录
     *
     * @return
     */
    private boolean isLogin() {
        return PreferenceUtil.getBoolean(Constant.AUTO_LOGIN, false);
    }

    /**
     * 获取评论列表
     */
    private void getCommentList(int pageNum) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .getCommentList(contentDetailsBean.id, pageNum, PAGE_SIZE)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData<CommentBean>>() {
                    @Override
                    public void onSuccess(BaseData<CommentBean> baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            List<CommentBean> newComments = baseData.dataList;

                            if (null != newComments && newComments.size() > 0) {
                                if (pageNum == 1) {
                                    setPageNum(1);
                                } else {
                                    setPageNum(pageNum);
                                }
                                initListBeans(newComments, commentAdapter);
                            } else {
                                if (1 == pageNum) {
                                    commentAdapter.setEmptyView(emptyView);
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                } else {
                                    refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                                }
                                refreshLayout.setEnableLoadMore(false);
                            }
                            finishRefresh(pageNum, true);
                        } else {
                            commentAdapter.setEmptyView(errorView);
                            finishRefresh(pageNum, false);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        commentAdapter.setEmptyView(errorView);
                        finishRefresh(pageNum, false);
                    }
                });
    }

    /**
     * 设置数据
     */
    private void initListBeans(List newsList, BaseQuickAdapter adapter) {
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

    /**
     * 关闭动画
     *
     * @param pageNum
     */
    private void finishRefresh(int pageNum, boolean success) {
        if (1 == pageNum) {
            refreshLayout.finishRefresh(success);
            refreshLayout.setEnableFooterFollowWhenNoMoreData(false);
        } else {
            refreshLayout.finishLoadMore(success);
        }

        if (1 == pageNum && !success) {
            refreshLayout.setEnableLoadMore(false);
        }
    }

    /**
     * 设置评论数
     *
     * @param commentNum
     */
    private void setCommentNumber(String commentNum) {
        tvCommentNumber.setText(commentNum);
        if (Integer.valueOf(commentNum) > 0) {
            tvSmallCommentNumber.setVisibility(View.VISIBLE);
            tvSmallCommentNumber.setText(commentNum);
        } else {
            tvSmallCommentNumber.setVisibility(View.GONE);
        }

        //设置新的评论数量
//        contentDetailsBean.commentNum = String.valueOf(commentAdapter.getItemCount());
    }

    private CustomInputDialog commentInputDialog;

    private void showCommentDialog() {
        if (null == commentInputDialog) {
            commentInputDialog = new CustomInputDialog(this, R.style.inputDialogStyle, "请输入评论", 500);
            commentInputDialog.setClickListener(new CustomInputDialog.onSendClickListener() {
                @Override
                public void sendComment(String comment) {
                    addCommentRequest(comment);
                }
            });
        }
        commentInputDialog.show();
    }

    /**
     * 发送评论
     *
     * @param content
     */
    private void addCommentRequest(String content) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addComment(contentDetailsBean.id, StringUtils.encoder(content))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            CommentBean commentBean = new CommentBean();
                            commentBean.creator = Integer.valueOf(accountBean.id);
                            commentBean.context = content;
                            commentBean.createDate = System.currentTimeMillis();
                            commentBean.id = Integer.valueOf(baseData.id);
                            commentBean.createName = accountBean.name;
                            commentBean.url = accountBean.url;

                            commentAdapter.addData(0, commentBean);
                            rvList.smoothScrollToPosition(1);

                            commentNum++;
                            setCommentNumber(String.valueOf(commentNum));
                            commentInputDialog.clearContent();
                            commentInputDialog.dismiss();
                            ToastHelper.showShort("评论成功");

                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("评论失败");
                    }
                });
    }

    /**
     * 删除评论
     */
    private void delCommentRequest(int position) {
        CommentBean commentBean = commentAdapter.getItem(position);

        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .delComment(commentBean.id)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            commentAdapter.remove(position);
                            commentNum--;
                            setCommentNumber(String.valueOf(commentNum));
                            //如果长度为0则设置空布局
                            if (0 == commentAdapter.getData().size()) {
                                commentAdapter.setEmptyView(emptyView);
                                rvList.smoothScrollToPosition(1);
                            } else {
                                rvList.smoothScrollToPosition(commentAdapter.getItemCount());
                            }
                            ToastHelper.showShort("删除评论成功");
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("删除评论失败");
                    }
                });
    }

    /**
     * 收藏
     */
    private void addCollectiontRequest() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .addCollectiont(contentDetailsBean.id)
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            ivCollect.setImageResource(R.mipmap.icon_collect_true);
                            collectState = 1;
                            collectionId = Integer.valueOf(baseData.id);
                            ToastHelper.showShort("收藏成功");
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("收藏失败");
                    }
                });
    }

    /**
     * 取消收藏
     */
    private void delCollectiontRequest() {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .delCollectiont(String.valueOf(collectionId))
//                .retryWhen(new RetryWithDelay(3, 300))
                .compose(new CustomCompose())
                .compose(bindToLifecycle())
                .subscribe(new RxSubscriber<BaseData>() {
                    @Override
                    public void onSuccess(BaseData baseData) {
                        if (BaseData.SUCCESS.equals(baseData.result)) {
                            collectState = 0;
                            ivCollect.setImageResource(R.mipmap.icon_collect_false);
                            ToastHelper.showShort("取消收藏成功");
                        } else {
                            ToastHelper.showShort(baseData.message);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("取消收藏失败");
                    }
                });
    }


    private ShareSelectDialog shareDialog;

    /**
     * 初始化分享对话框
     */
    private void initShareDialog() {
        shareDialog = new ShareSelectDialog(this, R.style.inputDialogStyle);
        shareDialog.setItemClickListener(new ShareSelectDialog.onItemClickListener() {
            @Override
            public void selectClick(int type) {
                switch (type) {
                    case ShareSelectDialog.TYPE_PICTURE:
                        sendWx(SendMessageToWX.Req.WXSceneSession);
                        if (null != shareDialog) {
                            shareDialog.dismiss();
                        }
                        break;

                    case ShareSelectDialog.TYPE_ALBUM:
                        sendWx(SendMessageToWX.Req.WXSceneTimeline);
                        if (null != shareDialog) {
                            shareDialog.dismiss();
                        }
                        break;
                }
            }
        });
    }

    /**
     * 显示选择对话框
     */
    private void showShareDialog() {
        if (null == shareDialog) {
            initShareDialog();
        }
        shareDialog.show();
    }

    private IWXAPI api;

    private static final int THUMB_SIZE = 150;

    /**
     * 分享
     * type为1:分享给好友，type为2:分享给朋友圈
     *
     * @param type
     */
    private void sendWx(int type) {
        if (!isWeixinAvilible(this)) {
            ToastHelper.showShort("当前没有安装微信");
            return;
        }
        api = WXAPIFactory.createWXAPI(this, Constant.weixinAppId, false);

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = "http://61.182.50.12:9090/xjdj/webApp/shareDetails.html?newsId=" + shareId;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = contentDetailsBean.title;
        msg.description = contentDetailsBean.title;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WeChatUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = type;
        boolean b = api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    //判断是否安装了微信
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    //分享的id
    private int shareId;

    /**
     * 通过id查询详情
     *
     * @param newsId
     */
    private void queryNewsById(int newsId) {
        HttpUtils.getInstance(this)
                .getRetofitClinet()
                .setBaseUrl(Urls.BASE_URL)
                .builder(StudyApi.class)
                .queryNewById(newsId)
                .compose(this.bindToLifecycle())
                .compose(new CustomCompose())
                .subscribe(new RxSubscriber<StudyNewsBean>() {
                    @Override
                    public void onSuccess(StudyNewsBean newsBean) {
                        if (!"error".equals(newsBean.result)) {
                            shareId = newsBean.data.id;
                            collectionId = newsBean.data.collectionId;
                            collectState = newsBean.data.collectState;
                            //设置评论数
                            commentNum = Integer.valueOf(newsBean.data.commentNum);
                            setCommentNumber(newsBean.data.commentNum);
                            ivCollect.setImageResource(newsBean.data.collectState == 0 ? R.mipmap.icon_collect_false : R.mipmap.icon_collect_true);
                        }
                    }

                    @Override
                    protected void onError(ResponeThrowable responeThrowable) {
                        ToastHelper.showShort("请求异常");
                    }
                });
    }


}
