package xyz.wendyltanpcy.myapplication.TodoBrowser;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.WebPageAdapter;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.model.WebPage;

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView mWebView;
    private ImageView mBack, mHome, mNext, mRefresh,mGoTo,mStar;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private DrawerLayout mDrawerLayout;
    private String homeUrl = "www.baidu.com";
    private WebPageAdapter MyAdapter;
    private ImageView mStarOpen;
    private NavigationView navView;
    private boolean isStar = false;
    private List<WebPage> pageInfoList = new ArrayList<>();
    private TextView openText;
    private int isCreated = 1;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_browser);


        mWebView = (WebView) findViewById(R.id.wb_test_web);
        mBack = (ImageView) findViewById(R.id.ib_back);   //返回按钮
        mHome = (ImageView) findViewById(R.id.ib_home);   //主页按钮
        mNext = (ImageView) findViewById(R.id.ib_next);   //前进按钮
        mGoTo = (ImageView) findViewById(R.id.ib_go);   //确认
        mStarOpen = (ImageView) findViewById(R.id.starOpen);
        mRefresh = (ImageView) findViewById(R.id.ib_refresh);     //刷新按钮
        mSearchView = (SearchView) findViewById(R.id.et_url);   //网址输入框
        mProgressBar = (ProgressBar) findViewById(R.id.pb_load);    //网页加载进度条
        mStar = (ImageView) findViewById(R.id.ib_star);
        openText = (TextView) findViewById(R.id.openText);

        /*设置监听事件*/
        mBack.setOnClickListener(this);
        mHome.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mGoTo.setOnClickListener(this);
        mStar.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
        mStarOpen.setOnClickListener(this);

        LitePal.getDatabase();
        pageInfoList = DataSupport.findAll(WebPage.class);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navView = (NavigationView) findViewById(R.id.nav_broswer_view);


        RecyclerView webPageRecyclerView = (RecyclerView) findViewById(R.id.web_page_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new WebPageAdapter(pageInfoList);
        webPageRecyclerView.setLayoutManager(layoutManager);
        webPageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        webPageRecyclerView.setAdapter(MyAdapter);

        /*WebView的基本设置*/
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setInitialScale(25);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                mProgressBar.setProgress(newProgress);  //设置进度
                System.out.println(newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);  //加载完后设置进度条不可见
                }else{
                    mProgressBar.setVisibility(View.VISIBLE);   //设置进度条可见
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                // TODO Auto-generated method stub
                mSearchView.setQueryHint(title);   //网站加载完后，
                super.onReceivedTitle(view, title);
            }
        });



        /*加载主页*/
        loadWeb(homeUrl);
        /**
         * 浏览器搜索框设置响应
         */

        mSearchView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                String url = mSearchView.getQuery().toString();
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (!url.equals("")) {
                        //符合标准
                        loadWeb(url);
                        return true;
                    } else{
                        //不符合标准
                        Toast.makeText(BrowserActivity.this,"请输入有效网址",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BrowserActivity.this, "请输入要访问的网址",
                            Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    /**
     * 加载页面
     * @param url
     */

    public void loadWeb(String url) {
        url = "http://" + url;      //补全url
        mWebView.loadUrl(url);      //加载页面
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_back:
                mWebView.goBack();      //返回上一个页面
                break;
            case R.id.ib_home:
                loadWeb(homeUrl);       //回到主页
                break;
            case R.id.ib_go:
                loadWeb(mSearchView.getQuery().toString());
                break;
            case R.id.ib_star:
                saveUrl(mWebView.getUrl(),mSearchView.getQueryHint().toString());
                break;
            case R.id.ib_next:
                mWebView.goForward();
                break;
            case R.id.ib_refresh:
                if (MyAdapter.getCurrentPosition()!=-1){
                    loadUrlWithStar(pageInfoList.get(MyAdapter.getCurrentPosition()).getUrl());
                }
                else
                    mWebView.reload();      //重新加载
                break;
            case R.id.et_url:
                mSearchView.setQuery("",true);  //输入网站
                mSearchView.setQueryHint("请输入网址");
                break;
            case R.id.starOpen:
                View pageList = navView.findViewById(R.id.page_list_fragment);
                View visibility = pageList.findViewById(R.id.web_page_recycler_view);
                if (visibility.getVisibility()==View.VISIBLE){
                    isStar = false;
                    mStarOpen.setImageResource(R.drawable.ic_star_white_24dp);
                    openText.setText("关");
                    openText.setTextColor(Color.WHITE);
                    visibility.setVisibility(View.GONE);
                }else{
                    isStar = true;
                    mStarOpen.setImageResource(R.drawable.ic_star_black_24dp);
                    openText.setText("开");
                    openText.setTextColor(Color.BLACK);
                    visibility.setVisibility(View.VISIBLE);
                }

                break;
            default:
                break;
        }
    }

    private void saveUrl(String purl,String pageTitle) {
        String url =  purl ;
        WebPage webpage;
        webpage = new WebPage();
        webpage.setPageName(pageTitle);
        webpage.setUrl(url);
        webpage.save();
        pageInfoList.add(webpage);
        Toast.makeText(BrowserActivity.this,"已收藏！",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCreated>1)
            loadUrlWithStar(pageInfoList.get(MyAdapter.getCurrentPosition()).getUrl());
        isCreated = 2;
    }

    private void loadUrlWithStar(String url){
        mWebView.loadUrl(url);
    }
}
