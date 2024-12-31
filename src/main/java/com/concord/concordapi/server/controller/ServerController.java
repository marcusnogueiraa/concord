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

import com.concord.concordapi.server.dto.ServerPutBodyDTO;
import com.concord.concordapi.server.dto.ServerCreateBodyDTO;
import com.concord.concordapi.server.dto.ServerDTO;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.service.ServerService;
import com.concord.concordapi.user.dto.UserRequestDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    @Autowired
    private ServerService serverService;
    

    @GetMapping("/{id}")
    public ResponseEntity<ServerDTO> getById(@PathVariable Long id){
        ServerDTO server = serverService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(server);
    }

    @PostMapping
    public ResponseEntity<ServerDTO> create(@RequestBody @Valid ServerCreateBodyDTO server) {
        ServerDTO createdServer = serverService.create(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        serverService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDTO> updateById(@RequestBody @Valid ServerPutBodyDTO server, @PathVariable Long id) {
        ServerDTO updatedServer = serverService.updateById(id, server);
        return ResponseEntity.status(HttpStatus.OK).body(updatedServer); 
    }

    @PostMapping("/{serverId}/subscribe/{username}")
    public ResponseEntity<Void> subscribeServer(@PathVariable String username, @PathVariable Long serverId) {
        serverService.subscribeUser(username, serverId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
