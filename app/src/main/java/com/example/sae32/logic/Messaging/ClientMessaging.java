package com.example.sae32.logic.Messaging;

import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message.TCPMessage;
import com.example.sae32.logic.Tasks.ClientHandler;
import com.example.sae32.logic.Tasks.ClientTask;
import com.example.sae32.logic.utils.ConnectionType;

import java.net.InetAddress;

public class ClientMessaging extends Messaging{
    private ClientTask client=null;
    public ClientMessaging(){super();};

    public ClientTask createClient(InetAddress ip, int port,String clientName, ConnectionType coType) {
        if (client != null) {
            client.kill();
        }
        try {
            client = new ClientTask(ip, port, clientName, coType);
            client.run();
            return client;
        } catch (TaskException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

    public void sendMessage(String msg){
        try{
            client.addMessage(msg);
        }catch(NullPointerException e){
            logger.warning("client not created, can't send message");
        }
    }
}
