package xyz.wendyltanpcy.easytodo.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.TodoList.EventContentActivity;
import xyz.wendyltanpcy.easytodo.helper.CheckBoxSample;
import xyz.wendyltanpcy.easytodo.model.FinishEvent;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;


/**
 * 事件适配器，用于主界面事件的显示处理逻辑
 */
public class EventsAdapter extends SwipeMenuRecyclerView.Adapter<EventsAdapter.ViewHolder> implements
        Serializable

{
    private transient Context mContext;
    private List<TodoEvent> mTodoEventList;
    private int position;
    private ViewHolder holder;
    public SwipeMenuRecyclerView menuRecyclerView;
    private int clickedItem = 0;

    private View visibility;        // showNoEvent用

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


    public EventsAdapter(List<TodoEvent> todoEventList, SwipeMenuRecyclerView menuRecyclerView, View v) {
        mTodoEventList = todoEventList;
        this.menuRecyclerView = menuRecyclerView;
        visibility = v;
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final EventsAdapter.ViewHolder holder, int position) {
        final TodoEvent todoEvent = mTodoEventList.get(holder.getAdapterPosition());

        // 显示过期问题
        if (todoEvent.isEventExpired())
            holder.expiredText.setVisibility(View.VISIBLE);
        else
            holder.expiredText.setVisibility(View.GONE);

        //seting defaul style or the viewholder don't know what to do
        if (todoEvent.isClicked()) {
            holder.checkBoxSample.setChecked(true);        // 勾的打勾
            holder.eventNameText.setPaintFlags(holder.eventNameText.getPaintFlags() | Paint
                    .STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.checkBoxSample.setChecked(false);
            holder.eventNameText.setPaintFlags(holder.eventNameText.getPaintFlags() & (~Paint
                    .STRIKE_THRU_TEXT_FLAG));
        }


        // 长按出现删除菜单
        holder.handleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        // 触摸拖动
        holder.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        menuRecyclerView.startDrag(holder);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        // 触摸拖动
        // 这个也能touch的话，会导致当item过多的时候，难以往下拉
//        holder.eventNameText.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                int action = motionEvent.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                    case MotionEvent.ACTION_UP:
//                        menuRecyclerView.startDrag(holder);
//                        break;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });

        // 长按出现删除菜单
        holder.eventNameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        holder.eventNameText.setText(todoEvent.getEventName());

        holder.eventNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!todoEvent.isClicked()) {
                    EventContentActivity eC = new EventContentActivity(holder);
                    eC.actionStart(mContext, todoEvent);
                }
            }
        });

        holder.checkBoxSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TodoEvent todoEvent = mTodoEventList.get(holder.getAdapterPosition());

                if(todoEvent.isClicked()) {
                    // 取消标记完成， 取消打钩的操作
                    holder.checkBoxSample.setChecked(false);
                    holder.eventNameText.setPaintFlags(holder.eventNameText.getPaintFlags() & (~Paint
                            .STRIKE_THRU_TEXT_FLAG));

                    todoEvent.setClicked(false);
//                    todoEvent.save();

                    --clickedItem;

                    if(clickedItem > 0) {
                        Snackbar.make(holder.itemView, "已完成 "+clickedItem+" 件事", Snackbar.LENGTH_SHORT)
                                .setAction("取消", cancelListener)
                                .show();
                    }

                } else {
                    // 标记完成， 打钩的操作
                    holder.checkBoxSample.setChecked(true);
                    holder.eventNameText.setPaintFlags(holder.eventNameText.getPaintFlags() | Paint
                            .STRIKE_THRU_TEXT_FLAG);

                    todoEvent.setClicked(true);
//                    todoEvent.save();

                    ++clickedItem;

                    Snackbar.make(holder.itemView, "已完成 "+clickedItem+" 件事", Snackbar.LENGTH_SHORT)
                            .setAction("取消", cancelListener)
                            .show();
                }
            }
        });

    }

    // Snackbar 的确定监听器
//    private View.OnClickListener confirmListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//            // 确定时执行将所有打钩的事件丢进FinishEvent中
//            Iterator<TodoEvent> iterator = mTodoEventList.iterator();
//            while (iterator.hasNext()){
//                TodoEvent tmp = iterator.next();
//                if (tmp.isClicked()){
//                    FinishEvent finishEvent = new FinishEvent();
//                    finishEvent.setEventName(tmp.getEventName());
//                    finishEvent.setEventFinishDate(tmp.getEventDate());
//                    finishEvent.save();
//                    tmp.delete();
//                    iterator.remove();
//                }
//            }
//            notifyDataSetChanged();
//            clickedItem = 0;
//        }
//    };

    // Snackbar 的取消监听器
    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // 取消打钩
            Iterator<TodoEvent> iterator = mTodoEventList.iterator();
            while (iterator.hasNext()){
                TodoEvent tmp = iterator.next();
                if (tmp.isClicked()){
                    tmp.setClicked(false);
                }
            }
            clickedItem = 0;
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return mTodoEventList.size();
    }
}
