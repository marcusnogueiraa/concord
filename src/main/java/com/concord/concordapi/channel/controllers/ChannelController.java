package com.concord.concordapi.channel.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.channel.dtos.ChannelRequestBodyDTO;
import com.concord.concordapi.channel.entities.Channel;
import com.concord.concordapi.channel.services.ChannelService;

@RestController
@RequestMapping("/api")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    @GetMapping("/channel/{id}")
    public ResponseEntity<Channel> get(@PathVariable Long id){
        Channel channel = channelService.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @PostMapping("/channel")
    public ResponseEntity<Channel> create(@RequestBody ChannelRequestBodyDTO channel) {
        Channel createdChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel); 
    }
}
