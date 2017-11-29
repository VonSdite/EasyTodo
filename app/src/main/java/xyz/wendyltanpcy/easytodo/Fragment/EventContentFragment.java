package xyz.wendyltanpcy.easytodo.Fragment;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
import xyz.wendyltanpcy.easytodo.Service.AlarmService;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

import static android.content.Context.ALARM_SERVICE;

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
        eventAlarmText.setText(event.getEventTime());
        categoryAdapter.setEvent(event);

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
                Event.save();
                eventAlarmText.setText(Event.getEventTime());

//                //听说是原生的方法
                Intent i = new Intent(getContext(),AlarmService.class);
                i.putExtra("event",Event);
                PendingIntent sender = PendingIntent.getBroadcast(getContext(),0,i,0);
                AlarmManager manager = (AlarmManager)getContext().getSystemService(ALARM_SERVICE);
                Calendar c = Calendar.getInstance();
                c.setTime(Event.getEventDeadline());
                manager.set(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),sender);
                manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+5000, 5000, sender);
                getContext().startService(i);

                //尝试双进程


//                Intent service = new Intent(getContext(),LocalService.class);
//                getContext().startService(service);
//                Intent remoteService = new Intent(getContext(),AlarmService.class);
//
//                remoteService.putExtra("event",Event);
//                getContext().startService(remoteService);
//
//                Toast.makeText(getActivity(),"将会在指定时间提醒！",Toast.LENGTH_SHORT).show();

            default:
                break;
        }

    }
}
