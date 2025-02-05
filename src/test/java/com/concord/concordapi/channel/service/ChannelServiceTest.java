package com.concord.concordapi.channel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

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

    @Test
    void testGetChannelById_Success() {
        // Arrange
        Long channelId = 1L;
        Channel mockChannel = new Channel();
        mockChannel.setId(channelId);
        mockChannel.setName("Test Channel");

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));

        // Act
        ChannelDto result = channelService.get(channelId);

        // Assert
        assertNotNull(result);
        assertEquals("Test Channel", result.name());
        Mockito.verify(channelRepository).findById(channelId);
    }

    @Test
    void testGetChannelById_NotFound() {
        // Arrange
        Long channelId = 1L;

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.get(channelId);
        });

        assertEquals("Channel 1 not found", exception.getMessage());
        Mockito.verify(channelRepository).findById(channelId);
    }

    @Test
    void testCreateChannel_Success() {
        // Arrange
        Long serverId = 1L;
        ChannelCreateBodyDto channelCreateBody = new ChannelCreateBodyDto("New Channel", serverId, "Test Description");
        Server mockServer = new Server();
        mockServer.setId(serverId);

        Channel mockChannel = new Channel();
        mockChannel.setName("New Channel");
        mockChannel.setDescription("Test Description");
        mockChannel.setServer(mockServer);

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.of(mockServer));
        Mockito.when(channelRepository.save(Mockito.any(Channel.class))).thenReturn(mockChannel);

        // Act
        ChannelDto result = channelService.create(channelCreateBody);

        // Assert
        assertNotNull(result);
        assertEquals("New Channel", result.name());
        assertEquals("Test Description", result.description());
        Mockito.verify(serverRepository).findById(serverId);
        Mockito.verify(authService).isUserTheAuthenticated(mockServer.getOwner());
        Mockito.verify(channelRepository).save(Mockito.any(Channel.class));
    }

    @Test
    void testCreateChannel_ServerNotFound() {
        // Arrange
        Long serverId = 1L;
        ChannelCreateBodyDto channelCreateBody = new ChannelCreateBodyDto("New Channel", serverId, "Test Description");

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.create(channelCreateBody);
        });

        assertEquals("Server 1 not found", exception.getMessage());
        Mockito.verify(serverRepository).findById(serverId);
    }

    @Test
    void testDeleteChannel_Success() {
        // Arrange
        Long channelId = 1L;
        Channel mockChannel = new Channel();
        mockChannel.setId(channelId);
        Server mockServer = new Server();
        mockChannel.setServer(mockServer);

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));

        // Act
        channelService.delete(channelId);

        // Assert
        Mockito.verify(authService).isUserTheAuthenticated(mockServer.getOwner());
        Mockito.verify(channelRepository).delete(mockChannel);
    }

    @Test
    void testDeleteChannel_NotFound() {
        // Arrange
        Long channelId = 1L;

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.delete(channelId);
        });

        assertEquals("Channel 1 not found", exception.getMessage());
        Mockito.verify(channelRepository).findById(channelId);
    }

    @Test
    void testUpdateChannel_Success() {
        // Arrange
        Long channelId = 1L;
        ChannelPutBodyDto channelUpdateBody = new ChannelPutBodyDto("Updated Channel", "Updated Description");
        Channel mockChannel = new Channel();
        mockChannel.setId(channelId);
        Server mockServer = new Server();
        mockChannel.setServer(mockServer);

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.of(mockChannel));
        Mockito.when(channelRepository.save(Mockito.any(Channel.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ChannelDto result = channelService.update(channelId, channelUpdateBody);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Channel", result.name());
        assertEquals("Updated Description", result.description());
        Mockito.verify(authService).isUserTheAuthenticated(mockServer.getOwner());
        Mockito.verify(channelRepository).save(mockChannel);
    }

    @Test
    void testUpdateChannel_NotFound() {
        // Arrange
        Long channelId = 1L;
        ChannelPutBodyDto channelUpdateBody = new ChannelPutBodyDto("Updated Channel", "Updated Description");

        Mockito.when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            channelService.update(channelId, channelUpdateBody);
        });

        assertEquals("Channel 1 not found", exception.getMessage());
        Mockito.verify(channelRepository).findById(channelId);
    }
}