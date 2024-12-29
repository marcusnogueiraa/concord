package com.concord.concordapi.server.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.server.dto.ServerPutBodyDTO;
import com.concord.concordapi.server.dto.ServerRequestBodyDTO;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.repository.ServerRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;

@Service
public class ServerService {
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authInfoService;

    public Server getById(Long id){
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server server = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        return server;
    }

    public Server create(ServerRequestBodyDTO server){
        Server newServer = new Server();
        newServer.setName(server.name());
        Optional<User> searchedOwner = userRepository.findById(server.ownerId());
        User owner = searchedOwner.orElseThrow(() -> new EntityNotFoundException("Owner "+server.ownerId()+" not found"));
        if (!owner.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        newServer.setOwner(owner);
        return serverRepository.save(newServer);
    }
    
    public void deleteById(Long id){
    
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server server = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));

        Optional<User> searchedOwner = userRepository.findById(server.getOwner().getId());
        User owner = searchedOwner.orElseThrow(() -> new EntityNotFoundException("Owner "+server.getOwner().getId()+" not found"));
        if (!owner.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }

        serverRepository.delete(server);
    }

    public Server updateById(Long id, ServerPutBodyDTO server){
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server updatedServer = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));

       
        if (!updatedServer.getOwner().getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        updatedServer.setName(server.name());

        Server createdServer = serverRepository.save(updatedServer);
        return createdServer;
    }
    public void subscribeUser(String username, Long serverId){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server not found"));
        user.getServers().add(server);
        userRepository.save(user);
    }
}
