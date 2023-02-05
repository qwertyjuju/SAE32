package com.example.sae32.logic.Messaging.Message;

import com.example.sae32.logic.Exceptions.MessageException;
/* Interface commune pour tous les Messages (UDP et TCP)*/
public interface MessageInt {
    String getPublishableString() throws MessageException;
    MessageType getType();
    void setMsg(String msg);
}
