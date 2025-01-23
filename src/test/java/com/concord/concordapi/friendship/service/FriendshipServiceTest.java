package com.concord.concordapi.friendship.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.friendship.dto.request.FriendshipCreateDTO;
import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.friendship.mapper.FriendshipMapper;
import com.concord.concordapi.friendship.repository.FriendshipRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.shared.exception.FailManipulationFriendship;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.concord.concordapi.websocket.service.NotificationService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @Mock
    private FriendshipMapper friendshipMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FriendshipService friendshipService;

    private User to;
    private User from;
    private Friendship friendship;
    private FriendshipDto friendshipDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        to = new User(1l, "to", "to", "to@email.com", "123", null, new ArrayList(), null, null);
        from = new User(2l, "from", "from", "from@email.com", "123", null, new ArrayList(), null, null);
        friendship = new Friendship(1L, to, from, FriendshipStatus.ACCEPTED, null, null);
        friendshipDto = FriendshipMapper.toDto(friendship);
    }

    @Test
    void testGetFriendshipSuccess() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(authService.getAuthenticatedUserId()).thenReturn(friendship.getToUser().getId());
        FriendshipDto result = friendshipService.get(1L);

        assertNotNull(result);
        assertEquals(friendshipDto.id(), result.id());
        assertEquals(friendshipDto.from(), result.from());
        assertEquals(friendshipDto.to(), result.to());
        assertEquals(friendshipDto.status(), result.status());
    }

    @Test
    void testGetFriendshipNotFound() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> friendshipService.get(1L));
    }

    @Test
    void testGetAllFriendshipsSuccess() {
        Friendship friendship = new Friendship(1L, from, to, FriendshipStatus.ACCEPTED, null, null);
        FriendshipDto friendshipDto = friendshipMapper.toDto(friendship);

        when(userRepository.findByUsername("from")).thenReturn(Optional.of(from));
        doNothing().when(authService).isUserTheAuthenticated(from);
        when(friendshipRepository.findAllFriendshipsByUserAndStatus(from, FriendshipStatus.ACCEPTED))
            .thenReturn(List.of(friendship));

        List<FriendshipDto> result = friendshipService.getAllFriendships(from.getUsername());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(friendshipDto, result.get(0));
    }

    @Test
    void testGetAllFriendshipsUserNotFound() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> friendshipService.getAllFriendships("username"));
    }

    @Test
    void testCreateFriendshipSuccess() {

        FriendshipCreateDTO createDTO = new FriendshipCreateDTO("to");
        Friendship friendship = new Friendship(1L, from, to, FriendshipStatus.PENDING, null, null);
        FriendshipDto friendshipDto = friendshipMapper.toDto(friendship);

        when(authService.getAuthenticatedUserId()).thenReturn(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(from));
        when(userRepository.findByUsername("to")).thenReturn(Optional.of(to));
        when(friendshipRepository.findFriendshipsBetweenUsers(from, to, FriendshipStatus.PENDING)).thenReturn(List.of());
        when(friendshipRepository.findFriendshipsBetweenUsers(from, to, FriendshipStatus.ACCEPTED)).thenReturn(List.of());
        when(friendshipRepository.save(Mockito.any())).thenReturn(friendship);

        FriendshipDto result = friendshipService.create(createDTO);

        assertNotNull(result);
        assertEquals(friendshipDto.from(), result.from());
        assertEquals(friendshipDto.to(), result.to());
        assertEquals(friendshipDto.status(), result.status());
    }

    @Test
    void testCreateFriendshipUserAlreadyFriends() {
        FriendshipCreateDTO createDTO = new FriendshipCreateDTO("to");

        when(authService.getAuthenticatedUserId()).thenReturn(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(from));
        when(userRepository.findByUsername("to")).thenReturn(Optional.of(to));
        when(friendshipRepository.findFriendshipsBetweenUsers(from, to, FriendshipStatus.ACCEPTED)).thenReturn(List.of(friendship));

        assertThrows(FailManipulationFriendship.class, () -> friendshipService.create(createDTO));
    }

    @Test
    void testCancelFriendship() throws Exception {
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(1L)).thenReturn(Optional.of(from));
        doNothing().when(authService).isUserTheAuthenticated(from);

        friendshipService.cancel(1L);

        assertEquals(FriendshipStatus.CANCELED, friendship.getStatus());
        verify(friendshipRepository, times(1)).save(friendship);
        verify(notificationService, times(1)).sendFriendRequestToUser(2L, FriendshipMapper.toDto(friendship));
    }

    @Test
    void testCancelFriendship_UserNotFound() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            friendshipService.cancel(1L);
        });

        assertEquals("User id 1 not found", thrown.getMessage());
    }

    @Test
    void testAcceptFriendship() {
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(2L)).thenReturn(Optional.of(to));
        doNothing().when(authService).isUserTheAuthenticated(to);

        FriendshipDto response = friendshipService.accept(1L);

        assertEquals(FriendshipStatus.ACCEPTED, friendship.getStatus());
        assertNotNull(response);
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void testAcceptFriendship_FriendshipNotPending() {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        when(userRepository.findById(2L)).thenReturn(Optional.of(to));
        doNothing().when(authService).isUserTheAuthenticated(to);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));

        FailManipulationFriendship thrown = assertThrows(FailManipulationFriendship.class, () -> {
            friendshipService.accept(1L);
        });

        assertEquals("You can only accept pending friendship requests", thrown.getMessage());
    }

    @Test
    void testDenyFriendship() {
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(2L)).thenReturn(Optional.of(to));
        doNothing().when(authService).isUserTheAuthenticated(to);

        FriendshipDto response = friendshipService.deny(1L);

        assertEquals(FriendshipStatus.DENIED, friendship.getStatus());
        assertNotNull(response);
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void testDenyFriendship_FriendshipNotPending() {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(2L)).thenReturn(Optional.of(to));
        doNothing().when(authService).isUserTheAuthenticated(to);

        FailManipulationFriendship thrown = assertThrows(FailManipulationFriendship.class, () -> {
            friendshipService.deny(1L);
        });

        assertEquals("You can only deny pending friendship requests", thrown.getMessage());
    }

    @Test
    void testRemoveFriendship() {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(authService.getAuthenticatedUserId()).thenReturn(1L);

        FriendshipDto response = friendshipService.remove(1L);

        assertEquals(FriendshipStatus.REMOVED, friendship.getStatus());
        assertNotNull(response);
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void testRemoveFriendship_FriendshipNotAccepted() {
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(authService.getAuthenticatedUserId()).thenReturn(1L);

        FailManipulationFriendship thrown = assertThrows(FailManipulationFriendship.class, () -> {
            friendshipService.remove(1L);
        });

        assertEquals("You can only remove accepted friendships", thrown.getMessage());
    }

    
}