package xyz.wendyltanpcy.myapplication.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Wendy on 2017/9/6.
 */

public class TodoEvent extends DataSupport implements Serializable{

    private String eventName;
    private String eventDetail;
    private boolean eventFinish;
    private String eventDeadLine;
    private long id;
    private byte[] eventImageBitMap;


    public byte[] getEventImageBitMap() {
        return eventImageBitMap;
    }

    public void setEventImageBitMap(byte[] eventImageBitMap) {
        this.eventImageBitMap = eventImageBitMap;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getEventDeadLine() {
        return eventDeadLine;
    }

    public void setEventDeadLine(String eventDeadLine) {
        this.eventDeadLine = eventDeadLine;
    }


    public boolean isEventFinish() {
        return eventFinish;
    }

    public void setEventFinish(boolean eventFinish) {
        this.eventFinish = eventFinish;
    }


    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }


}
