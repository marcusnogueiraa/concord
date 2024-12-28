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

import com.concord.concordapi.server.dto.ServerRequestBodyDTO;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.service.ServerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/servers")
public class ServerController {
    @Autowired
    private ServerService serverService;

    @GetMapping("/{id}")
    public ResponseEntity<Server> getById(@PathVariable Long id){
        Server server = serverService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(server);
    }

    @PostMapping
    public ResponseEntity<Server> create(@RequestBody @Valid ServerRequestBodyDTO server) {
        Server createdServer = serverService.create(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        serverService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<Server> updateById(@RequestBody @Valid ServerRequestBodyDTO server, @PathVariable Long id) {
        Server updatedServer = serverService.updateById(id, server);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(updatedServer); 
    }
}
