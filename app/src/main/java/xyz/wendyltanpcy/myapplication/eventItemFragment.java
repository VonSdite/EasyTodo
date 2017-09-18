package xyz.wendyltanpcy.myapplication;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import xyz.wendyltanpcy.myapplication.model.TodoEvent;

import static android.R.id.list;

/**
 * Created by Wendy on 2017/9/6.
 */

public class eventItemFragment extends Fragment {

    private EventsAdapter MyAdapter;
    private List<TodoEvent> eventList = getEvents();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list_frag,container,false);
        RecyclerView eventNameRecyclerView = view.findViewById(R.id.news_title_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        eventNameRecyclerView.setLayoutManager(layoutManager);
        MyAdapter = new EventsAdapter(eventList);
        eventNameRecyclerView.setAdapter(MyAdapter);


        return  view;
    }

    private List<TodoEvent> getEvents(){
        List<TodoEvent> todoEventList = new ArrayList<>();
        for(int i =1;i<=50;i++){
            TodoEvent todoEvent = new TodoEvent();
            todoEvent.setEventName("This is event name " + i);
            todoEvent.setEventDetail(getRandomLengthContent("This is todoEvent etail"+i+"."));
            todoEvent.setEventDeadLine("9月14日");
            todoEventList.add(todoEvent);
        }

        return todoEventList;
    }

    private List<TodoEvent> getCurrentList(){
        return eventList;
    }

    private String getRandomLengthContent(String content) {
        Random random = new Random();
        int length = random.nextInt(20) + 1;
        StringBuilder builder = new StringBuilder();
        for(int i = 0;i<length;i++){
            builder.append(content);
        }
        return builder.toString();
    }


    //内部类适配器
    class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder>

    {

        private List<TodoEvent> mTodoEventList;

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
            mTodoEventList = todoEventList;
        }


        @Override
        public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
            final ViewHolder holder = new ViewHolder(view);
            holder.checkBoxSample.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final CheckBoxSample checkBoxSample = view.findViewById(R.id.event_finish);
                    checkBoxSample.toggle();
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Are u sure you have finish this event?");
                    dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int position = holder.getAdapterPosition();
                            TodoEvent todoEvent = mTodoEventList.get(position);
                            todoEvent.setEventFinish(true);
                            mTodoEventList.remove(position);
                            MyAdapter.notifyItemRemoved(position);
                            MyAdapter.notifyItemRangeChanged(position, getItemCount());
                        }
                    });
                    dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkBoxSample.toggle();
                        }
                    });
                    dialog.show();


                }
            });
            holder.eventNameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TodoEvent todoEvent = mTodoEventList.get(holder.getAdapterPosition());
                    eventContentActivity.actionStart(getContext(), todoEvent.getEventName(), todoEvent.getEventDetail(),todoEvent.getEventDeadLine());

                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
            TodoEvent todoEvent = mTodoEventList.get(position);
            holder.eventNameText.setText(todoEvent.getEventName());


        }

        @Override
        public int getItemCount() {
            return mTodoEventList.size();
        }
    }
}
