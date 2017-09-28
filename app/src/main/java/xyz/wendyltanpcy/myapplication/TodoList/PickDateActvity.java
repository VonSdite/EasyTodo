package xyz.wendyltanpcy.myapplication.TodoList;

import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import xyz.wendyltanpcy.myapplication.R;

/**
 * 选择日期界面
 */

public class PickDateActvity extends AppCompatActivity{



    private PickDateFragment mPickDateFragment;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_date_activity);

        FragmentManager fm = getSupportFragmentManager();
        mPickDateFragment = (PickDateFragment)fm.findFragmentById(R.id.pd_fragment);

        if(mPickDateFragment == null )
        {
            mPickDateFragment = new PickDateFragment();
            fm.beginTransaction().add(R.id.pd_activity,mPickDateFragment).commit();
        }

    }
}
