package xyz.wendyltanpcy.easytodo.TodoList;

/**
 * Created by Wendy on 2017/10/7.
 */

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

import xyz.wendyltanpcy.easytodo.R;
import xyz.wendyltanpcy.easytodo.helper.ColorManager;
import xyz.wendyltanpcy.easytodo.helper.MyApplication;
import xyz.wendyltanpcy.easytodo.model.ThemeColor;


public class MotiveActivity extends AppCompatActivity {

    private final int[] layouts = { R.id.skin_01, R.id.skin_02, R.id.skin_03,
            R.id.skin_04, R.id.skin_05 };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.theme_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.title_back);
        }

        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return false;
    }

    private void initView() {
        int colorArr[] = ColorManager.getInstance().getSkinColor(this);
        for (int i = 0; i < layouts.length; i++) {
            View view = findViewById(layouts[i]);
            View color = view.findViewById(R.id.motive_item_color);
            View selected = view.findViewById(R.id.motive_item_selected);
            color.setBackgroundColor(colorArr[i]);
            if (colorArr[i] == MyApplication.getInstance(this).getSkinColorValue()) {
                selected.setVisibility(View.VISIBLE);
            }
            color.setOnClickListener(new OnSkinColorClickListener(i));
        }
    }

    class OnSkinColorClickListener implements OnClickListener {

        private int position;

        public OnSkinColorClickListener(int position) {
            this.position = position;
        }

        public void storeAndSaveColor(){
            LitePal.getDatabase();
            List<ThemeColor> colorList = DataSupport.findAll(ThemeColor.class);
            ThemeColor color = colorList.get(0);
            if (color!=null){
                color.setColor(ColorManager.getInstance().getStoreColor());
                color.save();
            }else{
                color = new ThemeColor();
                color.setColor(ColorManager.getInstance().getStoreColor());
                color.save();
            }
        }

        @Override
        public void onClick(View v) {
            for (int i = 0; i < layouts.length; i++) {
                View view = findViewById(layouts[i]);
                View selected = view.findViewById(R.id.motive_item_selected);
                selected.setVisibility(i == position ? View.VISIBLE : View.GONE);
                ColorManager.getInstance().setSkinColor(MotiveActivity.this,
                        position);
                storeAndSaveColor();


            }
        }
    }

}
