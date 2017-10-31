package xyz.wendyltanpcy.easytodo.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.Serializable;
import java.util.List;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.TodoList.EventContentActivity;
import xyz.wendyltanpcy.easytodo.helper.CheckBoxSample;
import xyz.wendyltanpcy.easytodo.model.FinishEvent;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

import static android.content.ContentValues.TAG;


/**
 * 事件适配器，用于主界面事件的显示处理逻辑
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements
        Serializable

{
    private transient Context mContext;
    private List<TodoEvent> mTodoEventList;
    private int position;
    private ViewHolder holder;
    public SwipeMenuRecyclerView menuRecyclerView;

    public ViewHolder getHolder() {
        return holder;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTodoEventSize(){
        return mTodoEventList.size();
    }

    /**
     * 自定义ViewHolder并且实现了ItemTouchHelperViewHolder接口（若无必要可以去掉）
     * 控制事件拖拽。
     */

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener {

        private TextView eventNameText;
        private CheckBoxSample checkBoxSample;
        private ImageView handleView;
        private TextView expiredText;

        public ViewHolder(View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            checkBoxSample = itemView.findViewById(R.id.event_finish);
            handleView = itemView.findViewById(R.id.handle);
            expiredText = itemView.findViewById(R.id.expired_text);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo
                contextMenuInfo) {
            //menuInfo is null
            menu.add(0, 1, getAdapterPosition(), "删除");
        }

    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    /**
     * 返回适配器内部的列表给外部
     *
     * @return
     */
    public List<TodoEvent> getTodoEventList() {
        return mTodoEventList;
    }


    public EventsAdapter(List<TodoEvent> todoEventList, SwipeMenuRecyclerView menuRecyclerView) {
        mTodoEventList = todoEventList;
        this.menuRecyclerView = menuRecyclerView;
    }


    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        final TodoEvent todoEvent = mTodoEventList.get(position);
        final int pos = position;
        final EventsAdapter.ViewHolder hd = holder;

        // 显示过期问题
        if (todoEvent.isEventExpired())
            hd.expiredText.setVisibility(View.VISIBLE);
        else
            hd.expiredText.setVisibility(View.GONE);

        //seting defaul style or the viewholder don't know what to do
        hd.checkBoxSample.setChecked(false);
        hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() & (~Paint
                .STRIKE_THRU_TEXT_FLAG));


        // 长按出现删除菜单
        hd.handleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(hd.getAdapterPosition());
                return false;
            }
        });

        // 触摸拖动
        hd.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        menuRecyclerView.startDrag(hd);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // 触摸拖动
        hd.eventNameText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        menuRecyclerView.startDrag(hd);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // 长按出现删除菜单
        hd.eventNameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(hd.getAdapterPosition());
                return false;
            }
        });

        hd.eventNameText.setText(todoEvent.getEventName());

        hd.eventNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventContentActivity eC = new EventContentActivity(hd);
                eC.actionStart(mContext, todoEvent);
            }
        });

        hd.checkBoxSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TodoEvent todoEvent = mTodoEventList.get(pos);
                if(todoEvent.isClicked()) {
                    hd.checkBoxSample.setChecked(false);
                    hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() & (~Paint
                            .STRIKE_THRU_TEXT_FLAG));
                    todoEvent.setClicked(false);
                } else {

                    hd.checkBoxSample.setChecked(true);
                    hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() | Paint
                            .STRIKE_THRU_TEXT_FLAG);
                    todoEvent.setClicked(true);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTodoEventList.size();
    }
}