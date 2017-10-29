package xyz.wendyltanpcy.myapplication.TodoList;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

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

    private static final String TAG = "MainActivity";
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

        baseInit();  // 启动页面， 数据库加载， 界面初始化(滑动侧板菜单等)

        // showData();      // 输出eventList中的对象， 查看数据库 测试用

        // 设置刷新
//        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
//        swipeRefresh.setColorSchemeColors(ColorManager.getInstance().getStoreColor());
//        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                doRefresh();
//                            }
//                        });
//                    }
//                }).start();
//
//            }
//        });

        // 设置RecycleView
        SwipeMenuRecyclerView eventNameRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id
                .event_name_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new EventsAdapter(eventList, eventNameRecyclerView);

        // 设置侧滑菜单和侧滑菜单监听器
        eventNameRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator); // 创建滑动菜单
        eventNameRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener); // 滑动菜单监听器
        eventNameRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。

        eventNameRecyclerView.setLayoutManager(layoutManager);
        eventNameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventNameRecyclerView.setAdapter(MyAdapter);

        registerForContextMenu(eventNameRecyclerView);   // 不使用那个上下文菜单了

    }

    // 创建菜单：
    private SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int viewType) {

            int width = getResources().getDimensionPixelSize(R.dimen.dp_70); // 70dp宽度

            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 编辑菜单
            SwipeMenuItem addItem = new SwipeMenuItem(MainActivity.this)
                    .setBackground(R.drawable.selector_green)
                    .setImage(R.mipmap.ic_action_edit)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(addItem); // 添加菜单到右侧

            // 分享菜单
            SwipeMenuItem shareItem = new SwipeMenuItem(MainActivity.this)
                    .setBackground(R.drawable.selector_yellow)
                    .setImage(R.mipmap.ic_action_share)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(shareItem); // 添加菜单到右侧

            // 删除菜单
            SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                    .setBackground(R.drawable.selector_red)
                    .setImage(R.mipmap.ic_action_delete)
                    .setWidth(width)
                    .setHeight(height);
            rightMenu.addMenuItem(deleteItem); // 添加菜单到右侧

        }
    };

    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position。
            int menuPosition = menuBridge.getPosition();    // 菜单在RecyclerView的Item中的Position。

            final TodoEvent todoEvent = eventList.get(adapterPosition); // 获取到todoEvent
            switch (menuPosition){
                case 0:
                    EventContentActivity eC = new EventContentActivity(MyAdapter.getHolder());
                    eC.actionStart(MainActivity.this, todoEvent);
                    break;
                case 1:
                    Intent i = new Intent(Intent.ACTION_SEND);//setting action
                    i.setType("text/plain");//setting intent data type
                    StringBuilder builder = new StringBuilder();
                    builder.append("你的朋友通过ToDoList给你分享他的事件！\n");
                    builder.append("标题: " + todoEvent.getEventName() + "\n");
                    builder.append("详情: " + todoEvent.getEventDetail() + "\n");
//                    builder.append("是否已经完成: " + todoEvent.isEventFinish() + "\n");
                    String text = builder.toString();
                    i.putExtra(Intent.EXTRA_TEXT, text);
                    i.putExtra(Intent.EXTRA_SUBJECT, "an interesting event");
                    //putting extra
                    i = Intent.createChooser(i, "share event");//creating chooser to
                    // choose an app to do the activity
                    MainActivity.this.startActivity(i); //start activity
                    break;
                case 2:
                    todoEvent.delete();
                    eventList.remove(adapterPosition);
                    MyAdapter.notifyItemRangeRemoved(adapterPosition, 1);
                    break;
                default:
                    break;
            }

        }
    };

    public void swapEventTodoList(TodoEvent t1, TodoEvent t2) {
        TodoEvent tmp = new TodoEvent();
        tmp.setEventName(t1.getEventName());
        tmp.setEventDetail(t1.getEventDetail());
        tmp.setDelay(t1.isDelay());
        tmp.setEventDeadLine(t1.getEventDeadLine());
        tmp.setEventCalendar(t1.getEventCalendar());
        tmp.setEventDate();
        tmp.setEventTime();
        tmp.setEventPriority();
        tmp.setEventExpired(t1.isEventExpired());


        t1.setEventName(t2.getEventName());
        t1.setEventDetail(t2.getEventDetail());
        t1.setDelay(t2.isDelay());
        t1.setEventDeadLine(t2.getEventDeadLine());
        t1.setEventCalendar(t2.getEventCalendar());
        t1.setEventDate();
        t1.setEventTime();
        t1.setEventPriority();
        t1.setEventExpired(t2.isEventExpired());
        t1.save();

        t2.setEventName(tmp.getEventName());
        t2.setEventDetail(tmp.getEventDetail());
        t2.setDelay(tmp.isDelay());
        t2.setEventDeadLine(tmp.getEventDeadLine());
        t2.setEventCalendar(tmp.getEventCalendar());
        t2.setEventDate();
        t2.setEventTime();
        t2.setEventPriority();
        t2.setEventExpired(tmp.isEventExpired());
        t2.save();
    }

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            Collections.swap(eventList, fromPosition, toPosition);
            MyAdapter.notifyItemMoved(fromPosition, toPosition);
//            swapEventTodoList(eventList.get(fromPosition), eventList.get(toPosition));
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            int position = srcHolder.getAdapterPosition();
            eventList.remove(position);
            MyAdapter.notifyItemRemoved(position);
            Toast.makeText(MainActivity.this, "现在的第" + position + "条被删除。", Toast.LENGTH_SHORT).show();
        }

    };

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
            // eventList = sortEventList(eventList); // 按日期优先级排序，已不使用
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
        //doRefresh();
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

//        swipeRefresh.setRefreshing(false);
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

    // 上下文菜单， 这个已不使用了
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

    // 输出eventList中的对象， 查看数据库 测试用
    public void showData(){
        for(TodoEvent l : eventList)
        {
            Log.i(TAG, "onCreate: "+l);
        }
    }

}

