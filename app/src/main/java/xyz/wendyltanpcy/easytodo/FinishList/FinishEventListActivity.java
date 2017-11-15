package xyz.wendyltanpcy.easytodo.FinishList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

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

public class FinishEventListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FinishEventsAdapter MyAdapter;
    private List<FinishEvent> finishEventList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private static boolean haveInit = false;
    private Drawer mDrawer;

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
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawerLayout.addDrawerListener(toggle);
//        toggle.syncState();
//        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
//        navView.setNavigationItemSelectedListener(this);
        //define drawer items
        SecondaryDrawerItem item1 = new SecondaryDrawerItem()
                .withIdentifier(1)
                .withName("未完成")
                .withIcon(R.drawable.ic_featured_play_list_black_24dp)
                .withDescription("Your todo event here");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem()
                .withIdentifier(2)
                .withName("已完成")
                .withIcon(R.drawable.ic_done_black_24dp)
                .withSetSelected(false)
                .withDescription("Your finish event here");


        //subitem
        SecondaryDrawerItem subitem1 = new SecondaryDrawerItem().withIdentifier(3).withName("生活").withBadge("Life").withTextColorRes(R.color.theme0);
        SecondaryDrawerItem subitem2 = new SecondaryDrawerItem().withIdentifier(3).withName("工作").withBadge("Work").withTextColorRes(R.color.theme1);
        SecondaryDrawerItem subitem3 = new SecondaryDrawerItem().withIdentifier(3).withName("紧急").withBadge("Emergency").withTextColorRes(R.color.theme2);
        SecondaryDrawerItem subitem4 = new SecondaryDrawerItem().withIdentifier(3).withName("私人").withBadge("Private").withTextColorRes(R.color.theme3);

        SecondaryDrawerItem item3  = new SecondaryDrawerItem().withIdentifier(3)
                .withName("类别")
                .withIcon(R.drawable.icon_bookmark)
                .withSubItems(subitem1,subitem2,subitem3,subitem4)
                .withIsExpanded(false)
                .withDescription("Your event category");

        final SecondaryDrawerItem item4 = new SecondaryDrawerItem()
                .withName("设置")
                .withIdentifier(4)
                .withIcon(R.drawable.ic_settings)
                .withDescription("Click for settings");

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.nav_header_bg)
                .addProfiles(
                        new ProfileDrawerItem().withName("EasyToDo").withEmail("youlinai233@gmail.com").withIcon(getResources().getDrawable(R.mipmap.icon2))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }

                })
                .build();



        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new SectionDrawerItem().withName("常规").withDivider(false),
                        item1,
                        item2,
                        item3,
                        new SectionDrawerItem().withName("相关"),
                        item4
                )
                .withCloseOnClick(false)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        switch ((int) drawerItem.getIdentifier()){
                            case 1:
                                onBackPressed();
                                mDrawer.closeDrawer();
                                break;
                            case 4:
                                startActivity(new Intent(FinishEventListActivity.this,SettingsActivity.class));
                                mDrawer.closeDrawer();
                                break;

                        }

                        return true;
                    }
                })
                .build();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_task:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
//            case R.id.nav_finish:
//                startActivity(new Intent(getApplicationContext(), FinishEventListActivity.class));
//                break;
            //隐藏浏览器入口，浏览器只作为测试版本用
//                    case R.id.nav_broswer:
//                        startActivity(new Intent(getApplicationContext(), BrowserActivity.class));
//                        mDrawerLayout.closeDrawer(Gravity.START);
//                        break;
            case R.id.nav_setting:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
