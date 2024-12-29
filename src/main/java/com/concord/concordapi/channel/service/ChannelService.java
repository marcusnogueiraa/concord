package com.concord.concordapi.channel.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.channel.dto.ChannelPutBodyDTO;
import com.concord.concordapi.channel.dto.ChannelRequestBodyDTO;
import com.concord.concordapi.channel.entity.Channel;
import com.concord.concordapi.channel.repository.ChannelRepository;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.repository.ServerRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;


@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private AuthService authService;

    public Channel get(Long id){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel channel = searchedChannel.orElseThrow(() -> new EntityNotFoundException("Channel "+id+" not found"));
        return channel;
    }

    public Channel create(ChannelRequestBodyDTO channel){
        Channel newChannel = new Channel();
        newChannel.setName(channel.name());
        Optional<Server> searchedServer = serverRepository.findById(channel.serverId());
        Server attServer = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+channel.serverId()+" not found"));
        if (!attServer.getOwner().getUsername().equals(authService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        newChannel.setServer(attServer);
        newChannel.setDescription(channel.description());
        Channel createdChannel = channelRepository.save(newChannel);
        return createdChannel;
    }
    public void delete(Long id){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel channel = searchedChannel.orElseThrow(() -> new EntityNotFoundException("Channel "+id+" not found"));
        if (!channel.getServer().getOwner().getUsername().equals(authService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        channelRepository.delete(channel);
    }
    public Channel update(Long id, ChannelPutBodyDTO channel){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel updateChannel = searchedChannel.orElseThrow(() -> new EntityNotFoundException("Channel "+id+" not found"));
        if (!updateChannel.getServer().getOwner().getUsername().equals(authService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("Owner doesn't match the logged-in user");
        }
        
        updateChannel.setName(channel.name());
        updateChannel.setDescription(channel.description());
        Channel createdChannel = channelRepository.save(updateChannel);
        return createdChannel;
    }
    
}
