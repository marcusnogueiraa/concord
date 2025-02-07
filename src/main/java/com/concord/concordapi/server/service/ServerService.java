package com.concord.concordapi.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.server.dto.request.ServerCreateBodyDTO;
import com.concord.concordapi.server.dto.request.ServerPutBodyDTO;
import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.mapper.ServerMapper;
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
    private AuthService authService;
    
    @Autowired
    private FileStorageService fileStorageService;

    public ServerDto getById(Long id){
        Server server = serverRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        return ServerMapper.toDto(server);
    }

    public ServerDto create(ServerCreateBodyDTO server){
        User owner = userRepository.findById(server.ownerId())
            .orElseThrow(() -> new EntityNotFoundException("Owner "+server.ownerId()+" not found"));
        authService.isUserTheAuthenticated(owner);
        Server newServer = new Server();
        newServer.setName(server.name());
        newServer.setOwner(owner);
        if(server.imageTempPath() != null){
            FilePrefix prefix = new FilePrefix("server_image");
            fileStorageService.persistFile(prefix ,server.imageTempPath());
            newServer.setImagePath(prefix.getDisplayName()+"/"+server.imageTempPath());
        }
        newServer = serverRepository.save(newServer);
        owner.getServers().add(newServer);
        userRepository.save(owner);
        return ServerMapper.toDto(newServer);
    }
    
    public void deleteById(Long id){
        Server server = serverRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        authService.isUserTheAuthenticated(server.getOwner());
        for (User user : server.getUsers()) {
            user.getServers().remove(server);
            userRepository.save(user);
        }
        if(server.getImagePath()!= null && fileStorageService.fileExists(server.getImagePath())){
            fileStorageService.deleteFile(server.getImagePath());
        }
        serverRepository.delete(server);
    }

    public ServerDto updateById(Long id, ServerPutBodyDTO server){
        Server updatedServer = serverRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Server "+id+" not found"));
        authService.isUserTheAuthenticated(updatedServer.getOwner());
        if(server.imageTempPath() != null){
            FilePrefix prefix = new FilePrefix("server_image");
            fileStorageService.persistFile(prefix ,server.imageTempPath());
            if(fileStorageService.fileExists(updatedServer.getImagePath())){
                fileStorageService.deleteFile(updatedServer.getImagePath());
            }
            updatedServer.setImagePath(prefix.getDisplayName()+"/"+server.imageTempPath());
        }
        updatedServer.setName(server.name());
        Server createdServer = serverRepository.save(updatedServer);
        return ServerMapper.toDto(createdServer);
    }

    public void subscribeUser(String username, Long serverId){
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Server server = serverRepository.findById(serverId)
            .orElseThrow(() -> new EntityNotFoundException("Server not found"));
        user.getServers().add(server);
        userRepository.save(user);
    }
}
