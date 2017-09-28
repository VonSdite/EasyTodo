package xyz.wendyltanpcy.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.helper.CheckBoxSample;
import xyz.wendyltanpcy.myapplication.model.FinishEvent;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

/**
 * Created by Wendy on 2017/9/28.
 */

public class FinishEventsAdapter extends RecyclerView.Adapter<FinishEventsAdapter.ViewHolder> {

    private Context mContext;
    private List<FinishEvent> mFinishEventsList;

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
        FinishEvent finishEvent = mFinishEventsList.get(position);
        holder.eventNameText.setText(finishEvent.getEventName());
    }

    @Override
    public int getItemCount() {
        return mFinishEventsList.size();
    }

    public FinishEventsAdapter(List<FinishEvent> finishEventsList){
        mFinishEventsList = finishEventsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView eventNameText;
        private ImageView handleView;
        public ViewHolder(View itemView) {
            super(itemView);
            eventNameText = itemView.findViewById(R.id.event_name);
            handleView = itemView.findViewById(R.id.handle);
        }
    }
}
