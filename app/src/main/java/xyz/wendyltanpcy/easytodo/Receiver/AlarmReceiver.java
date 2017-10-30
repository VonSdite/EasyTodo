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

import java.io.File;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.TodoList.EventContentActivity;
import xyz.wendyltanpcy.easytodo.TodoList.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Wendy on 2017/9/28.
 */

public class AlarmReceiver extends BroadcastReceiver {


    private String name,detail;

    /**
     * 接收到广播之后，发送一条通知
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        name = intent.getStringExtra("name");
        detail = intent.getStringExtra("detail");
        showNormal(context);

        if (action == MainActivity.INTENT_EVENT) {
           showMainNotifi(context,intent);
        }

    }

    public void showNormal(Context context) {

        Intent i = new Intent(context, EventContentActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setContentTitle("今日要完成的事件:  " + name)
                .setContentText("详情: " + detail)
                .setSmallIcon(R.mipmap.icon2)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon2))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setLights(Color.GREEN,1000,1000)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();
        manager.notify(1,notification);
    }

    public void showMainNotifi(Context context,Intent intent){
        int num = intent.getIntExtra("event_num",0);
        boolean vibrate = intent.getBooleanExtra("vibrate",false);
        String ringtoneName = "/system/media/audio/notifications/"+intent.getStringExtra("ringtoneName")+".ogg";
        String title;
        String content;

        if(num==0){
            title = "今天的事情都做完了！";
            content = "享受美好的一天！";
        }else{
            title = "今日要完成的事件数:  " + num;
            content = "记得完成哦！";
        }

        PendingIntent pi = PendingIntent.getActivity(context,777,intent,0);

        NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (vibrate){
            //Has vibrate
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.icon2)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon2))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setVibrate(new long[]{0,1000,1000,1000})
                    .setSound(Uri.fromFile(new File(ringtoneName)))
                    .setLights(Color.GREEN,1000,1000)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            manager.notify(1,notification);
        }else{
            //Don't have vibrate but have ringtone
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setSmallIcon(R.mipmap.icon2)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon2))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(Uri.fromFile(new File(ringtoneName)))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            manager.notify(1,notification);
        }

    }

}

