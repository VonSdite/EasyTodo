package xyz.wendyltanpcy.myapplication.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * 数据库表类事件主类
 */

public class TodoEvent extends DataSupport implements Serializable{

    /*
    basic info
     */
    private long id;
    private String eventName;
    private String eventDetail;
    private boolean eventFinish;

    /*
    time info
     */
    private Date eventDeadLine;
    private Calendar eventCalendar;
    private String eventTime;
    private String eventDate;

    /*
    image info
     */
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

    public Calendar getEventCalendar() {
        return eventCalendar;
    }

    public void setEventCalendar(Calendar calendar) {
        Calendar newC = Calendar.getInstance();

        newC.setTime(this.eventDeadLine);
        newC.set(newC.get(Calendar.YEAR),newC.get(Calendar.MONTH),newC.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));

        this.eventCalendar = newC;
    }


    public void setEventDate() {
        int year = eventCalendar.get(Calendar.YEAR);
        int month = eventCalendar.get(Calendar.MONTH);
        int day = eventCalendar.get(Calendar.DAY_OF_MONTH);
        StringBuilder builder = new StringBuilder().append(year)
                .append("年").append(month+1).append("月").append(day).append("日");
        this.eventDate = builder.toString();
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventTime() {
        int hour = eventCalendar.get(Calendar.HOUR_OF_DAY);
        int min = eventCalendar.get(Calendar.MINUTE);
        StringBuilder builder = new StringBuilder().append(hour)
                .append("时").append(min).append("分");
        this.eventTime = builder.toString();
    }

    public String getEventTime() {
        return eventTime;
    }


    public Date getEventDeadLine() {
        return eventDeadLine;
    }

    public void setEventDeadLine(Date eventDeadLine) {
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
