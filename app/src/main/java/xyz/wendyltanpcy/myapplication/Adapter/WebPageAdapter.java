package xyz.wendyltanpcy.myapplication.Adapter;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.model.WebPage;

/**
 * Created by Wendy on 2017/10/10.
 */

public class WebPageAdapter extends RecyclerView.Adapter<WebPageAdapter.ViewHolder> {

    private Context mContext;
    private List<WebPage> mWebPageList;
    public int CURRENT_POSITION = -1;

    @Override
    public WebPageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null){
        mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item_webpage,parent,false);
        WebPageAdapter.ViewHolder holder = new WebPageAdapter.ViewHolder(view);
        return holder;
        }

    @Override
    public void onBindViewHolder(WebPageAdapter.ViewHolder holder, int position) {
        final WebPage webPage = mWebPageList.get(position);
        final int pos = position;
        holder.pageNameText.setText(webPage.getPageName());
        holder.pageUrlText.setText(webPage.getUrl());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CURRENT_POSITION = pos;
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Snackbar.make(view,"确认删除这个收藏吗",Snackbar.LENGTH_SHORT)
                        .setAction("确认", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                webPage.delete();
                                mWebPageList.remove(pos);
                                notifyDataSetChanged();
                            }
                        }).show();
                return true;
            }
        });
    }

    public int getCurrentPosition(){
        return CURRENT_POSITION;
    }

    @Override
    public int getItemCount() {
        return mWebPageList.size();
    }

    public WebPageAdapter(List<WebPage> webPageList){
        mWebPageList = webPageList;
    }

    public List<WebPage> getFinishEventsList(){
        return mWebPageList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView pageNameText;
        private TextView pageUrlText;
        public ViewHolder(View itemView) {
            super(itemView);
            pageNameText = itemView.findViewById(R.id.page_name);
            pageUrlText = itemView.findViewById(R.id.page_url);
        }
    }
}
