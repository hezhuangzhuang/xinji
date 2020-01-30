package com.zxwl.xinji.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zxwl.commonlibrary.utils.NotificationHelper;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.bean.response.DocumentBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

import java.io.File;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * 文件详情
 */
public class DocumentDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private ImageView ivRightOperate;

    private TextView tvTitle;
    private WebView webView;

    public static final String TITLE = "TITLE";
    public static final String CONTENT = "CONTENT";

    public static final String DOCUMENT_BEAN = "DOCUMENT_BEAN";

    private String title;
    private String content;

    private DocumentBean contentDetailsBean;

    private NotificationHelper notificationHelper;

    public static void startActivity(Context context, String title, String content) {
        Intent intent = new Intent(context, DocumentDetailsActivity.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(CONTENT, content);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, DocumentBean documentBean) {
        Intent intent = new Intent(context, DocumentDetailsActivity.class);
        intent.putExtra(DOCUMENT_BEAN, documentBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        ivRightOperate = (ImageView) findViewById(R.id.iv_right_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        webView = (WebView) findViewById(R.id.tv_content);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("详情");

        notificationHelper = new NotificationHelper(this);

        contentDetailsBean = (DocumentBean) getIntent().getSerializableExtra(DOCUMENT_BEAN);

        if (!TextUtils.isEmpty(contentDetailsBean.pdfUrl)) {
            ivRightOperate.setVisibility(View.VISIBLE);
            ivRightOperate.setImageResource(R.mipmap.ic_download);
        }

        title = getIntent().getStringExtra(TITLE);
        content = getIntent().getStringExtra(CONTENT);

        tvTitle.setText(contentDetailsBean.title);
        setWebContent();
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
                    if (ActivityCompat.checkSelfPermission(DocumentDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        PermissionGen.with(DocumentDetailsActivity.this)
                                .addRequestCode(1)
                                .permissions(
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                ).request();
                    } else {
                        downPdf(contentDetailsBean.pdfUrl, contentDetailsBean.pdfrealname);
                    }
                } else {
                    downPdf(contentDetailsBean.pdfUrl, contentDetailsBean.pdfrealname);
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_document_details;
    }

    /**
     * 设置webview的内容
     */
    private void setWebContent() {
        StringBuilder sb = new StringBuilder();
        //TODO:设置传递过来的参数
        sb.append(getHtmlData(contentDetailsBean.context));
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
     * 下载文件
     *
     * @param url
     */
    private void downPdf(String url, String fileName) {
        String filePath = Environment.getExternalStorageDirectory() + "/xinji/pdf/" + "test.pdf";
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

                        //打开文件
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
        //intent.setDataAndType(Uri.fromFile(file), getMIMEType(file));//设置类型
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
        downPdf(contentDetailsBean.pdfUrl, contentDetailsBean.pdfrealname);
    }

    /**
     * 没得到拨号权限
     */
    @PermissionFail(requestCode = 1)
    public void take() {
        ToastHelper.showShort("下载文件需要获取权限");
    }

}
