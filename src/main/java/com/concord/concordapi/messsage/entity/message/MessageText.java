package com.concord.concordapi.messsage.entity.message;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class MessageText implements Message{
    private String text;
    public MessageText(String text){
        this.text = text;
    }
    public MessageText(){
    }
    @Override
    public String getText() {
        return this.text;
    }

    @JsonIgnore
    @Override
    public String getPath() {
        return null;
    }
}
