package com.concord.concordapi.messsage.entity.message;

public class MessageFile implements Message{
    private String text;
    private String path;

    public MessageFile(String text, String path){
        this.text = text;
        this.path = path;
    }
    public MessageFile(){
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
