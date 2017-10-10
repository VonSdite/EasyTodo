package xyz.wendyltanpcy.myapplication.model;

import org.litepal.annotation.Column;
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
    @Column(unique = true)
    private long id;

    @Column(nullable = false)
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
    priority
     */
    private int eventDateNum;
    private String eventPriority;
    private boolean eventExpired;

    /*
    image info
     */
    private byte[] eventImageBitMap;

    public void setEventPriority() {
        Calendar calendar = Calendar.getInstance();
        int year =calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        StringBuilder builder2 = new StringBuilder().append(year).append(month).append(day);
        int eventNum = Integer.parseInt(builder2.toString());
        if (eventDateNum==eventNum){
            eventPriority = "高";
            setEventExpired(false);
        }else if (eventDateNum>eventNum&&eventDateNum<=eventNum+3){
            eventPriority = "中";
            setEventExpired(false);
        }else if (eventDateNum>eventNum+3){
            eventPriority = "低";
            setEventExpired(false);
        }else if (eventDateNum<eventNum){
            eventPriority = "已过期";
            setEventExpired(true);
        }



    }

    public void setEventExpired(boolean eventExpired) {
        this.eventExpired = eventExpired;
    }

    public boolean isEventExpired() {
        return eventExpired;
    }

    public String getEventPriority() {
        return eventPriority;
    }

    public int getEventDateNum() {
        return eventDateNum;
    }

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
        calendar.set(Calendar.SECOND,0);
        newC.set(newC.get(Calendar.YEAR),newC.get(Calendar.MONTH),newC.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));
        newC.set(Calendar.MILLISECOND,0);

        this.eventCalendar = newC;
    }


    public void setEventDate() {
        int year = eventCalendar.get(Calendar.YEAR);
        int month = eventCalendar.get(Calendar.MONTH)+1;
        int day = eventCalendar.get(Calendar.DAY_OF_MONTH);
        StringBuilder builder1 = new StringBuilder().append(year)
                .append("年").append(month).append("月").append(day).append("日");

        this.eventDate = builder1.toString();
        StringBuilder builder2 = new StringBuilder().append(year).append(month).append(day);
        eventDateNum = Integer.parseInt(builder2.toString());
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
