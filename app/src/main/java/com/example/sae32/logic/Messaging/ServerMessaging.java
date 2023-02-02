package com.example.sae32.logic.Messaging;

import android.widget.TextView;

import com.example.sae32.logic.Exceptions.MessagingException;
import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Tasks.ServerTask;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerMessaging extends Messaging{
    private ServerTask server=null;
    public ServerMessaging(){super();}

    public ServerTask createServer(int port) {
        if (server != null) {
            server.kill();
        }
        try {
            server = new ServerTask(port, ServerTask.ServerType.TCP);
            server.run();
            return server;
        } catch (TaskException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

    public Message getInitMessage(String initmsg){
        return new Message(initmsg, MessageType.INIT);
    }
    public Message createInitMessageResponse(Message initmsg){
        if (initmsg.isValid()) {
            Message resp = new Message(MessageType.ACK, server.getName());
            resp.setMsg(server.getWelcomeMessage());
            return resp;
        }
        else{
            Message resp = new Message(MessageType.CLOSING, server.getName());
            resp.setMsg("init message not valid, closing");
            return resp;
        }
    }

}
