package com.concord.concordapi.messsage.entity.message;

public class MessageImage implements Message{
    private String text;
    private String path;

    public MessageImage(String text, String path){
        this.text = text;
        this.path = path;
    }
    public MessageImage(){
    }
    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
