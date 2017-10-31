package xyz.wendyltanpcy.easytodo.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.model.FinishEvent;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

/**
 * Created by Wendy on 2017/9/28.
 */

public class FinishEventsAdapter extends RecyclerView.Adapter<FinishEventsAdapter.ViewHolder> {

    private Context mContext;
    private List<TodoEvent> mFinishEventsList;

    @Override
    public FinishEventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item_finish,parent,false);
        FinishEventsAdapter.ViewHolder holder = new FinishEventsAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FinishEventsAdapter.ViewHolder holder, int position) {
        TodoEvent finishEvent = mFinishEventsList.get(position);
        holder.eventNameText.setText(finishEvent.getEventName());
        holder.eventNameText.setPaintFlags(holder.eventNameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.eventFinishDateText.setText(finishEvent.getEventDate());
    }

    @Override
    public int getItemCount() {
        return mFinishEventsList.size();
    }

    public FinishEventsAdapter(List<TodoEvent> finishEventsList){
        mFinishEventsList = finishEventsList;
    }

    public List<TodoEvent> getFinishEventsList(){
        return mFinishEventsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView eventNameText;
        private TextView eventFinishDateText;
        private ImageView handleView;
        public ViewHolder(View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            eventFinishDateText = itemView.findViewById(R.id.event_finish_date);
            handleView = itemView.findViewById(R.id.handle);
        }
    }
}
