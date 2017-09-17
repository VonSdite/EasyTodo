package xyz.wendyltanpcy.myapplication.model;

import java.util.Calendar;

/**
 * Created by Wendy on 2017/9/6.
 */
//实体类里面有属性，和设置方法

public class TodoEvent {
    private String eventName;
    private String eventDetail;
    private boolean eventFinish;
    private String eventDeadLine;


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
