package xyz.wendyltanpcy.myapplication;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Wendy on 2017/9/16.
 */

public class EditMenuFragment extends DialogFragment implements View.OnClickListener{

    private ImageView calender;
    private EditText editEvent;
    private ImageView saveButton;
    private ImageView shareButton;
    private ImageView cameraButton;

    private String date_return;
    private String date;
    private EventsAdapter adapter;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // 使用不带theme的构造器，获得的dialog边框距离屏幕仍有几毫米的缝隙。
        Dialog dialog = new Dialog(getActivity(), R.style.Theme_Light_NoTitle_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // must be called before set content
        dialog.setContentView(R.layout.edit_menu_bar);
        dialog.setCanceledOnTouchOutside(true);

        // 设置宽度为屏宽、靠近屏幕底部。
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);
        window.setWindowAnimations(R.style.Animation_Bottom);

        //init other widget and set listener
        editEvent =  dialog.findViewById(R.id.edit_event);
        calender =  dialog.findViewById(R.id.pick_date);
        saveButton =  dialog.findViewById(R.id.save);
        shareButton =  dialog.findViewById(R.id.share);
        cameraButton =  dialog.findViewById(R.id.take_photo);

        editEvent.setOnClickListener(this);
        calender.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);
        cameraButton.setOnClickListener(this);


        Bundle bundle = getArguments();
        adapter = (EventsAdapter) bundle.getSerializable("adapter");


        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pick_date:
//                Intent i = new Intent(getContext(),PickDateActvity.class);
//                startActivityForResult(i,1);
                Toast.makeText(getContext(),"can't choose from here anymore",Toast.LENGTH_SHORT).show();
                break;
            case R.id.take_photo:
                Toast.makeText(getContext(),"taking photo!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.share:
                Toast.makeText(getContext(),"sharing!",Toast.LENGTH_SHORT).show();
                break;
            case R.id.save:
                Toast.makeText(getContext(),"text save!",Toast.LENGTH_SHORT).show();
                TodoEvent event = new TodoEvent();
                event.setEventName(editEvent.getText().toString());
                event.setEventFinish(false);
                event.setEventDetail("add more detail");
                event.setId(adapter.getItemCount());
                if (date_return!=null){
                    //set to specific date
                    event.setEventDeadLine(date_return);
                    event.save();
                }
                else{
                    //set to default date
                    Calendar now = Calendar.getInstance();
                    int year = now.get(Calendar.YEAR);
                    int month = now.get(Calendar.MONTH)+1;
                    int day = now.get(Calendar.DAY_OF_MONTH);
                    date = new String(new StringBuilder().append("在 ").append(year)
                            .append("年").append(month).append("月")
                            .append(day).append("日").append(" 前完成"));
                    event.setEventDeadLine(date);
                    event.save();
                }
                adapter.getTodoEventList().add(event);
                adapter.notifyItemInserted(adapter.getItemCount());
                View visibility = getActivity().findViewById(R.id.no_event_layout);
                visibility.setVisibility(View.INVISIBLE);
                this.dismiss();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK) {
                    date_return = data.getStringExtra("date_return");
                }

        }
    }


}
