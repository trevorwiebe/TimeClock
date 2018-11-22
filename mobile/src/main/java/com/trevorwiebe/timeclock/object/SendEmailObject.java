package com.trevorwiebe.timeclock.object;

public class SendEmailObject {

    public static final int SEND_WITH_TEXT_MESSAGE = 0;
    public static final int SEND_WITH_TELEGRAM = 1;
    public static final int SEND_WITH_EMAIL = 2;

    private int sendMethod;
    private String whenToSend;
    private String email;
    private String phoneNumber;
    private long whenToSendNext;

    public SendEmailObject(int sendMethod, String whenToSend, String email, String phoneNumber, long whenToSendNext) {
        this.sendMethod = sendMethod;
        this.whenToSend = whenToSend;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.whenToSendNext = whenToSendNext;
    }

    public int getSendMethod() {
        return sendMethod;
    }

    public void setSendMethod(int sendMethod) {
        this.sendMethod = sendMethod;
    }

    public String getWhenToSend() { return whenToSend; }

    public void setWhenToSend(String whenToSend) {
        this.whenToSend = whenToSend;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getWhenToSendNext() {
        return whenToSendNext;
    }

    public void setWhenToSendNext(long whenToSendNext) {
        this.whenToSendNext = whenToSendNext;
    }
}
