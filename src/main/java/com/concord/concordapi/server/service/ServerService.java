package com.concord.concordapi.server.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.channel.dto.ChannelDTO;
import com.concord.concordapi.server.dto.ServerPutBodyDTO;
import com.concord.concordapi.server.dto.ServerCreateBodyDTO;
import com.concord.concordapi.server.dto.ServerDTO;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.repository.ServerRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.dto.UserRequestDto;
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

    public ServerDTO getById(Long id){
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server server = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        User user = server.getOwner();
        UserRequestDto userRequest = new UserRequestDto(user.getName(), user.getUsername(), user.getEmail(), user.getCreatedAt());
        List<ChannelDTO> channels = server.getChannelDTOs();
        ServerDTO serverDTO = new ServerDTO(server.getId(), server.getName(), userRequest, channels);
        return serverDTO;
    }

    public ServerDTO create(ServerCreateBodyDTO server){
        Server newServer = new Server();
        newServer.setName(server.name());
        Optional<User> searchedOwner = userRepository.findById(server.ownerId());
        User owner = searchedOwner.orElseThrow(() -> new EntityNotFoundException("Owner "+server.ownerId()+" not found"));
        if (!owner.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        newServer.setOwner(owner);
        newServer = serverRepository.save(newServer);
        User user = newServer.getOwner();
        UserRequestDto userRequest = new UserRequestDto(user.getName(), user.getUsername(), user.getEmail(), user.getCreatedAt());
        List<ChannelDTO> channels = newServer.getChannelDTOs();
        ServerDTO serverDTO = new ServerDTO(newServer.getId(), newServer.getName(), userRequest, channels);
        return serverDTO;
    }
    
    public void deleteById(Long id){
    
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server server = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        Optional<User> searchedOwner = userRepository.findById(server.getOwner().getId());
        User owner = searchedOwner.orElseThrow(() -> new EntityNotFoundException("Owner "+server.getOwner().getId()+" not found"));
        if (!owner.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
  
        for (User user : server.getUsers()) {
            System.out.println("removendo Server do user: "+user.getName());
            user.getServers().remove(server);
            userRepository.save(user);  // Salve os usuários para garantir que a relação seja removida
        }
        serverRepository.delete(server);
    }

    public ServerDTO updateById(Long id, ServerPutBodyDTO server){
        Optional<Server> searchedServer = serverRepository.findById(id);
        Server updatedServer = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));

       
        if (!updatedServer.getOwner().getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        updatedServer.setName(server.name());

        Server createdServer = serverRepository.save(updatedServer);

        User user = createdServer.getOwner();
        UserRequestDto userRequest = new UserRequestDto(user.getName(), user.getUsername(), user.getEmail(), user.getCreatedAt());
        List<ChannelDTO> channels = createdServer.getChannelDTOs();
        ServerDTO serverDTO = new ServerDTO(createdServer.getId(), createdServer.getName(), userRequest, channels);
        return serverDTO;
    }
    public void subscribeUser(String username, Long serverId){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new EntityNotFoundException("Server not found"));
        user.getServers().add(server);
        userRepository.save(user);
    }
}
