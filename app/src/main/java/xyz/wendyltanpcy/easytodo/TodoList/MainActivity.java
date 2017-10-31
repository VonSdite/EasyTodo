package xyz.wendyltanpcy.easytodo.TodoList;

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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import xyz.wendyltanpcy.easytodo.Adapter.EventsAdapter;
import xyz.wendyltanpcy.easytodo.FinishList.FinishEventListActivity;
import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.TodoBrowser.BrowserActivity;
import xyz.wendyltanpcy.easytodo.helper.ColorManager;
import xyz.wendyltanpcy.easytodo.model.Consts;
import xyz.wendyltanpcy.easytodo.model.FinishEvent;
import xyz.wendyltanpcy.easytodo.model.ThemeColor;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;


public class MainActivity extends AppCompatActivity implements Serializable, DialogInterface
        .OnDismissListener {

    private static final String TAG = "MainActivity";
    private EventsAdapter MyAdapter;
    private List<TodoEvent> eventList = new ArrayList<>();
    private static boolean haveInit = false;
    private DrawerLayout mDrawerLayout;
    private ImageView homeImage;
    private FloatingActionButton add;
    public static final String INTENT_EVENT = "intent_event";
    private static List<Integer> DelayList = new ArrayList<>();

    private boolean isSwap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        baseInit();  // 启动页面， 数据库加载， 界面初始化(滑动侧板菜单, 浮动添加事件按钮等)

        Collections.sort(eventList);  // 按evenList每个元素的pos进行排序， 即为显示的顺序

        // 设置RecycleView
        SwipeMenuRecyclerView eventNameRecyclerView = (SwipeMenuRecyclerView) findViewById(R.id
                .event_name_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        MyAdapter = new EventsAdapter(eventList, eventNameRecyclerView);

        // 设置侧滑菜单和侧滑菜单监听器
        eventNameRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator); // 创建滑动菜单
        eventNameRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener); // 滑动菜单监听器
        eventNameRecyclerView.setOnItemMoveListener(onItemMoveListener);// 监听拖拽，更新UI。

        eventNameRecyclerView.setLayoutManager(layoutManager);  // 设置LayoutManager
        eventNameRecyclerView.setItemAnimator(new DefaultItemAnimator());
        eventNameRecyclerView.setAdapter(MyAdapter);    // 设置adapter

        registerForContextMenu(eventNameRecyclerView); // 长按上下文菜单

    }

    // 创建滑动菜单
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

    // 滑动菜单的监听器
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();
            closeContextMenu();         // 关闭上下文菜单

            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView的Item的position
            int menuPosition = menuBridge.getPosition();    // 菜单在RecyclerView的Item中的Position

            final TodoEvent todoEvent = eventList.get(adapterPosition); // 获取到todoEvent
            switch (menuPosition){
                case 0:
                    if (todoEvent.isClicked()){
                        // 判断事件是否被打钩
                        Toast.makeText(MainActivity.this, "该事件已完成", Toast.LENGTH_SHORT).show();
                    } else {
                        EventContentActivity eC = new EventContentActivity(MyAdapter.getHolder());
                        eC.actionStart(MainActivity.this, todoEvent);
                    }
                    break;
                case 1:
                    Intent i = new Intent(Intent.ACTION_SEND);//setting action
                    i.setType("text/plain");//setting intent data type
                    StringBuilder builder = new StringBuilder();
                    builder.append("你的朋友通过ToDoList给你分享他的事件！\n");
                    builder.append("标题: " + todoEvent.getEventName() + "\n");
                    builder.append("详情: " + todoEvent.getEventDetail() + "\n");
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
                    showNoEvent(); // 如果evenList为空会显示没有事件时的提示
                    break;
                default:
                    break;
            }

        }
    };

    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            closeContextMenu();         // 关闭上下文菜单

            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            Collections.swap(eventList, fromPosition, toPosition); // 交换这两个对象
            eventList.get(fromPosition).setPos(fromPosition); // 重新设置pos， Item根据pos排序
            eventList.get(toPosition).setPos(toPosition);     // 重新设置pos， Item根据pos排序

            MyAdapter.notifyItemMoved(fromPosition, toPosition);
            isSwap = true;          // 标记发生了交换

            return true; // 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        // 这个不用看， 因为禁止了侧滑删除， 但这个方法必须重写上去
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


    @Override
    protected void onPause() {
        super.onPause();

        notifySwapItem();   // 如果发生了交换位置，保存到数据库

        // 去掉完成的事件
            removeClicked();
    }

    private void notifySwapItem(){
        // 保存数据库， 如果发生交换位置
        if (isSwap) {
            for (TodoEvent todoEvent : eventList) {
                todoEvent.save();
            }
            isSwap = false;         // 重新设置标志为false
        }
    }

    // 删除被clicked的item
    private void removeClicked(){
        // 去掉完成的事件
        Iterator<TodoEvent> todoIter = eventList.iterator();
        while (todoIter.hasNext()){
            TodoEvent todoEvent = todoIter.next();
            if (todoEvent.isClicked()){
                FinishEvent finishEvent = new FinishEvent();
                finishEvent.setEventName(todoEvent.getEventName());
                finishEvent.setEventFinishDate(todoEvent.getEventDate());
                finishEvent.save();
                todoIter.remove();
                todoEvent.delete();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 视图上的初始化
     */
    private void baseInit() {

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

        if (!haveInit) {
            initEvents();           // 获取数据库 或 创建数据库

            showStartupAnimate();   // 显示启动动画

            eventList = DataSupport.findAll(TodoEvent.class); // 获取 待办事项数据库
            showNoEvent();
        }

        sendNotification(eventList);

        initThemeColor();

        initDrawerLayout();

    }

    private void initDrawerLayout()
    {
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
                    case R.id.nav_task:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.nav_finish:
                        startActivity(new Intent(getApplicationContext(), FinishEventListActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case R.id.nav_broswer:
                        startActivity(new Intent(getApplicationContext(), BrowserActivity.class));
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
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
     * 一键自动推迟到明天
     *
     * @param list
     * @return
     */
    public List<TodoEvent> autoDelayOne(List<TodoEvent> list, List<Integer> callbackList) {
        for (Integer num : callbackList) {
            TodoEvent event = list.get(num.intValue());


            Date date = new Date(new Date().getTime()+24*60*60*1000); // 设置截止日期为第二天
            event.setEventDeadline(date);

            event.setEventDate();       // 设置事件年月日字符串
            event.setEventTime();       // 设置事件时分字符串

            event.save();
        }
        MyAdapter.notifyDataSetChanged();
        Toast.makeText(this, "已将事件推迟到明天！ ", Toast.LENGTH_SHORT).show();
        return list;
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
        initDrawerLayout(); // 重新加载侧滑菜单选择在已完成上
        showNoEvent();      // 判断是否显示空的layout
        if (ColorManager.IS_COLOR_CHANGE) {
            syncButtonColor();
        }
        MyAdapter.notifyDataSetChanged();
    }

    /**
     * when onResume,'init' all theme color again
     */
    private void syncButtonColor() {
        initThemeColor();
    }

    // 关闭上下文菜单时
    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
    }

    // 上下文菜单
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int clickedItemPosition = item.getOrder();
        TodoEvent event = eventList.get(clickedItemPosition);
        switch (item.getItemId()) {
            case 1:
                event.delete();
                eventList.remove(clickedItemPosition);
                MyAdapter.notifyItemRangeRemoved(clickedItemPosition, 1);
//                Toast.makeText(this, "你删掉了这条项目", Toast.LENGTH_LONG).show();
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

