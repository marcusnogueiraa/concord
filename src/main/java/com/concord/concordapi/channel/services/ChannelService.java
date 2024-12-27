package com.concord.concordapi.channel.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.channel.dtos.ChannelRequestBodyDTO;
import com.concord.concordapi.channel.entities.Channel;
import com.concord.concordapi.servers.entities.Server;
import com.concord.concordapi.channel.repositories.ChannelRepository;
import com.concord.concordapi.servers.repositories.ServerRepository;
import com.concord.concordapi.shared.EntityNotFoundException;


@Service
public class ChannelService {
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ServerRepository serverRepository;

    public Channel get(Long id){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel channel = searchedChannel.orElseThrow(() -> new EntityNotFoundException(id));
        return channel;
    }

    public Channel create(ChannelRequestBodyDTO channel){
        Channel newChannel = new Channel();
        newChannel.setName(channel.name());
        Optional<Server> searchedServer = serverRepository.findById(channel.serverId());
        Server attServer = searchedServer.orElseThrow(() -> new EntityNotFoundException(channel.serverId()));
        newChannel.setServer(attServer);
        newChannel.setDescription(channel.description());
        return channelRepository.save(newChannel);
    }
    public boolean delete(Channel server){
        try{
            channelRepository.delete(server);
            return true;
        }catch(Exception error){
            return false;
        }
    }
    
}
