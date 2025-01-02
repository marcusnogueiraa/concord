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

import com.concord.concordapi.channel.dto.ChannelDTO;
import com.concord.concordapi.channel.dto.ChannelPutBodyDTO;
import com.concord.concordapi.channel.dto.ChannelCreateBodyDTO;
import com.concord.concordapi.channel.service.ChannelService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ChannelController {
    @Autowired
    private ChannelService channelService;

    @GetMapping("/channels/{id}")
    public ResponseEntity<ChannelDTO> get(@PathVariable Long id){
        ChannelDTO channel = channelService.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(channel);
    }

    @PostMapping("/channels")
    public ResponseEntity<ChannelDTO> create(@RequestBody @Valid ChannelCreateBodyDTO channel) {
        ChannelDTO createdChannel = channelService.create(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChannel); 
    }

    @DeleteMapping("/channels/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        channelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/channels/{id}")
    public ResponseEntity<ChannelDTO> update(@RequestBody @Valid ChannelPutBodyDTO channel, @PathVariable Long id) {
        ChannelDTO updatedChannel = channelService.update(id, channel);
        return ResponseEntity.status(HttpStatus.OK).body(updatedChannel); 
    }
}
