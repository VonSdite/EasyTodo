package xyz.wendyltanpcy.myapplication.TodoList;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

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
                Event.save();
                eventDeadLineText.setText(Event.getEventDate());
                break;
            case REQUEST_TIME:
                Calendar calendar_time = (Calendar) data.getSerializableExtra(PickTimeFragment.EXTRA_TIME);
                Event.setEventCalendar(calendar_time);
                Event.setEventTime();
                Event.save();
                eventAlarmText.setText(Event.getEventTime());
            default:
                break;
        }

    }
}
