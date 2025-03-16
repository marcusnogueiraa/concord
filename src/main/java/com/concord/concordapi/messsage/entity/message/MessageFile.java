package com.concord.concordapi.messsage.entity.message;

public class MessageFile implements Message{
    private String path;

    public MessageFile(String path){
        this.path = path;
    }
    public MessageFile(){
    }
    @Override
    public String getText() {
        return null;
    }

    @Override
    public String getPath() {
        return this.path;
    }
    
}
