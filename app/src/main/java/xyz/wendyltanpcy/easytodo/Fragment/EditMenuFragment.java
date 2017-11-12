package xyz.wendyltanpcy.easytodo.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import xyz.wendyltanpcy.easytodo.Adapter.EventsAdapter;
import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.model.TodoEvent;


/**
 * 底部弹出式，用以输入将要生成的新事件标题
 */

public class EditMenuFragment extends DialogFragment implements View.OnClickListener{

    private ImageView saveButton;
    private EventsAdapter adapter;
    private FloatingActionButton add;
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

        // 当点击软件盘确定按钮时， 保存文本
        editEvent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(!editEvent.getText().toString().isEmpty()) {
                    saveButton.performClick();
                }
                else{
                    return true;
                }
                return false;
            }
        });

        // 设置editText获取焦点并自动弹出键盘
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if(editEvent!=null){
                    //设置可获得焦点
                    editEvent.setFocusable(true);
                    editEvent.setFocusableInTouchMode(true);
                    //请求获得焦点
                    editEvent.requestFocus();
                    //调用系统输入法
                    InputMethodManager inputManager = (InputMethodManager) editEvent.getContext().
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(editEvent, InputMethodManager.SHOW_IMPLICIT);
                }

            }
        });
        return dialog;
    }

    private static final String TAG = "EditMenuFragment";
    
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save:
                if (!editEvent.getText().toString().isEmpty()) {

                    Toast.makeText(getContext(), "Event save", Toast.LENGTH_SHORT).show();
                    TodoEvent event = new TodoEvent();
                    event.setEventName(editEvent.getText().toString()); // 设置事件的名称
                    event.setPos(adapter.getTodoEventSize()+1);         // 设置事件的位置

                    //set to default deadline
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DAY_OF_MONTH, 1);    // 获取明天的日期
                    c.set(Calendar.SECOND, 0);          // 设置秒为0
                    Date date = c.getTime();

                    event.setEventDeadline(date);

                    event.setEventDate();       // 设置事件年月日字符串
                    event.setEventTime();       // 设置事件时分字符串

                    event.setClicked(false);    // 设置为没被点击
                    event.save();               // 保存到数据库

                    int newItemPos = adapter.getItemCount();
                    adapter.getTodoEventList().add(event);
                    adapter.notifyItemInserted(newItemPos);  // 通知新的事件加入

                    View visibility = getActivity().findViewById(R.id.no_event_layout);
                    visibility.setVisibility(View.INVISIBLE);
                    this.dismiss();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().findViewById(R.id.add_event).setVisibility(View.VISIBLE); // 显示加号按钮
    }
}
