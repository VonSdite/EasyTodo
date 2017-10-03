package xyz.wendyltanpcy.myapplication.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.List;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.TodoList.EventContentActivity;
import xyz.wendyltanpcy.myapplication.helper.CheckBoxSample;
import xyz.wendyltanpcy.myapplication.helper.RecyclerViewClickListener;
import xyz.wendyltanpcy.myapplication.model.FinishEvent;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;


/**
 * 事件适配器，用于主界面事件的显示处理逻辑
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> implements Serializable

{
    private transient Context mContext;
    private List<TodoEvent> mTodoEventList;
    private int position;

    public  int getThisPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }



    /**
     * 自定义ViewHolder并且实现了ItemTouchHelperViewHolder接口（若无必要可以去掉）
     * 控制事件拖拽。
     */

     public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, View.OnClickListener, View.OnLongClickListener{

            private TextView eventNameText;
            private CheckBoxSample checkBoxSample;
            private ImageView handleView;
            private RecyclerViewClickListener mRecyclerViewClickListener;

            public ViewHolder(View itemView) {
                    super(itemView);
                    eventNameText = itemView.findViewById(R.id.event_name);
                    checkBoxSample = itemView.findViewById(R.id.event_finish);
                    handleView = itemView.findViewById(R.id.handle);
                    itemView.setOnCreateContextMenuListener(this);
                    itemView.setOnLongClickListener(this);

            }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //menuInfo is null
            menu.add(0, 1, 0, "删除");
            menu.add(0, 2, 0, "优先级");
        }

        @Override
        public void onClick(View view) {

        }

        @Override
        public boolean onLongClick(View view) {
            mRecyclerViewClickListener.recyclerViewListClicked(this.getPosition());
            return true;
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    /**
     * 返回适配器内部的列表给外部
     * @return
     */
    public List<TodoEvent> getTodoEventList() {
        return mTodoEventList;
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
        final TodoEvent todoEvent = mTodoEventList.get(position);
        final int pos = position;
        final EventsAdapter.ViewHolder hd = holder;
        todoEvent.setId(position);

        hd.handleView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setPosition(hd.getPosition());
                return false;
            }
        });


        hd.eventNameText.setText(todoEvent.getEventName());

        hd.eventNameText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Snackbar.make(view,"你想分享这个事件吗？",Snackbar.LENGTH_LONG)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(Intent.ACTION_SEND);//setting action
                                i.setType("text/plain");//setting intent data type
                                StringBuilder builder = new StringBuilder();
                                builder.append("你的朋友通过ToDoList给你分享他的事件！\n");
                                builder.append("标题: "+todoEvent.getEventName()+"\n");
                                builder.append("详情: " + todoEvent.getEventDetail()+ "\n");
                                builder.append("是否已经完成: " + todoEvent.isEventFinish() + "\n");
                                String text = builder.toString();
                                i.putExtra(Intent.EXTRA_TEXT,text);
                                i.putExtra(Intent.EXTRA_SUBJECT,"an interesting event");//putting extra
                                i = Intent.createChooser(i,"share event");//creating chooser to choose an app to do the activity
                                mContext.startActivity(i); //start activity
                            }
                        }).show();
                return true;
            }
        });

        hd.eventNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventContentActivity eC = new EventContentActivity(hd);
                eC.actionStart(mContext,todoEvent);
            }
        });
        hd.checkBoxSample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!todoEvent.isEventFinish())
                    hd.checkBoxSample.toggle();
                hd.eventNameText.setPaintFlags(hd.eventNameText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

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
                        finishEvent.setId(todoEvent.getId());
                        finishEvent.setEventFinishDate(todoEvent.getEventDate());
                        finishEvent.setEventAlarmTime(todoEvent.getEventTime());
                        finishEvent.save();

                        /*
                        删掉此条未完成事件
                         */
                        todoEvent.setEventFinish(true);
                        mTodoEventList.remove(pos);
                        todoEvent.delete();
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, getItemCount());
                        Toast.makeText(mContext,"干得漂亮",Toast.LENGTH_SHORT).show();



                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hd.checkBoxSample.toggle();
                        hd.eventNameText.setPaintFlags( hd.eventNameText.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
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
