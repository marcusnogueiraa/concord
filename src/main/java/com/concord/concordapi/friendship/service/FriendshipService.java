package com.concord.concordapi.friendship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.friendship.mapper.FriendshipMapper;
import com.concord.concordapi.friendship.repository.FriendshipRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.shared.exception.FailManipulationFriendship;
import com.concord.concordapi.shared.exception.InternalServerErrorException;
import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.friendship.dto.request.FriendshipCreateDTO;
import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;
import com.concord.concordapi.websocket.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendshipService {

    @Autowired
    private FriendshipRepository friendshipRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private NotificationService notificationService;

    public FriendshipDto get(Long id) {
        Friendship friendship = friendshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Friendship not found"));
        canAuthenticatedManipuleFriendship(friendship);
        return FriendshipMapper.toDto(friendship);
    }

    public List<FriendshipDto> getAllFriendships(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new EntityNotFoundException("Username "+username+" not found"));
        authService.isUserTheAuthenticated(user);
        List<Friendship> friendships = friendshipRepository.findAllFriendshipsByUserAndStatus(user, FriendshipStatus.ACCEPTED);
        List<FriendshipDto> friendshipDTOs = new ArrayList<>();
        for(Friendship friendship : friendships){
            friendshipDTOs.add(FriendshipMapper.toDto(friendship));
        }
        return friendshipDTOs;
    }

    public List<FriendshipDto> getAllPendingFriendships(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new EntityNotFoundException("Username "+username+" not found"));
        authService.isUserTheAuthenticated(user);
        List<Friendship> friendships = friendshipRepository.findAllFriendshipsByUserAndStatus(user, FriendshipStatus.PENDING);
        List<FriendshipDto> friendshipDTOs = new ArrayList<>();
        for(Friendship friendship : friendships){
            friendshipDTOs.add(FriendshipMapper.toDto(friendship));
        }
        return friendshipDTOs;
    }

    public FriendshipDto create(FriendshipCreateDTO friendshipDTO) {
        User from = userRepository.findById(authService.getAuthenticatedUserId()).orElseThrow(()-> new EntityNotFoundException("User id "+authService.getAuthenticatedUserId()+" not found"));
        User to = userRepository.findByUsername(friendshipDTO.toUsername()).orElseThrow(()-> new EntityNotFoundException("User with username "+friendshipDTO.toUsername()+" not found"));
        
        if(from == to){
            throw new FailManipulationFriendship("User cannot send a friendship request to themselves");
        }
        if(!friendshipRepository.findFriendshipsBetweenUsers(from, to, FriendshipStatus.PENDING).isEmpty()){
            throw new FailManipulationFriendship("A pending friendship request already exists to this user");
        }
        if(!friendshipRepository.findFriendshipsBetweenUsers(from, to, FriendshipStatus.ACCEPTED).isEmpty()){
            throw new FailManipulationFriendship("You are already friends");
        }
        Friendship friendship = new Friendship(null, from, to, FriendshipStatus.PENDING, null, null);
        friendship = friendshipRepository.save(friendship);

        FriendshipDto responseDto = FriendshipMapper.toDto(friendship);
        notifyUser(to.getId(), responseDto);
        return responseDto;
    }

    public void cancel(Long id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new FailManipulationFriendship("You can only cancel pending friendship requests");
        }
        Long fromId = friendship.getFromUser().getId();
        User from = userRepository.findById(fromId).orElseThrow(()-> new EntityNotFoundException("User id "+fromId+" not found"));
        authService.isUserTheAuthenticated(from);

        friendship.setStatus(FriendshipStatus.CANCELED);
        friendshipRepository.save(friendship);
        
        Long toUSerId = friendship.getToUser().getId();
        notifyUser(toUSerId, FriendshipMapper.toDto(friendship));
    }

    public FriendshipDto accept(Long id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        User to = userRepository.findById(friendship.getToUser().getId()).orElseThrow(()-> new EntityNotFoundException("User id "+id+" not found"));
        authService.isUserTheAuthenticated(to);
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new FailManipulationFriendship("You can only accept pending friendship requests");
        }
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        friendshipRepository.save(friendship);

        FriendshipDto responseDto = FriendshipMapper.toDto(friendship);

        Long fromId = friendship.getFromUser().getId();
        notifyUser(fromId, responseDto);
        return responseDto;
    }

    public FriendshipDto deny(Long id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        User to = userRepository.findById(friendship.getToUser().getId()).orElseThrow(()-> new EntityNotFoundException("User id "+id+" not found"));
        authService.isUserTheAuthenticated(to);
        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new FailManipulationFriendship("You can only deny pending friendship requests");
        }
        friendship.setStatus(FriendshipStatus.DENIED);
        friendshipRepository.save(friendship);

        FriendshipDto responseDto = FriendshipMapper.toDto(friendship);
        Long fromId = friendship.getFromUser().getId();
        notifyUser(fromId, responseDto);
        return responseDto;
    }

    public FriendshipDto remove(Long id) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        
        canAuthenticatedManipuleFriendship(friendship);

        if (friendship.getStatus() != FriendshipStatus.ACCEPTED) {
            throw new FailManipulationFriendship("You can only remove accepted friendships");
        }

        friendship.setStatus(FriendshipStatus.REMOVED);
        friendshipRepository.save(friendship);

        FriendshipDto responseDto = FriendshipMapper.toDto(friendship);
        
        Long notifyUserId = getUserIdToNotify(friendship);
        notifyUser(notifyUserId, responseDto);
        return responseDto;
    }

    private Long getUserIdToNotify(Friendship friendship){
        Long authenticatedUserId = authService.getAuthenticatedUserId();
        if (authenticatedUserId == friendship.getFromUser().getId())
            return friendship.getToUser().getId();
        else {
            return friendship.getFromUser().getId();
        } 
    }

    private void canAuthenticatedManipuleFriendship(Friendship friendship){
        Long authenticatedId = authService.getAuthenticatedUserId();
        if(friendship.getFromUser().getId() != authenticatedId && friendship.getToUser().getId()!=authenticatedId){
            throw new AuthorizationDeniedException("Authenticated User doesn't have permission to perform this action.");
        }
    }

    private void notifyUser(Long toUserId, FriendshipDto friendshipDto){
        try {
            notificationService.sendFriendRequestToUser(toUserId, friendshipDto);
        } catch (Exception exception) {
            throw new InternalServerErrorException("Notification Serialization Error");
        }
    }
}