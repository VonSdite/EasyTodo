package xyz.wendyltanpcy.myapplication.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Wendy on 2017/9/28.
 */

public class FinishEvent extends DataSupport implements Serializable {

    private String eventName;
    private long id;
    private String eventFinishDate;
    private String eventAlarmTime;

    public void setEventFinishDate(String eventFinishDate) {
        this.eventFinishDate = eventFinishDate;
    }

    public void setEventAlarmTime(String eventAlarmTime) {
        this.eventAlarmTime = eventAlarmTime;
    }


    public String getEventFinishDate() {
        return eventFinishDate;
    }


    public String getEventAlarmTime() {
        return eventAlarmTime;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

}
