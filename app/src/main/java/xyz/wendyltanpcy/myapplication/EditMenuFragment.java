package xyz.wendyltanpcy.myapplication;


import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by Wendy on 2017/9/16.
 */

public class EditMenuFragment extends DialogFragment implements View.OnClickListener{

    private ImageView calender;
    private EditText editEvent;
    private ImageView saveButton;

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

        editEvent = dialog.findViewById(R.id.edit_event);
        calender =  dialog.findViewById(R.id.pick_date);
        saveButton =  dialog.findViewById(R.id.save);

       editEvent.setOnClickListener(this);
        calender.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        return dialog;
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pick_date:
                Intent i = new Intent(getActivity(),PickDateActvity.class);
                startActivity(i);
                break;
            case R.id.save:
                Toast.makeText(this.getActivity(),"text save!",Toast.LENGTH_SHORT).show();
                this.dismiss();
                break;
            default:
                break;
        }
    }
}
