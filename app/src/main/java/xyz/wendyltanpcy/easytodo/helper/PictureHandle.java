package xyz.wendyltanpcy.easytodo.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.squareup.picasso.Picasso;

import xyz.wendyltanpcy.easytodo.R;


/**
 * Show pic at EventContentActivity
 */

public class PictureHandle extends AppCompatActivity {

    private PinchImageView mImageView;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_handle);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get real file path which have been stored
        mImageView = findViewById(R.id.handle_pic);
        Intent i = getIntent();
        String path = i.getStringExtra("path");
        Uri uri = Uri.parse(path);
        String realPath = "file://" + uri;

        //get pic name
        String[] part= path.split("/");
        String title = part[5];

        //只能用毕加索搭配pinchimage，glide不兼容
        Picasso.with(this)
                .load(realPath)
                .placeholder(R.mipmap.icon2)
                .into(mImageView);

        ActionBar actionBar =  getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(title);
        }



    }
    public static void actionStart(Context context, String photoPath){
        Intent i = new Intent(context,PictureHandle.class);
        i.putExtra("path",photoPath);
        context.startActivity(i);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return false;
    }
}
