package xyz.wendyltanpcy.myapplication.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import xyz.wendyltanpcy.myapplication.CheckBoxSample;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.eventContentActivity;
import xyz.wendyltanpcy.myapplication.helper.ItemTouchHelperAdapter;
import xyz.wendyltanpcy.myapplication.helper.ItemTouchHelperViewHolder;
import xyz.wendyltanpcy.myapplication.helper.OnStartDragListener;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

/**
 * Created by Wendy on 2017/9/19.
 */

//适配器
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements Serializable,ItemTouchHelperAdapter

{
    private  Context mContext;
    protected List<TodoEvent> mTodoEventList ;
    private final OnStartDragListener mDragStartListener;

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mTodoEventList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mTodoEventList.remove(position);
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {
        protected TextView eventNameText;
        protected  CheckBoxSample checkBoxSample;
        protected  ImageView handleView;
        public ViewHolder(View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            checkBoxSample = itemView.findViewById(R.id.event_finish);
            handleView = itemView.findViewById(R.id.handle);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public List<TodoEvent> getTodoEventList() {
        return mTodoEventList;
    }

    public EventsAdapter(List<TodoEvent> todoEventList,OnStartDragListener dragStartListener) {
        mTodoEventList=todoEventList;
        mDragStartListener = dragStartListener;
    }


    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(final EventsAdapter.ViewHolder holder, int position) {
        final TodoEvent todoEvent = mTodoEventList.get(position);
        final int pos = position;
        final EventsAdapter.ViewHolder hd = holder;
        todoEvent.setId(position+1);

        hd.eventNameText.setText(todoEvent.getEventName());

        hd.eventNameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(mContext,todoEvent.getEventName(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        hd.eventNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventContentActivity.actionStart(mContext,todoEvent,hd);
            }
        });
        hd.checkBoxSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hd.checkBoxSample.toggle();
                final AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setTitle("Are u sure you have finish this event?");
                dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TodoEvent todoEvent = mTodoEventList.get(pos);
                        todoEvent.setEventFinish(true);
                        mTodoEventList.remove(pos);
                        todoEvent.delete();
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, getItemCount());
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hd.checkBoxSample.toggle();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }
        });

        hd.handleView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    mDragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTodoEventList.size();
    }
}
