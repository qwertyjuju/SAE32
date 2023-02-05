package com.example.sae32.logic.Messaging;


import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Exceptions.MessageException;
import com.example.sae32.logic.Messaging.Message.MessageInt;
import com.example.sae32.logic.Messaging.Message.TCPMessage;
import com.example.sae32.logic.Messaging.Message.UDPMessage;
import com.example.sae32.logic.utils.RotatingList;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public  class Messaging extends AppObject{
    private RotatingList<MessageInt> messages;
    private List<MessagingHandler<?>> handlers;

    public Messaging(){
        super();
        messages = new RotatingList<>(250);
        handlers = new ArrayList<MessagingHandler<?>>();
    }

    protected void addHandler(MessagingHandler handler){
        handlers.add(handler);
    }

    public void publishAll(){
        try {
            for (MessagingHandler handler : handlers) {
                for (MessageInt message : messages) {
                    handler.publish(message.getPublishableString()+"\n");
                }
            }
        } catch (MessageException ignore) {}
    }

    public TCPMessage getTCPMessageFromStr(String str){
        return new TCPMessage(str);
    }
    public UDPMessage getUDPMessageFromPacket(DatagramPacket packet) throws IOException, ClassNotFoundException {
        return UDPMessage.deSerialize(packet);
    }
    public TCPMessage createTCPMessage(MessageType msgt, String sender){
        return new TCPMessage(msgt, sender);
    }
    public UDPMessage createUDPMessage(MessageType msgt, UUID senderid, String sender){
        return new UDPMessage(msgt,senderid, sender);
    }
    public TCPMessage createTCPMessage(MessageType msgt, String sender, String msg){
        TCPMessage tcpm = new TCPMessage(msgt, sender);
        tcpm.setMsg(msg);
        return tcpm;
    }
    public UDPMessage createUDPMessage(MessageType msgt,UUID senderid, String sender, String msg){
        UDPMessage udpm=  new UDPMessage(msgt, senderid, sender);
        udpm.setMsg(msg);
        return udpm;
    }
    public void publish(MessageInt msg){
        try {
            String msgstr = msg.getPublishableString();
            messages.add(msg);
            for (MessagingHandler handler : handlers) {
                handler.publish(msgstr + "\n");
            }
        }catch(MessageException ignore){}
    }

}
