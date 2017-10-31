package xyz.wendyltanpcy.easytodo.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 完成事件主类
 */

public class FinishEvent extends DataSupport implements Serializable {

    private String eventName;       // 完成事件的名称
    private String eventFinishDate; // 完成事件的时间

    public void setEventFinishDate(String eventFinishDate) {
        this.eventFinishDate = eventFinishDate;
    }

    public String getEventFinishDate() {
        return eventFinishDate;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

}
