package xyz.wendyltanpcy.myapplication.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.TodoList.EventContentActivity;
import xyz.wendyltanpcy.myapplication.TodoList.EventContentFragment;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Wendy on 2017/9/28.
 */

public class AlarmReceiver extends BroadcastReceiver {


    /**
     * 接收到广播之后，发送一条通知
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String name = intent.getStringExtra("name");
        String detail = intent.getStringExtra("detail");
        if (action == EventContentFragment.INTENT_ALARM) {

            Intent i = new Intent(context, EventContentActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context,666,i,0);

            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context)
                    .setContentTitle("今日要完成的事件:  " + name)
                    .setContentText("详情: " + detail)
                    .setSmallIcon(R.mipmap.icon2)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.mipmap.icon2))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
//                        .setVibrate(new long[]{0,1000,1000,1000})
//                        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                    .setLights(Color.GREEN,1000,1000)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            manager.notify(1,notification);
        }

    }
}

