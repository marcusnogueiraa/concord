package com.concord.concordapi.channel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.channel.dto.request.ChannelCreateBodyDto;
import com.concord.concordapi.channel.dto.request.ChannelPutBodyDto;
import com.concord.concordapi.channel.dto.response.ChannelDto;
import com.concord.concordapi.channel.entity.Channel;
import com.concord.concordapi.channel.repository.ChannelRepository;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.repository.ServerRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ChannelService channelService;

    static Random random;
    
    @BeforeAll
    static void setup(){
        random = new Random();
    }

    @Test
    void testGetChannelById_Success() {
        Long channelId = random.nextLong();
        Channel mockChannel = new Channel();
        mockChannel.setId(channelId);
        mockChannel.setName("Test Channel");

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));
        ChannelDto result = channelService.get(channelId);

        assertNotNull(result);
        assertEquals("Test Channel", result.name());
        Mockito.verify(channelRepository).findById(channelId);
    }

    @Test
    void testGetChannelById_NotFound() {
        Long channelId = random.nextLong();

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.get(channelId);
        });

        assertEquals("Channel "+channelId+" not found", exception.getMessage());
        Mockito.verify(channelRepository).findById(channelId);
    }

    @Test
    void testCreateChannel_Success() {
        Long serverId = random.nextLong();
        ChannelCreateBodyDto channelCreateBody = new ChannelCreateBodyDto("New Channel", serverId, "Test Description");
        Server mockServer = new Server();
        mockServer.setId(serverId);

        Channel mockChannel = new Channel();
        mockChannel.setName("New Channel");
        mockChannel.setDescription("Test Description");
        mockChannel.setServer(mockServer);

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.of(mockServer));
        Mockito.when(channelRepository.save(Mockito.any(Channel.class))).thenReturn(mockChannel);

        ChannelDto result = channelService.create(channelCreateBody);

        assertNotNull(result);
        assertEquals("New Channel", result.name());
        assertEquals("Test Description", result.description());
        Mockito.verify(serverRepository).findById(serverId);
        Mockito.verify(authService).isUserTheAuthenticated(mockServer.getOwner());
        Mockito.verify(channelRepository).save(Mockito.any(Channel.class));
    }

    @Test
    void testCreateChannel_ServerNotFound() {
        Long serverId = random.nextLong();
        ChannelCreateBodyDto channelCreateBody = new ChannelCreateBodyDto("New Channel", serverId, "Test Description");

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.create(channelCreateBody);
        });

        assertEquals("Server "+serverId+" not found", exception.getMessage());
        Mockito.verify(serverRepository).findById(serverId);
    }

    @Test
    void testDeleteChannel_Success() {
        Long channelId = random.nextLong();
        Channel mockChannel = new Channel();
        mockChannel.setId(channelId);
        Server mockServer = new Server();
        mockChannel.setServer(mockServer);

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));

        channelService.delete(channelId);

        Mockito.verify(authService).isUserTheAuthenticated(mockServer.getOwner());
        Mockito.verify(channelRepository).delete(mockChannel);
    }

    @Test
    void testDeleteChannel_NotFound() {
        Long channelId = random.nextLong();

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.delete(channelId);
        });

        assertEquals("Channel "+channelId+" not found", exception.getMessage());
        Mockito.verify(channelRepository).findById(channelId);
    }

    @Test
    void testUpdateChannel_Success() {
        Long channelId = random.nextLong();
        ChannelPutBodyDto channelUpdateBody = new ChannelPutBodyDto("Updated Channel", "Updated Description");
        Channel mockChannel = new Channel();
        mockChannel.setId(channelId);
        Server mockServer = new Server();
        mockChannel.setServer(mockServer);

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));
        Mockito.when(channelRepository.save(Mockito.any(Channel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ChannelDto result = channelService.update(channelId, channelUpdateBody);

        assertNotNull(result);
        assertEquals("Updated Channel", result.name());
        assertEquals("Updated Description", result.description());
        Mockito.verify(authService).isUserTheAuthenticated(mockServer.getOwner());
        Mockito.verify(channelRepository).save(mockChannel);
    }

    @Test
    void testUpdateChannel_NotFound() {
        Long channelId = random.nextLong();
        ChannelPutBodyDto channelUpdateBody = new ChannelPutBodyDto("Updated Channel", "Updated Description");

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.update(channelId, channelUpdateBody);
        });

        assertEquals("Channel "+channelId+" not found", exception.getMessage());
        Mockito.verify(channelRepository).findById(channelId);
    }
}