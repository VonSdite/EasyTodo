package xyz.wendyltanpcy.myapplication.TodoList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.FinishList.FinishEventListActivity;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.TodoBrowser.BrowserActivity;
import xyz.wendyltanpcy.myapplication.helper.ColorManager;
import xyz.wendyltanpcy.myapplication.model.Consts;
import xyz.wendyltanpcy.myapplication.model.ThemeColor;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;


public class MainActivity extends AppCompatActivity implements Serializable, DialogInterface
        .OnDismissListener {

    private EventsAdapter MyAdapter;
    private List<TodoEvent> eventList = new ArrayList<>();
    private static boolean haveInit = false;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefresh;
    private ImageView homeImage;
    private FloatingActionButton add;
    public static final String INTENT_EVENT = "intent_event";
    private static List<Integer> DelayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // add Button的设置
        add = (FloatingActionButton) findViewById(R.id.add_event);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditMenuFragment dialog = new EditMenuFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("adapter", MyAdapter);
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "edit bar");
                view.setVisibility(View.GONE); // 隐藏加号按钮
            }
        });

        baseInit();

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

        RecyclerView eventNameRecyclerView = (RecyclerView) findViewById(R.id
                .event_name_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new EventsAdapter(eventList);

        eventNameRecyclerView.setLayoutManager(layoutManager);
        eventNameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventNameRecyclerView.setAdapter(MyAdapter);
        registerForContextMenu(eventNameRecyclerView);

        // 拖拽换位 滑动删除实现
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder
                    viewHolder) {
                //首先回调的方法 返回int表示是否监听该方向
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;//拖拽

                //START  右向左
                int swipeFlags = ItemTouchHelper.START; //侧滑删除
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                // 拖拽换位
                Collections.swap(eventList,viewHolder.getAdapterPosition(),target.getAdapterPosition());
                MyAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // 侧滑删除事件
                eventList.get(viewHolder.getAdapterPosition()).delete();
                eventList.remove(viewHolder.getAdapterPosition());
                MyAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                // 是否可拖拽
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                // 是否可滑动
                return true;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            /**
             * 手指松开的时候还原
             * @param recyclerView
             * @param viewHolder
             */
            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setBackgroundColor(0);
            }
        });

        mItemTouchHelper.attachToRecyclerView(eventNameRecyclerView);
    }

    public void initEvents() {
        LitePal.getDatabase();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        haveInit = false;
    }

    public void checkExpired(List<TodoEvent> todoEventList) {
        for (TodoEvent event : todoEventList) {
            event.setEventPriority();
        }
    }

    /**
     * 视图上的初始化
     */
    private void baseInit() {
        if (!haveInit) {
            initEvents();           // 获取数据库 或 创建数据库

            showStartupAnimate();   // 显示启动动画

            eventList = DataSupport.findAll(TodoEvent.class); // 获取 待办事项数据库
            showNoEvent();
            //eventList = sortEventList(eventList);
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
        navView.setNavigationItemSelectedListener(new NavigationView
                .OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_finish:
                        startActivity(new Intent(MainActivity.this, FinishEventListActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.nav_broswer:
                        startActivity(new Intent(MainActivity.this, BrowserActivity.class));
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

    private List<TodoEvent> sortEventList(List<TodoEvent> todoEventList) {
        Collections.sort(todoEventList, new Comparator<TodoEvent>() {
            public int compare(TodoEvent event1, TodoEvent event2) {
                return Integer.valueOf(event1.getEventDateNum()).compareTo(event2.getEventDateNum
                        ());
            }
        });
        return todoEventList;
    }

    /**
     * 初始化主题颜色
     */
    public void initThemeColor() {
        ThemeColor color = DataSupport.find(ThemeColor.class, 1);
        if (color != null) {
            ColorManager.getInstance().notifyColorChanged(color.getColor());
            add.setBackgroundTintList(ColorStateList.valueOf(color.getColor()));
        } else {
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
    private void sendNotification(List<TodoEvent> todoEventList) {
        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);


        if (setting.getBoolean(Consts.NOTIFICATION_KEY, false)) {
            Calendar calendar = Calendar.getInstance();
            int eventCount = 0;
            boolean vibrate = setting.getBoolean(Consts.VIBRATE_KEY, false);
            String ringtoneName = setting.getString("ringtoneName", "");
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            StringBuilder builder = new StringBuilder().append(year).append("年").append(month + 1)
                    .append("月").append(day).append("日");
            for (TodoEvent event : todoEventList) {
                if (event.getEventDate().equals(builder.toString())) {
                    eventCount++;
                }
            }
            Intent i = new Intent(INTENT_EVENT);
            i.putExtra("event_num", eventCount);
            i.putExtra("vibrate", vibrate);
            i.putExtra("ringtoneName", ringtoneName);
            PendingIntent pi = PendingIntent.getBroadcast(this, 777, i, 0);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        }

    }


    /**
     * 一键自动推迟到今天
     *
     * @param list
     * @return
     */
    public List<TodoEvent> autoDelayOne(List<TodoEvent> list, List<Integer> callbackList) {
        for (Integer num : callbackList) {
            TodoEvent event = list.get(num.intValue());
            Calendar newC = Calendar.getInstance();
            event.setEventDeadLine(newC.getTime());
            event.setEventCalendar(newC);
            event.setEventDate();
            event.setEventTime();
            event.setEventPriority();
            event.setEventExpired(false);
            event.save();

        }
        doRefresh();
        Toast.makeText(this, "已将事件推迟到今天！ ", Toast.LENGTH_SHORT).show();
        return list;
    }

    /**
     * 刷新具体要做什么
     */
    private void doRefresh() {
        //eventList = MyAdapter.getTodoEventList();
        //showNoEvent();
        //eventList = sortEventList(eventList);
        //MyAdapter.notifyDataSetChanged();

        swipeRefresh.setRefreshing(false);
    }

    /**
     * 开机启动动画
     */

    private void showStartupAnimate() {

        haveInit = true;
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

    private void showNoEvent() {
        if (eventList.isEmpty()) {
            View visibility = findViewById(R.id.no_event_layout);
            visibility.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        doRefresh();
        if (ColorManager.IS_COLOR_CHANGE) {
            syncButtonColor();
        }


    }

    /**
     * when onResume,'init' all theme color again
     */

    private void syncButtonColor() {
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
                Toast.makeText(this, "优先级: " + prioriy + " 完成日期: " + event.getEventDate(), Toast
                        .LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * showing delay dialog
     */

    public void showDelayDialog() {
        List<Map<String, Object>> list = new ArrayList<>();

        for (TodoEvent event : eventList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", event.getEventName());
            map.put("date", event.getEventDate());
            map.put("delay", event.isDelay());
            list.add(map);
        }

        FragmentManager manager = getSupportFragmentManager();
        DelayFragment dialog = DelayFragment.newInstance(list);
        dialog.show(manager, "DelayDialog");
    }

    public static void getDelayPosList(List<Integer> list) {
        DelayList = list;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        if (id == R.id.delete) {
            AlertDialog.Builder deleteAlert = new AlertDialog.Builder(MainActivity.this);
            deleteAlert.setTitle("你确定要全部删除吗?");
            deleteAlert.setCancelable(false);
            deleteAlert.setPositiveButton("全部删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    eventList.clear();
                    DataSupport.deleteAll(TodoEvent.class);
                    MyAdapter.notifyDataSetChanged();
                    showNoEvent();
                }
            });
            deleteAlert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            deleteAlert.show();

        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (id == R.id.delay) {
            DelayList.clear();
            showDelayDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (!DelayList.isEmpty())
            eventList = autoDelayOne(eventList, DelayList);
    }

}

