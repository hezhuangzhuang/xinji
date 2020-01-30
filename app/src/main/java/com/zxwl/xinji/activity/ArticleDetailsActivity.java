package com.zxwl.xinji.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.zxwl.commonlibrary.utils.DateUtil;
import com.zxwl.commonlibrary.utils.ToastHelper;
import com.zxwl.network.bean.response.NewsBean;
import com.zxwl.xinji.R;
import com.zxwl.xinji.base.BaseActivity;

/**
 * 文章详情
 */
public class ArticleDetailsActivity extends BaseActivity {
    private ImageView ivBackOperate;
    private TextView tvTopTitle;
    private TextView tvTitle;
    private TextView tvTime;
    private TextView tvWebsite;
    private WebView webview;

    public static final String NEW_BEAN = "NEW_BEAN";

    private NewsBean newsBean;

    public static void startActivity(Context context, NewsBean newsBean) {
        Intent intent = new Intent(context, ArticleDetailsActivity.class);
        intent.putExtra(NEW_BEAN, newsBean);
        context.startActivity(intent);
    }

    @Override
    protected void findViews() {
        ivBackOperate = (ImageView) findViewById(R.id.iv_back_operate);
        tvTopTitle = (TextView) findViewById(R.id.tv_top_title);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvWebsite = (TextView) findViewById(R.id.tv_website);
        webview = (WebView) findViewById(R.id.webview);
    }

    @Override
    protected void initData() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_E64A3A), 0);

        tvTopTitle.setText("详情");

        newsBean = (NewsBean) getIntent().getSerializableExtra(NEW_BEAN);

        tvTitle.setText(newsBean.title);
        tvWebsite.setText(newsBean.announcer);
        tvTime.setText(DateUtil.longToString(newsBean.createDate, DateUtil.FORMAT_DATE));

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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_article_details;
    }

    /**
     * 下载pdf
     * @param url
     */
    private void downPdf(String url) {
        FileDownloader.getImpl()
                .create(url)
                .setPath(Environment.getExternalStorageDirectory() + "/xinji/pdf/"+"psf.pdf")
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i("FileDownloader", "pending--》总大小:" + totalBytes + ",当前大小" + soFarBytes);
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        Log.i("FileDownloader", "connected--》总大小:" + totalBytes + ",当前大小" + soFarBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i("FileDownloader", "progress--》总大小:" + totalBytes + ",当前大小" + soFarBytes);
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
                        ToastHelper.showShort("下载完成");
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Log.i("FileDownloader", "paused");
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        Log.i("FileDownloader", "error");
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        Log.i("FileDownloader", "warn");

                    }
                }).start();
    }

    /**
     * 设置webview的内容
     */
    private void setWebContent() {
        StringBuilder sb = new StringBuilder();
        //TODO:设置传递过来的参数
        sb.append(getHtmlData(newsBean.context));
        webview.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);
    }

    /**
     * 给显示的内容设置默认样式
     * @param bodyHTML
     * @return
     */
    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }
}
