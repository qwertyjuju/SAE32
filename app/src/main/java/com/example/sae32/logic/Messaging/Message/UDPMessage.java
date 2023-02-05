package com.example.sae32.logic.Messaging.Message;

import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Exceptions.MessageException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.UUID;

public class UDPMessage implements Serializable, MessageInt {
    private String version;
    private MessageType type;
    private UUID senderID;
    private String sender=null;
    private String msg=null;
    transient ByteArrayOutputStream buffer;
    transient InetAddress senderIp;
    transient int senderPort;

    public UDPMessage(MessageType msgType, UUID id, String sendername){
        version=AppObject.VERSION.toString();
        type=msgType;
        senderID=id;
        sender=sendername;
    }

    public void serialize() throws IOException {
        buffer = new ByteArrayOutputStream();
        ObjectOutputStream objout = new ObjectOutputStream(buffer);
        objout.writeObject(this);
        objout.flush();
        objout.close();
    }

    public static UDPMessage deSerialize(DatagramPacket packet) throws IOException, ClassNotFoundException {
        ByteArrayInputStream instream = new ByteArrayInputStream(packet.getData());
        ObjectInputStream objin = new ObjectInputStream(instream);
        UDPMessage msg = (UDPMessage) objin.readObject();
        msg.senderIp=packet.getAddress();
        msg.senderPort=packet.getPort();
        objin.close();
        return msg;
    }
    public void setMsg(String msg){
        this.msg = msg;
    }
    public byte[] getBuffer(){
        return buffer.toByteArray();
    }
    public int getBufferSize(){
        return buffer.size();
    }
    public UUID getSenderID(){
        return senderID;
    }
    public InetAddress getSenderIp(){
        return senderIp;
    }
    public int getSenderPort(){
        return senderPort;
    }

    @Override
    public String getPublishableString() throws MessageException {
        if(type!=MessageType.MSG&&type!=MessageType.ACK&&type!=MessageType.CLOSING) {
            throw new MessageException();
        }else{
            return sender+": "+msg;
        }
    }

    @Override
    public MessageType getType() {
        return type;
    }
}
