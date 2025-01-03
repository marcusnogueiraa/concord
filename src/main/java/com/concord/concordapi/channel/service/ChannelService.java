package com.concord.concordapi.channel.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.channel.dto.request.ChannelCreateBodyDto;
import com.concord.concordapi.channel.dto.request.ChannelPutBodyDto;
import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.entity.Channel;
import com.concord.concordapi.channel.mapper.ChannelMapper;
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

    public ChannelDto get(Long id){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel channel = searchedChannel.orElseThrow(() -> new EntityNotFoundException("Channel "+id+" not found"));
        return ChannelMapper.toDto(channel);
    }

    public ChannelDto create(ChannelCreateBodyDto channel){
        Channel newChannel = new Channel();
        newChannel.setName(channel.name());
        Optional<Server> searchedServer = serverRepository.findById(channel.serverId());
        Server attServer = searchedServer.orElseThrow(() -> new EntityNotFoundException("Server "+channel.serverId()+" not found"));
        authService.isUserTheAuthenticated(attServer.getOwner());
        newChannel.setServer(attServer);
        newChannel.setDescription(channel.description());
        Channel createdChannel = channelRepository.save(newChannel);
        return ChannelMapper.toDto(createdChannel);
    }
    public void delete(Long id){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel channel = searchedChannel.orElseThrow(() -> new EntityNotFoundException("Channel "+id+" not found"));
        authService.isUserTheAuthenticated(channel.getServer().getOwner());
        channelRepository.delete(channel);
    }
    public ChannelDto update(Long id, ChannelPutBodyDto channel){
        Optional<Channel> searchedChannel = channelRepository.findById(id);
        Channel updateChannel = searchedChannel.orElseThrow(() -> new EntityNotFoundException("Channel "+id+" not found"));
        authService.isUserTheAuthenticated(updateChannel.getServer().getOwner());
        updateChannel.setName(channel.name());
        updateChannel.setDescription(channel.description());
        Channel createdChannel = channelRepository.save(updateChannel);
        return ChannelMapper.toDto(createdChannel);
    }
    
}
