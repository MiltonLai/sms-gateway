package com.rockbb.sms.bo;

import java.io.Serializable;

public class SmsMessage implements Serializable {
    private String id;
    private String merId;
    private String recipient;
    private String content;

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getMerId() {return merId;}
    public void setMerId(String merId) {this.merId = merId;}
    public String getRecipient() {return recipient;}
    public void setRecipient(String recipient) {this.recipient = recipient;}
    public String getContent() {return content;}
    public void setContent(String content) {this.content = content;}
}
