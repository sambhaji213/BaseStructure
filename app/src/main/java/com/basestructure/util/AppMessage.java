package com.basestructure.util;

import android.content.Context;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

/**
 * @author sandeep on 21-July-2015
 */
public class AppMessage {
    private String key ;
    private String message;
    private String longMessage;
    private MessageTypeEnum messageType;
    private JSONObject response;

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }

    public enum MessageTypeEnum {
        SUCCESS(0), WARNING(1), ERROR(2), INFO(3), SUGGESTION(4), PURGED(5), SERVER_ERROR(6),
        NETWORK_ERROR(7);

        private int statusCode;

        MessageTypeEnum(int i) {
            statusCode = i;
        }

        public int valueOf(int status){ return statusCode; }
    }

    public AppMessage(MessageTypeEnum messageType, String key, String message, String longMessage) {
        this.messageType=messageType;
        this.key = key;
        this.message= StringUtils.trimToEmpty(message);
        this.longMessage= StringUtils.trimToEmpty(longMessage);
    }

    public AppMessage(MessageTypeEnum messageType, String key, String message) {
        this.messageType=messageType;
        this.key = key;
        this.message= StringUtils.trimToEmpty(message);
        this.longMessage= StringUtils.trimToEmpty(longMessage);
    }

    public AppMessage(MessageTypeEnum messageType, String message) {
        this.messageType=messageType;
        this.key = "message";
        this.message= StringUtils.trimToEmpty(message);
        this.longMessage= StringUtils.trimToEmpty(longMessage);
    }

    public AppMessage(String message) {
        this.messageType= MessageTypeEnum.ERROR;
        this.key = "message";
        this.message= StringUtils.trimToEmpty(message);
        this.longMessage= StringUtils.trimToEmpty(longMessage);
    }
    public String getKey() {
        return key;
    }

    public String getMessage() {
        return message;
    }

    public String getLongMessage() {
        return longMessage;
    }

    public MessageTypeEnum getMessageType() {
        return messageType;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
    }

    public void setLongMessage(String longMessage) {
        this.longMessage = longMessage;
    }

    public void setMessage(String message) {
        this.message = message;
        this.longMessage = message;
    }

    public void setShortMessage(String message) {
        this.message = message;
    }

    public void showMessageDlg(Context context, String title){
        if(!longMessage.isEmpty())
            AppAndroidUtils.hxInformationDlg(context, title, longMessage);
        else
            AppAndroidUtils.hxInformationDlg(context, title, message);
    }

    public void showToastMessage(Context context) {
        showToastMessage(context, "");
    }

    public void showToastMessage(Context context, String defaultMsg) {
        if(!longMessage.isEmpty())
            Toast.makeText(context, longMessage, Toast.LENGTH_SHORT)
                    .show();
        else{
            Toast.makeText(context, defaultMsg, Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
