package xyz.wendyltanpcy.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import xyz.wendyltanpcy.myapplication.model.TodoEvent;

public class eventContentActivity extends AppCompatActivity {

    private String date_return;
    private FloatingActionButton saveDetailButton;
    private ImageView chooseDate;
    private ImageView chooseAlarm;
    private TextView eventNameText ;
    private TextView eventDetailText ;
    private TextView eventDeadLineText ;
    public static  TodoEvent event;
    private static boolean action_status;

    public static void actionStart(Context context,TodoEvent event){
        Intent intent = new Intent(context,eventContentActivity.class);
        intent.putExtra("event",event);
        context.startActivity(intent);

    }

    public static TodoEvent actionEnd(){
        return event;
    }

    public static boolean actionStatus(){
        return action_status;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_content);
        if(getSupportActionBar() != null){
            // Enable the Up button
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        event = (TodoEvent) getIntent().getSerializableExtra("event");

        Toast.makeText(getApplicationContext(),event.getEventName(),Toast.LENGTH_LONG).show();

        final eventContentFragment eventContentFragment = (eventContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.news_content_fragment);
        eventContentFragment.refresh(event);

        chooseDate = (ImageView) findViewById(R.id.choose_date);
        chooseAlarm = (ImageView) findViewById(R.id.choose_alarm);
        eventNameText = (TextView) findViewById(R.id.event_name);
        eventDetailText = (TextView) findViewById(R.id.event_detail);
        eventDeadLineText = (TextView) findViewById(R.id.event_deadline);


        saveDetailButton = (FloatingActionButton) findViewById(R.id.save_detail_button);
        chooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(eventContentActivity.this,PickDateActvity.class);
                startActivityForResult(i,1);
            }
        });
        saveDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.setEventName(eventNameText.getText().toString());
                event.setEventDetail(eventDetailText.getText().toString());
                event.setEventDeadLine(eventDeadLineText.getText().toString());
                Toast.makeText(getApplicationContext(),"Detail save!",Toast.LENGTH_SHORT).show();
                action_status = true;
                finish();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if (resultCode==RESULT_OK) {
                    date_return = data.getStringExtra("date_return");
                    eventDeadLineText.setText(date_return);
                }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
