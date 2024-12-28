package com.concord.concordapi.channel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.channel.dto.ChannelRequestBodyDTO;
import com.concord.concordapi.channel.entity.Channel;
import com.concord.concordapi.channel.service.ChannelService;

import jakarta.validation.Valid;

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
    public ResponseEntity<Channel> create(@RequestBody @Valid ChannelRequestBodyDTO channel) {
        Channel createdChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel); 
    }

    @DeleteMapping("/channel/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/channel/{id}")
    public ResponseEntity<Channel> update(@RequestBody @Valid ChannelRequestBodyDTO channel, @PathVariable Long id) {
        Channel updatedChannel = channelService.update(id, channel);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedChannel); 
    }
}
