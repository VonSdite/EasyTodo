package xyz.wendyltanpcy.myapplication.TodoList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.FinishList.FinishEventListActivity;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.TodoBrowser.BrowserActivity;
import xyz.wendyltanpcy.myapplication.helper.ColorManager;
import xyz.wendyltanpcy.myapplication.model.Consts;
import xyz.wendyltanpcy.myapplication.model.ThemeColor;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;


public class MainActivity extends AppCompatActivity implements Serializable{

    private EventsAdapter MyAdapter;
    private List<TodoEvent> eventList = new ArrayList<>();
    private static boolean haveInit = false;
    private transient DrawerLayout mDrawerLayout;
    private transient SwipeRefreshLayout swipeRefresh;
    private transient ImageView homeImage;
    private FloatingActionButton add;
    public static final String INTENT_EVENT= "intent_event";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add = (FloatingActionButton) findViewById(R.id.add_event);

        baseInit();
        addEvent();

        /*
        设置刷新
         */
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(ColorManager.getInstance().getStoreColor());
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



        RecyclerView eventNameRecyclerView = (RecyclerView) findViewById(R.id.event_name_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new EventsAdapter(eventList);
        eventNameRecyclerView.setLayoutManager(layoutManager);
        eventNameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventNameRecyclerView.setAdapter(MyAdapter);
        registerForContextMenu(eventNameRecyclerView);





    }

    private void addEvent(){
         /*
        添加事件按钮响应
         */

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
    }


    public  void initEvents(){
        LitePal.getDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        haveInit = false;
    }

    public void checkExpired(List<TodoEvent> todoEventList){
        for (TodoEvent event:todoEventList){
            event.setEventPriority();
        }
    }

    /**
     * 视图上的初始化
     */
    private void baseInit(){
        if(!haveInit){
            initEvents();

            showStartupAnimate();
            eventList = DataSupport.findAll(TodoEvent.class);
            showNoEvent();
            eventList = sortEventList(eventList);

        }

        sendNotification(eventList);
        checkExpired(eventList);
        initThemeColor();




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
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.nav_setting:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    default:
                }
                return true;
            }
        });
    }

    /**
     * 按优先级排序列表(根据日期）
     */

    private List<TodoEvent> sortEventList(List<TodoEvent> todoEventList){
        Collections.sort(todoEventList, new Comparator<TodoEvent>(){
            public int compare(TodoEvent event1, TodoEvent event2) {
                 return Integer.valueOf(event1.getEventDateNum()).compareTo(event2.getEventDateNum());
        }});
        return  todoEventList;
    }

    /**
     * 初始化主题颜色
     */
    public void initThemeColor(){
        ThemeColor color = DataSupport.find(ThemeColor.class,1);
        if (color!=null) {
            ColorManager.getInstance().notifyColorChanged(color.getColor());
            add.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
        }
        else{
            color = new ThemeColor();
            color.setColor(ColorManager.DEFAULT_COLOR);
            color.save();
            ColorManager.getInstance().notifyColorChanged(color.getColor());
            add.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
        }
    }



    /**
     * 发送通知
     */
    private void sendNotification(List<TodoEvent> todoEventList){
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);




        if (setting.getBoolean(Consts.NOTIFICATION_KEY,false)){
            Calendar calendar = Calendar.getInstance();
            int eventCount = 0;
            boolean vibrate = setting.getBoolean(Consts.VIBRATE_KEY,false);
            String ringtoneName = setting.getString("ringtoneName","");
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            StringBuilder builder = new StringBuilder().append(year).append("年").append(month+1)
                    .append("月").append(day).append("日");
            for(TodoEvent event:todoEventList){
                if (event.getEventDate().equals(builder.toString())){
                    eventCount++;
                }
            }
            Intent i = new Intent(INTENT_EVENT);
            i.putExtra("event_num",eventCount);
            i.putExtra("vibrate",vibrate);
            i.putExtra("ringtoneName",ringtoneName);
            PendingIntent pi = PendingIntent.getBroadcast(this, 777, i, 0);
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }

    }
    /**
     * 刷新具体要做什么
     */
    private void doRefresh(){
        eventList = MyAdapter.getTodoEventList();
        showNoEvent();
        eventList = sortEventList(eventList);
        MyAdapter.notifyDataSetChanged();
        swipeRefresh.setRefreshing(false);
    }

    /**
     * 开机启动动画
     */

    private void showStartupAnimate(){

        haveInit=true;
        homeImage = (ImageView) findViewById(R.id.startup);

        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(2000);//设定动画时间
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

    /**
     * 显示没有事件时候
     */

    private void showNoEvent(){
        if (eventList.isEmpty()){
            View visibility = findViewById(R.id.no_event_layout);
            visibility.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doRefresh();
        if (ColorManager.IS_COLOR_CHANGE){
            syncButtonColor();
        }
    }

    /**
     * when onResume,'init' all theme color again
     */

    private void syncButtonColor(){
        initThemeColor();
    }





    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int clickedItemPosition = item.getOrder();
        TodoEvent event = eventList.get(clickedItemPosition);
        switch (item.getItemId()) {
            case 1:
                event.delete();
                eventList.remove(clickedItemPosition);
                MyAdapter.notifyDataSetChanged();
                Toast.makeText(this, "你删掉了这条项目", Toast.LENGTH_LONG).show();
                break;
            case 2:
                event.setEventPriority();
                String prioriy = event.getEventPriority();
                Toast.makeText(this, "优先级: "+prioriy+" 完成日期: "+event.getEventDate() , Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
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
         if (id == R.id.delete){
            eventList.clear();
            DataSupport.deleteAll(TodoEvent.class);
            MyAdapter.notifyDataSetChanged();
            showNoEvent();
        }else if(id == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }


}
