package com.example.sae32.logic.Messaging;


import com.example.sae32.logic.AppObject;
import com.example.sae32.logic.Exceptions.MessageException;
import com.example.sae32.logic.Exceptions.MessagingException;
import com.example.sae32.logic.utils.RotatingList;

import java.util.ArrayList;
import java.util.List;

public  class Messaging extends AppObject{
    private RotatingList<Message> messages;
    private List<MessagingHandler> handlers;

    public Messaging(){
        messages = new RotatingList<>(250);
        handlers = new ArrayList<MessagingHandler>();
    }

    protected void addHandler(MessagingHandler handler){
        handlers.add(handler);
    }

    public void publishAll(){
        try {
            for (MessagingHandler handler : handlers) {
                for (Message message:messages) {
                    System.out.println(messages);
                    handler.publish(message.getPublishableString()+"\n");
                }
            }
        } catch (MessageException e) {
            e.printStackTrace();
        }
    }


    public Message getMsgMessage(String str){
        return new Message(str, MessageType.MSG);
    }

    public void publish(Message msg){
        try {
            String msgstr = msg.getPublishableString();
            messages.add(msg);
            for (MessagingHandler handler : handlers) {
                handler.publish(msgstr + "\n");
            }
        }catch(MessageException ignore){}
    }
}
