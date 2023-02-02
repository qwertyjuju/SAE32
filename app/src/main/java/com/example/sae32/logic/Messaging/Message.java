package com.example.sae32.logic.Messaging;

import androidx.annotation.NonNull;

import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Exceptions.MessageException;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private String version;
    private String sender;
    private JSONObject json;
    private MessageType type;
    private boolean valid=false;
    private boolean publishable=true;
    private String publishablestr;

    Message(MessageType msgtype, String msgsender){
        version = AppObject.VERSION.toString();
        type = msgtype;
        sender = msgsender;
        json = new JSONObject();
        try {
            json.put("sender", sender);
            json.put("msgtype", msgtype);
            json.put("version", version);
        } catch (JSONException ignore) {}
    }

    Message(String msg, MessageType awaitedMessageType){
        try{
            json = new JSONObject(msg);
            type= MessageType.valueOf(json.getString("msgtype"));
            sender = json.getString("sender");
            publishablestr= sender+": ";
            if (type ==awaitedMessageType) {
                switch (type) {
                    case ACK:
                    case MSG:
                        publishablestr+=json.getString("msg");
                        break;
                    case INIT:
                        publishablestr += type.toString() + "new connection accepted";
                        break;
                    case ALERT:
                        publishablestr += "ALERT: " + json.getString("alert");
                        break;
                    default:
                        publishable = false;
                }
                valid=true;
            }
            else{
                publishablestr+="Not awaited message";
            }
        }catch(JSONException ignore){}
    }

    public void setMsg(String msg){
        try {
            json.put("msg", msg);
        } catch (JSONException ignore){}
    }
    public boolean isValid(){
        return valid;
    }

    public String getPublishableString() throws MessageException {
        if(!publishable){
            throw new MessageException();
        }
        return publishablestr;
    }

    @NonNull
    public String toString(){
        return json.toString();
    }
}
