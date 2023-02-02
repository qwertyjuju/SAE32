package com.example.sae32.logic.Messaging;

import android.widget.TextView;

import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Exceptions.MessagingException;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Tasks.ClientTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;

public class ClientMessaging extends Messaging{
    private ClientTask client=null;

    public ClientTask createClient(InetAddress ip, int port,String clientName) {
        if (client != null) {
            client.kill();
        }
        try {
            client = new ClientTask(ip, port, clientName);
            client.run();
            return client;
        } catch (TaskException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

    public Message createInitMessage(){
        return new Message(MessageType.INIT, client.getName());
    }

    public Message getInitMessage(String msg){
        return new Message(msg, MessageType.ACK);
    }
    public Message createMsgMessage(String str){
        Message msg = new Message(MessageType.MSG, client.getName());
        msg.setMsg(str);
        return msg;
    }

    public void sendMessage(String msg){
        try{
            client.addMessage(msg);
        }catch(NullPointerException e){
            logger.warning("client not created, can't send message");
        }
    }
}
