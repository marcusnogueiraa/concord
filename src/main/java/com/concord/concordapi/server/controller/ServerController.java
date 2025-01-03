package com.concord.concordapi.server.controller;

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

import com.concord.concordapi.server.dto.request.ServerCreateBodyDTO;
import com.concord.concordapi.server.dto.request.ServerPutBodyDTO;
import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.server.service.ServerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    @Autowired
    private ServerService serverService;
    

    @GetMapping("/{id}")
    public ResponseEntity<ServerDto> getById(@PathVariable Long id){
        ServerDto server = serverService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(server);
    }

    @PostMapping
    public ResponseEntity<ServerDto> create(@RequestBody @Valid ServerCreateBodyDTO server) {
        ServerDto createdServer = serverService.create(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        serverService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDto> updateById(@RequestBody @Valid ServerPutBodyDTO server, @PathVariable Long id) {
        ServerDto updatedServer = serverService.updateById(id, server);
        return ResponseEntity.status(HttpStatus.OK).body(updatedServer); 
    }

    @PostMapping("/{serverId}/subscribe/{userId}")
    public ResponseEntity<Void> subscribeServer(@PathVariable Long serverId, @PathVariable Long userId) {
        serverService.subscribeUser(userId, serverId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
