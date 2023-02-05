package com.example.sae32.logic.Messaging;

import com.example.sae32.logic.Exceptions.TaskException;
import com.example.sae32.logic.Messaging.Message.TCPMessage;
import com.example.sae32.logic.Tasks.ServerTask;
import com.example.sae32.logic.utils.ConnectionType;

public class ServerMessaging extends Messaging{
    private ServerTask server=null;
    public ServerMessaging(){super();}

    public ServerTask createServer(int port, ConnectionType coType, String servname) {
        if (server != null) {
            server.kill();
        }
        try {
            server = new ServerTask(port, coType, servname);
            server.run();
            return server;
        } catch (TaskException e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

}
