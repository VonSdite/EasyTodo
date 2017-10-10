package xyz.wendyltanpcy.myapplication.TodoList;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Wendy on 2017/9/6.
 */

public class EventContentFragment extends Fragment {

    private View mView;
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;


    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;
    private TextView eventAlarmText;
    private TodoEvent Event;
    public static final String INTENT_ALARM = "intent_alarm";



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.events_content_frag,container,false);

        eventNameText = mView.findViewById(R.id.event_name);
        eventDetailText =  mView.findViewById(R.id.event_detail);
        eventDeadLineText = mView.findViewById(R.id.event_deadline);
        eventAlarmText = mView.findViewById(R.id.event_alram);

        return mView;
    }

    public void refresh(TodoEvent event){
        Event = event;
        eventNameText.setText(event.getEventName());
        eventDetailText.setText(event.getEventDetail());
        eventDeadLineText.setText(event.getEventDate());
        eventAlarmText.setText(event.getEventTime());
    }

    private void stopRemind(){

        Intent intent = new Intent(INTENT_ALARM);
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 666,
                intent, 0);
        AlarmManager am = (AlarmManager)getActivity().getSystemService(ALARM_SERVICE);
        //取消警报
        am.cancel(pi);
        Toast.makeText(getContext(), "关闭了提醒", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_DATE:
                Date date = (Date)data.getSerializableExtra(PickDateFragment.EXTRA_DATE);
                Event.setEventDeadLine(date);
                Calendar calendar_date = Calendar.getInstance();
                calendar_date.setTime(date);
                Event.setEventCalendar(calendar_date);
                Event.setEventDate();
                Event.setEventPriority();
                Event.save();
                eventDeadLineText.setText(Event.getEventDate());
                break;
            case REQUEST_TIME:
                Calendar calendar_time = (Calendar) data.getSerializableExtra(PickTimeFragment.EXTRA_TIME);
                Event.setEventCalendar(calendar_time);
                Event.setEventTime();
                Event.save();
                eventAlarmText.setText(Event.getEventTime());

                /*
                发送定时通知广播
                 */


                Intent i = new Intent(INTENT_ALARM);
                i.putExtra("name",Event.getEventName());
                i.putExtra("detail",Event.getEventDetail());
                PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 666, i, 0);
                AlarmManager am = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, Event.getEventCalendar().getTimeInMillis(), pi);
                Toast.makeText(getActivity(),"将会在指定时间提醒！",Toast.LENGTH_SHORT).show();
            default:
                break;
        }

    }
}
