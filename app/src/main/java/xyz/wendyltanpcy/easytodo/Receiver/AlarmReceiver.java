package xyz.wendyltanpcy.easytodo.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.TodoList.EventContentActivity;
import xyz.wendyltanpcy.easytodo.TodoList.MainActivity;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Wendy on 2017/9/28.
 */

public class AlarmReceiver extends BroadcastReceiver {


    private String name,detail;
    private List<TodoEvent> tempList = new ArrayList<>();

    /**
     * 接收到广播之后，发送一条通知
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        setTodayEvent();
//        name = intent.getStringExtra("name");
//        detail = intent.getStringExtra("detail");
//        showNormal(context);
        if (action == MainActivity.INTENT_EVENT) {
           showMainNotifi(context,intent);
        }

    }

    private void setTodayEvent(){
        List<TodoEvent> todoEventList = DataSupport.findAll(TodoEvent.class);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH)+1;
        StringBuilder builder;
        if (day<10){
            builder = new StringBuilder().append(year+"年"+month+"月"+"0"+day+"日");
        }else{
            builder = new StringBuilder().append(year+"年"+month+"月"+day+"日");
        }

        String dayString = builder.toString();
        for (TodoEvent event : todoEventList) {
            if (event.getEventDate().equals(dayString)) {
                tempList.add(event);
            }
        }
    }

//    public void showNormal(Context context) {
//
//        Intent i = new Intent(context, EventContentActivity.class);
//        PendingIntent pi = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(context)
//                .setContentTitle("今日要完成的事件:  " + name)
//                .setContentText("详情: " + detail)
//                .setSmallIcon(R.mipmap.icon2)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon2))
//                .setContentIntent(pi)
//                .setAutoCancel(true)
//                .setLights(Color.GREEN,1000,1000)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setPriority(NotificationCompat.PRIORITY_MAX)
//                .build();
//        manager.notify(999,notification);
//    }

    public NotificationCompat.Builder createBuilderWithRing(Context context,String title,String content,PendingIntent pi,String ringtoneName){
        return new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon2)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon2))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setVibrate(new long[]{0,1000,1000,1000})
                .setSound(Uri.fromFile(new File(ringtoneName)))
                .setLights(Color.GREEN,1000,1000)
                .setGroup("EVENT_GROUP")
                .setPriority(NotificationCompat.PRIORITY_MAX);
    }

    public NotificationCompat.Builder createBuilder(Context context,String title,String content,PendingIntent pi){
        return new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.icon2)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon2))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setGroup("EVENT_GROUP")
                .setPriority(NotificationCompat.PRIORITY_MAX);
    }

    public void showMainNotifi(Context context,Intent intent){
        int num = intent.getIntExtra("event_num",0);
        boolean vibrate = intent.getBooleanExtra("vibrate",false);
        String ringtoneName = "/system/media/audio/notifications/"+intent.getStringExtra("ringtoneName")+".ogg";
        String title;
        String content;
        int id;

        if(num==0){
            title = "今天的事情都做完了！";
            content = "享受美好的一天！";
            Intent intent1 = new Intent(context,MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context,777,intent1,0);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification notification = createBuilder(context,title,content,pi).build();
            assert manager != null;
            manager.notify(0,notification);
        }else{
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

            id=1;
            for (TodoEvent event :tempList) {
                title = event.getEventName();
                content = event.getEventDetail();
                Intent intent1 = new Intent(context, EventContentActivity.class);
                intent1.putExtra("event",event);
                PendingIntent pi = PendingIntent.getActivity(context, 777, intent1, 0);
                Notification notification;
                if (vibrate) {
                    if (id==1)
                        notification = createBuilderWithRing(context, title, content, pi, ringtoneName).setGroupSummary(true).build();
                    else
                        notification = createBuilderWithRing(context, title, content, pi, ringtoneName).build();
                } else {
                    if (id==1)
                        notification = createBuilder(context, title, content, pi).setGroupSummary(true).build();
                    else
                        notification = createBuilder(context, title, content, pi).build();
                }
                manager.notify(id, notification);
                id++;
            }
        }




    }

}

