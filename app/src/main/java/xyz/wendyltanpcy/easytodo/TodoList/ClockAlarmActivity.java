package xyz.wendyltanpcy.easytodo.TodoList;

import android.app.Service;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.model.Consts;


public class ClockAlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private String eventName;
    private String eventDetail;
    private boolean vibrate;
    private String ringtoneName;
    private SharedPreferences setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_alarm);
        int flag = this.getIntent().getIntExtra("flag", 2);
        eventName = this.getIntent().getStringExtra("eventName");
        eventDetail = this.getIntent().getStringExtra("eventDetail");

        showDialogInBroadcastReceiver(flag);


    }

    private void showDialogInBroadcastReceiver(final int flag) {

        //获得设置里的通知设置，响铃类型和震动
        setting = PreferenceManager.getDefaultSharedPreferences(this);
        vibrate = setting.getBoolean(Consts.VIBRATE_KEY, false);
        ringtoneName = setting.getString("ringtoneName", "");
        Log.i("TAG",ringtoneName);
        Log.i("TAG", String.valueOf(vibrate));
        if (ringtoneName!=null){
            ringtoneName = "/system/media/audio/notifications/"+ringtoneName+".ogg";
        }

        if (flag == 1 || flag == 2) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.fromFile(new File(ringtoneName)));
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        //数组参数意义：第一个参数为等待指定时间后开始震动，震动时间为第二个参数。后边的参数依次为等待震动和震动的时间
        //第二个参数为重复次数，-1为不重复，0为一直震动
        //如果设置了震动
        if (vibrate)
            if (flag == 0 || flag == 2) {
                vibrator = (Vibrator) this.getSystemService(Service.VIBRATOR_SERVICE);
                vibrator.vibrate(new long[]{100, 10, 100, 600}, 0);
            }

        new AlertDialog.Builder(this)
                .setTitle("EasyTodo:")
                .setIcon(R.mipmap.icon2)//设置图标
                .setTitle("EasyTodo")
                .setMessage("It's time to do event："+eventName+"\n\n事件详情: "+eventDetail)
                .setCancelable(true)//可取消
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (flag == 1 || flag == 2) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                        }
                        if (flag == 0 || flag == 2) {
                            vibrator.cancel();
                        }
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                })
                .show();

    }

}
