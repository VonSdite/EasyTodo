package xyz.wendyltanpcy.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import xyz.wendyltanpcy.myapplication.model.TodoEvent;

/**
 * Created by Wendy on 2017/9/19.
 */

//适配器
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements Serializable

{
    private  Context mContext;
    private List<TodoEvent> mTodoEventList ;
    private boolean isFinish=false;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView eventNameText;
        CheckBoxSample checkBoxSample;

        public ViewHolder(View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            checkBoxSample = itemView.findViewById(R.id.event_finish);
        }

    }


    public EventsAdapter(List<TodoEvent> todoEventList) {
        mTodoEventList=todoEventList;
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
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        TodoEvent todoEvent = mTodoEventList.get(position);
        final EventsAdapter.ViewHolder hd = holder;
        hd.eventNameText.setText(todoEvent.getEventName());
        final int pos = position;
        hd.eventNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TodoEvent todoEvent = mTodoEventList.get(pos);

                eventContentActivity.actionStart(mContext,todoEvent);
                mTodoEventList.remove(pos);
                isFinish = eventContentActivity.actionStatus();
                if (isFinish) {
                    mTodoEventList.add(pos, eventContentActivity.actionEnd());
                    notifyItemChanged(pos);
                }

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
    }

    @Override
    public int getItemCount() {
        return mTodoEventList.size();
    }
}
