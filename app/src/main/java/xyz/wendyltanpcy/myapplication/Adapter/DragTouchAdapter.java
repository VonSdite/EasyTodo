package xyz.wendyltanpcy.myapplication.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.List;

import xyz.wendyltanpcy.myapplication.R;

/**
 * Created by Sdite on 17/10/28.
 */

public class DragTouchAdapter extends BaseAdapter<DragTouchAdapter.ViewHolder> {

    private SwipeMenuRecyclerView mMenuRecyclerView;
    private List<String> mDataList;

    public DragTouchAdapter(Context context, SwipeMenuRecyclerView menuRecyclerView) {
        super(context);
        this.mMenuRecyclerView = menuRecyclerView;
    }

    @Override
    public void notifyDataSetChanged(List<String> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(getInflater().inflate(R.layout.event_item, parent, false));
        viewHolder.mMenuRecyclerView = mMenuRecyclerView;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener {

        TextView tvTitle;
        SwipeMenuRecyclerView mMenuRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
//            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            itemView.findViewById(R.id.handle).setOnTouchListener(this);
        }

        public void setData(String title) {
            this.tvTitle.setText(title);
        }


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mMenuRecyclerView.startDrag(this);
                    break;
                }
            }
            return false;
        }
    }

}