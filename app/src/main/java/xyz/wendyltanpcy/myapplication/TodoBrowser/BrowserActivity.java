package xyz.wendyltanpcy.myapplication.TodoBrowser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import xyz.wendyltanpcy.myapplication.MainActivity;
import xyz.wendyltanpcy.myapplication.R;

public class BrowserActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView mWebView;
    private ImageView mBack, mHome, mNext, mRefresh,mGoTo;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private ListView mListView;
    private ArrayAdapter mAdapter;
    private DrawerLayout mDrawerLayout;
    private String [] data = {"www.360.com","study.jnu.edu.cn","www.google.com","www.jikexueyuan.com",
            "wendyltanpcy.xyz","Python","PHP","JavaScript"};
    private String homeUrl = "www.baidu.com";

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
        mRefresh = (ImageView) findViewById(R.id.ib_refresh);     //刷新按钮
        mSearchView = (SearchView) findViewById(R.id.et_url);   //网址输入框
//        mListView = (ListView) findViewById(R.id.search_list);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_load);    //网页加载进度条

        /*设置监听事件*/
        mBack.setOnClickListener(this);
        mHome.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mGoTo.setOnClickListener(this);
        mRefresh.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
//
//        mAdapter = new ArrayAdapter(BrowserActivity.this, android.R.layout.simple_list_item_1, data);
//        mListView.setAdapter(mAdapter);
//        mListView.setTextFilterEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_broswer);

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

//        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (!TextUtils.isEmpty(newText)){
//                    mAdapter.getFilter().filter(newText);
//                }else{
//                    mListView.clearTextFilter();
//                }
//                return false;
//            }
//        });

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
            case R.id.ib_next:
                mWebView.goForward();
                break;
            case R.id.ib_refresh:
                mWebView.reload();      //重新加载
                break;
            case R.id.et_url:
                mSearchView.setQuery("",true);  //输入网站
                mSearchView.setQueryHint("请输入网址");
                break;
            default:
                break;
        }
    }
}
