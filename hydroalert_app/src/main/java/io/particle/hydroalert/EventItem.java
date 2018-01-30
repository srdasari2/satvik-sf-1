package io.particle.hydroalert;

import java.util.Date;

/**
 * Created by qz2zvk on 4/10/17.
 */

public class EventItem {

    private int message;
    private Date messageTime;


    public EventItem() {
    }

    public EventItem(int message, Date messageTime) {
        this.message = message;
        this.messageTime = messageTime;

    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public Date getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }




}
