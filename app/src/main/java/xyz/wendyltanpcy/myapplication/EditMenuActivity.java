package xyz.wendyltanpcy.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

import xyz.wendyltanpcy.myapplication.Adapter.EventsAdapter;
import xyz.wendyltanpcy.myapplication.model.TodoEvent;

/**
 * Created by Wendy on 2017/9/16.
 */

public class EditMenuActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_bar_activity);

    }
}
