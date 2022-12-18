package com.example.sae32.logic;

import java.io.IOException;
import java.net.*;
import java.util.List;

public class Server extends AppObject{
    public ServerSocket socket;
    private Inet4Address ip;
    private ServerTask task;
    List<String> blacklist;

    public Server(int port){
        try {
            socket = new ServerSocket(port,100, usedIp);
            logger.info("Server setup done");
            logger.info("@IP/port: "+socket.getInetAddress().toString());
            System.out.println(socket.getInetAddress().getAddress());
            task = new ServerTask(this);
            task.run();
        }catch(IOException e){
            logger.warning("Server socket not created: "+e.getMessage());
        }

    }

    public void addClientHandler(Socket sock){
        new ClientHandler(sock);
    }
}
