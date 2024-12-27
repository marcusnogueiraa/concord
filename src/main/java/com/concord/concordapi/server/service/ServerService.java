package com.concord.concordapi.server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.server.dto.ServerRequestBodyDTO;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.repository.ServerRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;


@Service
public class ServerService {
    @Autowired
    private ServerRepository serverRepository;

    public Server get(Long id){
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server server = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        return server;
    }

    public Server create(ServerRequestBodyDTO server){
        Server newServer = new Server();
        newServer.setName(server.name());
        newServer.setOwnerId(server.ownerId());
        return serverRepository.save(newServer);
    }
    public void delete(Server server){
        serverRepository.delete(server);
    }
    
}
