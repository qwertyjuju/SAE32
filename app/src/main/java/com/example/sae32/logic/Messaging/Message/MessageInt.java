package com.example.sae32.logic.Messaging.Message;

import com.example.sae32.logic.Exceptions.MessageException;
import com.example.sae32.logic.Messaging.MessageType;

public interface MessageInt {
    String getPublishableString() throws MessageException;
    MessageType getType();
    void setMsg(String msg);
}
