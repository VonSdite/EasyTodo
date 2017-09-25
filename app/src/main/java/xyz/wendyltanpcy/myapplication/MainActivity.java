package xyz.wendyltanpcy.myapplication;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.helper.OnStartDragListener;
import xyz.wendyltanpcy.myapplication.helper.SimpleItemTouchHelperCallback;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;


public class MainActivity extends AppCompatActivity implements OnStartDragListener {

    private EventsAdapter MyAdapter;
    private List<TodoEvent> eventList = new ArrayList<>();
    private static boolean haveInit = false;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!haveInit){
            initEvents();
            haveInit=true;
            eventList = DataSupport.findAll(TodoEvent.class);
            if (eventList.isEmpty()){
                View visibility = findViewById(R.id.no_event_layout);
                visibility.setVisibility(View.VISIBLE);
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        RecyclerView eventNameRecyclerView = (RecyclerView) findViewById(R.id.news_title_recycler_view);
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
            if (eventList.isEmpty()){
                View visibility = findViewById(R.id.no_event_layout);
                visibility.setVisibility(View.VISIBLE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }
}
