package xyz.wendyltanpcy.easytodo.FinishList;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.easytodo.Adapter.FinishEventsAdapter;
import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.TodoList.MainActivity;
import xyz.wendyltanpcy.easytodo.TodoList.SettingsActivity;
import xyz.wendyltanpcy.easytodo.model.FinishEvent;

/**
 * Created by Wendy on 2017/9/28.
 */

public class FinishEventListActivity extends AppCompatActivity {

    private FinishEventsAdapter MyAdapter;
    private List<FinishEvent> finishEventList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private static boolean haveInit = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finish_event_list_activity);
        baseInit();

        showNoEvent();

        RecyclerView eventNameRecyclerView = (RecyclerView) findViewById(R.id.event_name_recycler_view_finish);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new FinishEventsAdapter(finishEventList);
        eventNameRecyclerView.setLayoutManager(layoutManager);
        eventNameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventNameRecyclerView.setAdapter(MyAdapter);


         /*
        设置刷新
         */
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                doRefresh();
                            }
                        });
                    }
                }).start();

            }
        });

    }

    private void showNoEvent(){
        if (finishEventList.isEmpty()){
            View visablity = findViewById(R.id.no_event_layout);
            visablity.setVisibility(View.VISIBLE);
            visablity = findViewById(R.id.event_name_recycler_view_finish);
            visablity.setVisibility(View.GONE);
        }else{
            View visablity = findViewById(R.id.no_event_layout);
            visablity.setVisibility(View.GONE);
            visablity = findViewById(R.id.event_name_recycler_view_finish);
            visablity.setVisibility(View.VISIBLE);
        }
    }

    private void doRefresh(){
        finishEventList = MyAdapter.getFinishEventsList();
        showNoEvent();
        MyAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
        Toast.makeText(FinishEventListActivity.this,"数据刷新成功",Toast.LENGTH_SHORT).show();
    }

    private  void initEvents(){
        LitePal.getDatabase();
    }

    private void baseInit(){

        finishEventList = DataSupport.findAll(FinishEvent.class);

        if(!haveInit){
            initEvents();

            showNoEvent();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.finsh_toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_finish);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_task:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.nav_finish:
                        startActivity(new Intent(getApplicationContext(), FinishEventListActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    //隐藏浏览器入口，浏览器只作为测试版本用
//                    case R.id.nav_broswer:
//                        startActivity(new Intent(getApplicationContext(), BrowserActivity.class));
//                        mDrawerLayout.closeDrawer(Gravity.START);
//                        break;
                    case R.id.nav_setting:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_finish, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.delete){
            finishEventList.clear();
            DataSupport.deleteAll(FinishEvent.class);
            MyAdapter.notifyDataSetChanged();
            showNoEvent();
            Toast.makeText(this,"删除成功！",Toast.LENGTH_SHORT).show();
        }else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }
}
