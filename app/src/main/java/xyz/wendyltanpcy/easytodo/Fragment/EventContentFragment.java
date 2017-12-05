package xyz.wendyltanpcy.easytodo.Fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import xyz.wendyltanpcy.easytodo.Adapter.ExpandListAdapter;
import xyz.wendyltanpcy.easytodo.TodoList.ClockAlarmActivity;
import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.helper.AlarmManagerUtil;
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
    private static TodoEvent Event;
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
        else eventAlarmText.setText("闹钟尚未设置");
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
                Toast.makeText(getActivity(),"闹钟已设置！",Toast.LENGTH_SHORT).show();
            // TODO
                // 需要设置闹钟提醒
                //using position of event as alarm id:
                int id = Event.getPos();
                AlarmManagerUtil.setAlarm(getContext(), 0, hour, min, id, 0, "时间到了", 0);
            default:
                break;
        }

    }

    //内部类广播接收器，接受闹钟发送的广播,需要定义为静态类
    public static class MyAlarmReceiver extends BroadcastReceiver {
        private MediaPlayer mediaPlayer;
        private Vibrator vibrator;

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String msg = intent.getStringExtra("msg");
            long intervalMillis = intent.getLongExtra("intervalMillis", 0);
            if (intervalMillis != 0) {
                AlarmManagerUtil.setAlarmTime(context, System.currentTimeMillis() + intervalMillis,
                        intent);
            }
            Log.i(TAG, "onReceive: AlarmBroadcast");

            int flag = intent.getIntExtra("soundOrVibrator", 0);

            Intent clockIntent = new Intent(context, ClockAlarmActivity.class);
            clockIntent.putExtra("msg", msg);
            clockIntent.putExtra("flag", flag);
            clockIntent.putExtra("eventName",Event.getEventName());
            clockIntent.putExtra("eventDetail",Event.getEventDetail());
            clockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//            PendingIntent pi = PendingIntent.getActivity(context,0,clockIntent,0);
            context.startActivity(clockIntent);
        }
    }
}
