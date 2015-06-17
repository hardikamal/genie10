package com.getgenieapp.android.Objects;

/**
 * Created by Raviteja on 6/16/2015.
 */
public class MessageStatus
{
    private int sent;

    private int delivered;

    private int seen;

    public MessageStatus(int sent, int delivered, int seen) {
        this.sent = sent;
        this.delivered = delivered;
        this.seen = seen;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public int getDelivered() {
        return delivered;
    }

    public void setDelivered(int delivered) {
        this.delivered = delivered;
    }

    public int getSeen() {
        return seen;
    }

    public void setSeen(int seen) {
        this.seen = seen;
    }
}
