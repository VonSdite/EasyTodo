package xyz.wendyltanpcy.myapplication.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.TodoList.EventContentActivity;
import xyz.wendyltanpcy.myapplication.helper.CheckBoxSample;
import xyz.wendyltanpcy.myapplication.helper.ItemTouchHelperAdapter;
import xyz.wendyltanpcy.myapplication.model.FinishEvent;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;


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

        public TextView getEventNameText() {
            return eventNameText;
        }


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

        todoEvent.save();

        //seting defaul style or the viewholder don't know what to do
        hd.checkBoxSample.setChecked(false);
        hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() & (~Paint
                .STRIKE_THRU_TEXT_FLAG));


        //setting click listener
        hd.eventNameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(hd.getAdapterPosition());
                return false;
            }
        });

        hd.handleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(hd.getAdapterPosition());
                return false;
            }
        });


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



        hd.eventNameText.setText(todoEvent.getEventName());

        if (todoEvent.isEventExpired())
            hd.expiredText.setVisibility(View.VISIBLE);
        else
            hd.expiredText.setVisibility(View.GONE);

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
//                if (!todoEvent.isEventFinish()) {
                    hd.checkBoxSample.setChecked(true);
                    hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() | Paint
                            .STRIKE_THRU_TEXT_FLAG);
//                }


                final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("确定已经完成这个事件？");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TodoEvent todoEvent = mTodoEventList.get(pos);
                        FinishEvent finishEvent = new FinishEvent();

                        /*
                        对完成事件信息设置
                         */
                        finishEvent.setEventName(todoEvent.getEventName());
//                        finishEvent.setId(todoEvent.getId());
                        finishEvent.setEventFinishDate(todoEvent.getEventDate());
                        finishEvent.setEventAlarmTime(todoEvent.getEventTime());
                        finishEvent.save();

                        /*
                        删掉此条未完成事件
                         */
//                        todoEvent.setEventFinish(true);
                        todoEvent.delete();
                        mTodoEventList.remove(pos);
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "干得漂亮", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hd.checkBoxSample.setChecked(false);
                        hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() & (~Paint
                                .STRIKE_THRU_TEXT_FLAG));
                    }
                });
                dialog.setCancelable(false);
                dialog.show();

            }
        });
        hd.handleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.showContextMenu();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mTodoEventList.size();
    }

}
