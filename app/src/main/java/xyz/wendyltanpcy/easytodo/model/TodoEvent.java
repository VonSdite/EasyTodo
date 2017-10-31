package xyz.wendyltanpcy.easytodo.model;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 数据库表类事件主类
 */
public class TodoEvent extends DataSupport implements Serializable, Comparable<TodoEvent>{

//    @Column(nullable = false)
    private String eventName;        // 事件的名称
    private String eventDetail;      // 事件的详情

    private Date eventDeadline;      // 事件的deadline 这是个Date类型

    private String eventDate;        // 事件的年月日字符串
    private String eventTime;        // 事件的时分字符串

    private byte[] eventImageBitMap; // 事件的照片

    private int pos;  // 标记是item的第几项, 显示的时候按这个顺序显示出来

    private boolean isClicked;  // 标记是否被点击了

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean isClicked() {
        return isClicked;
    }

    // deadline的日期
    public void setEventDeadline(Date eventDeadline) {
        this.eventDeadline = eventDeadline;
    }

    public Date getEventDeadline() {
        return eventDeadline;
    }


    // deadline的年月日
    public void setEventDate() {
        SimpleDateFormat format1 = new SimpleDateFormat("YYYY年MM月dd日");
        this.eventDate = format1.format(this.eventDeadline);

    }

    public String getEventDate() {
        return eventDate;
    }

    // deadline的时分
    public void setEventTime() {
        SimpleDateFormat format1 = new SimpleDateFormat("HH时mm分");
        this.eventTime = format1.format(this.eventDeadline);

    }

    public String getEventTime() {
        return eventTime;
    }

    // event的照片
    public byte[] getEventImageBitMap() {
        return eventImageBitMap;
    }

    public void setEventImageBitMap(byte[] eventImageBitMap) {
        this.eventImageBitMap = eventImageBitMap;
    }

    // event的详情
    public void setEventDetail(String eventDetail) {
        this.eventDetail = eventDetail;
    }

    public String getEventDetail() {
        return eventDetail;
    }

    // event的名称
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return eventName;
    }

    // event作为item的位置
    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getPos() {
        return pos;
    }

    // 用于排序的
    @Override
    public int compareTo(@NonNull TodoEvent todoEvent) {
        return this.getPos() - todoEvent.getPos();
    }

    public boolean isEventExpired(){
        Date date = new Date();
        return date.compareTo(eventDeadline) > 0;
    }
}
