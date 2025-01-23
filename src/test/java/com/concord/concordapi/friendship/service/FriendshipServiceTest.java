package com.concord.concordapi.friendship.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authorization.AuthorizationDeniedException;

import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.friendship.dto.request.FriendshipCreateDTO;
import com.concord.concordapi.friendship.dto.request.FriendshipPutDTO;
import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.friendship.mapper.FriendshipMapper;
import com.concord.concordapi.friendship.repository.FriendshipRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.shared.exception.FailManipulationFriendship;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
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
        when(authService.getAuthenticatedUserId()).thenReturn(friendship.getTo_user().getId());
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
    void testCanceledSuccess() {
        User user = new User(1l, "user", "user", "user@email.com", "123", null, new ArrayList(), null, null);
        Friendship friendship = new Friendship(1L, user, new User(), FriendshipStatus.PENDING, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));

        friendshipService.canceled(1L);

        assertEquals(FriendshipStatus.CANCELED, friendship.getStatus());
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void testCanceledFriendshipNotFound() {
        when(friendshipRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> friendshipService.canceled(1L));
    }

    @Test
    void testCanceledInvalidStatus() {
        User user = new User(1l, "user", "user", "user@email.com", "123", null, new ArrayList(), null, null);
        Friendship friendship = new Friendship(1L, user, new User(), FriendshipStatus.ACCEPTED, null, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));

        assertThrows(FailManipulationFriendship.class, () -> friendshipService.canceled(1L));
    }

    @Test
    void testUpdateFriendshipSuccess() {
        FriendshipPutDTO friendshipPutDTO = new FriendshipPutDTO(1L, FriendshipStatus.ACCEPTED);
        Friendship friendship = new Friendship(1L, from, to, FriendshipStatus.PENDING, null, null);

        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(friendship.getTo_user().getId())).thenReturn(Optional.of(to));
        doNothing().when(authService).isUserTheAuthenticated(friendship.getTo_user());
        when(friendshipRepository.save(friendship)).thenReturn(friendship);

        FriendshipDto result = friendshipService.update(1L, friendshipPutDTO);

        assertNotNull(result);
        assertEquals(friendshipPutDTO.status(), result.status());
    }

    @Test
    void testUpdateFriendshipNotFound() {
        FriendshipPutDTO friendshipPutDTO = new FriendshipPutDTO(1L, FriendshipStatus.ACCEPTED);
        when(friendshipRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> friendshipService.update(1L, friendshipPutDTO));
    }

    @Test
    void testUpdateFriendshipUnauthorized() {
        FriendshipPutDTO friendshipPutDTO = new FriendshipPutDTO(1L, FriendshipStatus.ACCEPTED);
        Friendship friendship = new Friendship(1L, from, to, FriendshipStatus.PENDING, null, null);

        when(friendshipRepository.findById(1L)).thenReturn(Optional.of(friendship));
        when(userRepository.findById(friendship.getTo_user().getId())).thenReturn(Optional.of(to));
        doThrow(AuthorizationDeniedException.class).when(authService).isUserTheAuthenticated(friendship.getTo_user());

        assertThrows(AuthorizationDeniedException.class, () -> friendshipService.update(1L, friendshipPutDTO));
    }
}