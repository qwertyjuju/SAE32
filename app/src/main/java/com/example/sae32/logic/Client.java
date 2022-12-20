package com.example.sae32.logic;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends AppObject{
    public Socket socket;

    Client(InetAddress ip, int port){
        try {
            socket = new Socket(ip, port);
            logger.info("Connected to ");
        }catch(IOException e){
            logger.warning("Socket not created: "+e.getMessage());
        }
    }
}
