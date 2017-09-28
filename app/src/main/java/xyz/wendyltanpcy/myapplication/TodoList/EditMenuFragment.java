package xyz.wendyltanpcy.myapplication.TodoList;


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

import java.util.Calendar;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.R;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

import static android.app.Activity.RESULT_OK;

/**
 * 底部弹出式，用以输入将要生成的新事件标题
 */

public class EditMenuFragment extends DialogFragment implements View.OnClickListener{


    private ImageView saveButton;

    private String dateReturn;
    private String date;
    private EventsAdapter adapter;
    private EditText editEvent;

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
        saveButton =  dialog.findViewById(R.id.save);
        editEvent = dialog.findViewById(R.id.edit_event);
        saveButton.setOnClickListener(this);

        //获得传入的适配器
        Bundle bundle = getArguments();
        adapter = (EventsAdapter) bundle.getSerializable("adapter");


        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save:
                Toast.makeText(getContext(),"text save!",Toast.LENGTH_SHORT).show();
                TodoEvent event = new TodoEvent();
                event.setEventName(editEvent.getText().toString());
                event.setEventFinish(false);
                event.setEventDetail("add more detail");
                event.setId(adapter.getItemCount());
                if (dateReturn !=null){
                    //set to specific date
                    event.setEventDeadLine(dateReturn);
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
//                adapter.notifyItemInserted(adapter.getItemCount());
                adapter.notifyDataSetChanged();
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
                    dateReturn = data.getStringExtra("dateReturn");
                }

        }
    }


}
