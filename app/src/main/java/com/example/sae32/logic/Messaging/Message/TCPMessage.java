package com.example.sae32.logic.Messaging.Message;

import androidx.annotation.NonNull;

import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Exceptions.MessageException;
import com.example.sae32.logic.Messaging.MessageType;

import org.json.JSONException;
import org.json.JSONObject;

public class TCPMessage implements MessageInt {
    private String version;
    private String sender;
    private JSONObject json;
    private MessageType type;
    private boolean valid=false;
    private boolean publishable=true;
    private String publishablestr;

    public TCPMessage(MessageType msgtype, String msgsender){
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

    public TCPMessage(String msg){
        try{
            json = new JSONObject(msg);
            type= MessageType.valueOf(json.getString("msgtype"));
            sender = json.getString("sender");
            publishablestr= sender+": ";
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
                case CLOSING:
                    try {
                        String reason = json.getString("msg");
                        publishablestr+="CLOSING: "+reason;
                    }catch(JSONException ignore){
                        publishablestr+="CLOSING";
                    }
                    break;
                default:
                    publishable = false;
            }
            valid=true;
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void setMsg(String msg){
        try {
            json.put("msg", msg);
        } catch (JSONException ignore){}
    }
    public boolean isValid(){
        return valid;
    }

    public MessageType getType() {
        return type;
    }

    public String getPublishableString() throws MessageException {
        if(!publishable){
            throw new MessageException();
        }
        return publishablestr;
    }

    public Boolean equals(MessageType msgType){
        return type==msgType;
    }
    @NonNull
    public String toString(){
        return json.toString();
    }
}
