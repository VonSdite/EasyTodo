package xyz.wendyltanpcy.myapplication.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import xyz.wendyltanpcy.myapplication.IMyAidlInterface;
import xyz.wendyltanpcy.myapplication.Receiver.AlarmReceiver;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

/**
 * Created by Wendy on 2017/10/14.
 */

public class AlarmService extends Service {

    private AlarmManager am;
    private PendingIntent pi;
    private long time;
    private String title;
    private String content;
    private TodoEvent mEvent;

    MyConn conn;
    MyBinder binder;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mEvent = (TodoEvent) intent.getSerializableExtra("event");
        getAlarmTime();

        Toast.makeText(this, " 远程服务活了", Toast.LENGTH_SHORT).show();
        this.bindService(new Intent(this, LocalService.class), conn, Context.BIND_IMPORTANT);
        return START_STICKY;
    } //这里为了提高优先级，选择START_REDELIVER_INTENT 没那么容易被内存清理时杀死

    public void getAlarmTime() {

            title = mEvent.getEventName();
//            time = mEvent.getEventCalendar().getTimeInMillis();
            content = mEvent.getEventDetail();

            Intent startNotification = new Intent(this, AlarmReceiver.class);
            startNotification.putExtra("name",title);
            startNotification.putExtra("detail",content);
            am = (AlarmManager) getSystemService(ALARM_SERVICE);   //这里是系统闹钟的对象
            pi = PendingIntent.getBroadcast(this, 0, startNotification, PendingIntent.FLAG_UPDATE_CURRENT);     //设置事件
            if (time != 0) {
                am.set(AlarmManager.RTC_WAKEUP, time, pi);    //提交事件，发送给 广播接收器
            } else {
                //当提醒时间为空的时候，关闭服务，下次添加提醒时再开启
                stopService(new Intent(this, AlarmService.class));
            }
    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        conn = new MyConn();
        binder = new MyBinder();
    }


    class MyBinder extends IMyAidlInterface.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return AlarmService.class.getSimpleName();
        }
    }

    class MyConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i("xiaoqi", "绑定本地服务成功");
            // Toast.makeText(RomoteService.this, "绑定本地服务成功", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("xiaoqi", "本地服务被干掉了");
            Toast.makeText(AlarmService.this, "本地服务挂了", Toast.LENGTH_SHORT).show();

            //开启本地服务
            AlarmService.this.startService(new Intent(AlarmService.this, LocalService.class));
            //绑定本地服务
            AlarmService.this.bindService(new Intent(AlarmService.this, LocalService.class), conn, Context.BIND_IMPORTANT);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //开启本地服务
        AlarmService.this.startService(new Intent(AlarmService.this, LocalService.class));
        //绑定本地服务
        AlarmService.this.bindService(new Intent(AlarmService.this, LocalService.class), conn, Context.BIND_IMPORTANT);

    }
}

