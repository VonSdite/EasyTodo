package xyz.wendyltanpcy.easytodo.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import xyz.wendyltanpcy.easytodo.Adapter.ExpandListAdapter;
import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

/**
 * Created by Wendy on 2017/9/6.
 */

public class EventContentFragment extends Fragment {

    private View mView;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;

    private static final String TAG = "EventContentFragment";


    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;
    private TextView eventAlarmText;
    private TodoEvent Event;
    private ExpandableListView categoryExpandList;
    private ExpandListAdapter categoryAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.events_content_frag,container,false);

        eventNameText = mView.findViewById(R.id.event_name);
        eventDetailText =  mView.findViewById(R.id.event_detail);
        eventDeadLineText = mView.findViewById(R.id.event_deadline);
        eventAlarmText = mView.findViewById(R.id.event_alram);
        categoryExpandList = mView.findViewById(R.id.expand_list);
        categoryAdapter = new ExpandListAdapter();
        //传入事件
        categoryExpandList.setAdapter(categoryAdapter);
        return mView;
    }

    public void refresh(TodoEvent event){
        Event = event;
        eventNameText.setText(event.getEventName());
        eventDetailText.setText(event.getEventDetail());
        eventDeadLineText.setText(event.getEventDate());
        categoryAdapter.setEvent(event);
        if (Event.isSetAlarm()) eventAlarmText.setText(event.getEventTime());
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_DATE:

                // 控制时分秒不变
                Date d = (Date) data.getSerializableExtra(PickDateFragment.EXTRA_DATE);
                Calendar newC = Calendar.getInstance();
                newC.setTime(d);
                Calendar origin = Calendar.getInstance();
                origin.setTime(Event.getEventDeadline());
                origin.set(newC.get(Calendar.YEAR), newC.get(Calendar.MONTH), newC.get(Calendar.DATE));

                Date date = origin.getTime();
                Event.setEventDeadline(date);
                Event.setEventDate();
                Event.setEventTime();
                Event.save();

                eventDeadLineText.setText(Event.getEventDate());
                break;

            case REQUEST_TIME:
                int hour = data.getIntExtra("hour", 0);
                int min = data.getIntExtra("min", 0);

                // 控制年月日不变
                Calendar originC = Calendar.getInstance();
                originC.setTime(Event.getEventDeadline());
                originC.set(Calendar.HOUR_OF_DAY, hour);
                originC.set(Calendar.MINUTE, min);

                Date date_time = originC.getTime();
                Event.setEventDeadline(date_time);
                Event.setEventDate();
                Event.setEventTime();
                Event.setSetAlarm(true);        // 表明设置了闹钟
                Event.save();

                eventAlarmText.setText(Event.getEventTime());
            // TODO
                // 需要设置闹钟提醒
            default:
                break;
        }

    }
}
