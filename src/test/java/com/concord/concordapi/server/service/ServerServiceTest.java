package com.concord.concordapi.server.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.fileStorage.entity.FilePrefix;
import com.concord.concordapi.fileStorage.service.FileStorageService;
import com.concord.concordapi.server.dto.request.ServerCreateBodyDTO;
import com.concord.concordapi.server.dto.request.ServerPutBodyDTO;
import com.concord.concordapi.server.dto.response.ServerDto;
import com.concord.concordapi.server.entity.Server;
import com.concord.concordapi.server.repository.ServerRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ServerServiceTest {

    @Mock
    private ServerRepository serverRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private ServerService serverService;

    @Test
    void testGetById() {
        Long serverId = 1L;
        Server server = new Server();
        server.setId(serverId);
        server.setName("Test Server");
        server.setOwner(new User(1L, "test user", "testuser", "test@gmail.com", "123", null, null, null, null));

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));

        ServerDto result = serverService.getById(serverId);

        assertNotNull(result);
        assertEquals("Test Server", result.name());
        Mockito.verify(serverRepository).findById(serverId);
    }

    @Test
    void testGetById_ServerNotFound() {
        Long serverId = 1L;

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> serverService.getById(serverId));
    }

    @Test
    void testCreateServer() {
        ServerCreateBodyDTO serverDto = new ServerCreateBodyDTO("TestServer", 1L, "tempImagePath");
        User owner = new User(1L, "test user", "testuser", "test@gmail.com", "123", null, null, null, null);
        owner.setServers(new ArrayList());
        Mockito.when(userRepository.findById(serverDto.ownerId())).thenReturn(Optional.of(owner));
        Mockito.doNothing().when(authService).isUserTheAuthenticated(owner);
        Mockito.when(serverRepository.save(Mockito.any(Server.class))).thenAnswer(i -> i.getArgument(0));

        ServerDto result = serverService.create(serverDto);
        assertNotNull(result);
        assertEquals("TestServer", result.name());
        Mockito.verify(fileStorageService).persistImage(Mockito.any(FilePrefix.class), Mockito.eq("tempImagePath"));
        Mockito.verify(serverRepository).save(Mockito.any(Server.class));
    }

    @Test
    void testDeleteById() {
        Long serverId = 1L;
        Server server = new Server();
        server.setId(serverId);
        server.setImagePath("path/to/image");
        User owner = new User();
        owner.setId(1L);
        server.setOwner(owner);
        server.setUsers(new HashSet<>());

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));
        Mockito.doNothing().when(authService).isUserTheAuthenticated(owner);
        Mockito.when(fileStorageService.fileExists(server.getImagePath())).thenReturn(true);
        Mockito.doNothing().when(fileStorageService).deleteFile(server.getImagePath());

        serverService.deleteById(serverId);

        Mockito.verify(serverRepository).findById(serverId);
        Mockito.verify(authService).isUserTheAuthenticated(owner);
        Mockito.verify(fileStorageService).deleteFile(server.getImagePath());
        Mockito.verify(serverRepository).delete(server);
    }

    @Test
    void testUpdateById() {
        Long serverId = 1L;
        ServerPutBodyDTO serverDto = new ServerPutBodyDTO("Updated Server", "newTempImagePath");

        Server server = new Server();
        server.setId(serverId);
        server.setName("Old Server");
        server.setImagePath("path/to/oldImage");
        User owner = new User();
        server.setOwner(owner);

        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));
        Mockito.doNothing().when(authService).isUserTheAuthenticated(owner);
        Mockito.when(fileStorageService.fileExists(server.getImagePath())).thenReturn(true);
        Mockito.when(fileStorageService.persistImage(Mockito.any(FilePrefix.class), Mockito.eq("newTempImagePath"))).thenReturn("newTempImagePath");
        Mockito.doNothing().when(fileStorageService).deleteFile(server.getImagePath());
        Mockito.when(serverRepository.save(Mockito.any(Server.class))).thenReturn(server);

        ServerDto result = serverService.updateById(serverId, serverDto);

        assertNotNull(result);
        assertEquals("Updated Server", result.name());
        Mockito.verify(fileStorageService).persistImage(Mockito.any(FilePrefix.class), Mockito.eq("newTempImagePath"));
        Mockito.verify(fileStorageService).deleteFile("path/to/oldImage");
        Mockito.verify(serverRepository).save(server);
    }

    @Test
    void testSubscribeUser() {
        String username = "testuser";
        Long serverId = 1L;

        User user = new User();
        user.setServers(new ArrayList<>());
        user.setUsername(username);
        Server server = new Server();
        server.setId(serverId);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        Mockito.when(serverRepository.findById(serverId)).thenReturn(Optional.of(server));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        serverService.subscribeUser(username, serverId);

        assertTrue(user.getServers().contains(server));
        Mockito.verify(userRepository).save(user);
    }
}
