package xyz.wendyltanpcy.myapplication.TodoList;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.FinishList.FinishEventListActivity;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.TodoBrowser.BrowserActivity;
import xyz.wendyltanpcy.myapplication.helper.OnStartDragListener;
import xyz.wendyltanpcy.myapplication.helper.SimpleItemTouchHelperCallback;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;


public class MainActivity extends AppCompatActivity implements OnStartDragListener,Serializable {

    private EventsAdapter MyAdapter;
    private List<TodoEvent> eventList = new ArrayList<>();
    private static boolean haveInit = false;
    private transient ItemTouchHelper mItemTouchHelper;
    private transient DrawerLayout mDrawerLayout;
    private transient SwipeRefreshLayout swipeRefresh;
    private transient ImageView homeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseInit();

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

        /*
        添加事件按钮响应
         */
        FloatingActionButton add = (FloatingActionButton) findViewById(R.id.add_event);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditMenuFragment dialog = new EditMenuFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("adapter", MyAdapter);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "edit bar");

            }
        });

        RecyclerView eventNameRecyclerView = (RecyclerView) findViewById(R.id.event_name_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new EventsAdapter(eventList,this);
        eventNameRecyclerView.setLayoutManager(layoutManager);
        eventNameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventNameRecyclerView.setAdapter(MyAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(MyAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(eventNameRecyclerView);
    }


    public  void initEvents(){
        LitePal.getDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        haveInit = false;
    }

    /**
     * 视图上的初始化
     */
    private void baseInit(){
        if(!haveInit){
            initEvents();

//            showStartupAnimate();
            eventList = DataSupport.findAll(TodoEvent.class);
            if (eventList.isEmpty()){
                View visibility = findViewById(R.id.no_event_layout);
                visibility.setVisibility(View.VISIBLE);
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navView.setCheckedItem(R.id.nav_task);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_finish:
                        startActivity(new Intent(MainActivity.this,FinishEventListActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.nav_broswer:
                        startActivity(new Intent(MainActivity.this,BrowserActivity.class));
                        break;
                    default:
                }
                return true;
            }
        });
    }

    /**
     * 刷新具体要做什么？
     */
    private void doRefresh(){
        eventList = MyAdapter.getTodoEventList();
        showNoEvent();
        MyAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 开机启动动画，但是因为bug未修复暂时停用
     */

    private void showStartupAnimate(){

        haveInit=true;
        homeImage = (ImageView) findViewById(R.id.startup);

        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(1000);//设定动画时间
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                homeImage.setVisibility(View.GONE);
            }
        });

        homeImage.setAnimation(alphaAnimation);
        homeImage.setVisibility(View.VISIBLE);
    }

    private void showNoEvent(){
        if (eventList.isEmpty()){
            View visibility = findViewById(R.id.no_event_layout);
            visibility.setVisibility(View.VISIBLE);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.setting) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }else if (id == R.id.delete){
            eventList.clear();
            DataSupport.deleteAll(TodoEvent.class);
            MyAdapter.notifyDataSetChanged();
            showNoEvent();
        }else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }


}
