package com.concord.concordapi.friendship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.friendship.entity.FriendshipStatus;
import com.concord.concordapi.friendship.mapper.FriendshipMapper;
import com.concord.concordapi.friendship.repository.FriendshipRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.friendship.dto.request.FriendshipCreateDTO;
import com.concord.concordapi.friendship.dto.request.FriendshipPutDTO;
import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.user.entity.User;
import com.concord.concordapi.user.repository.UserRepository;

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

    public FriendshipDto get(Long id) {
        Friendship friendship = friendshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Friendship not found"));
        return FriendshipMapper.toDto(friendship);
    }

    public List<FriendshipDto> getAllFriendships(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()-> new EntityNotFoundException("User id "+username+" not found"));
        List<Friendship> friendships = friendshipRepository.findAllByUser(user);
        List<FriendshipDto> friendshipDTOs = new ArrayList<>();
        for(Friendship friendship : friendships){
            friendshipDTOs.add(FriendshipMapper.toDto(friendship));
        }
        return friendshipDTOs;
    }

    public FriendshipDto create(FriendshipCreateDTO friendshipDTO) {
        User from = userRepository.findById(friendshipDTO.fromId()).orElseThrow(()-> new EntityNotFoundException("User id "+friendshipDTO.fromId()+" not found"));
        authService.isUserTheAuthenticated(from);
        User to = userRepository.findById(friendshipDTO.toId()).orElseThrow(()-> new EntityNotFoundException("User id "+friendshipDTO.toId()+" not found"));
        Friendship friendship = new Friendship(null, from, to, FriendshipStatus.PENDING, null, null);
        friendship = friendshipRepository.save(friendship);
        return FriendshipMapper.toDto(friendship);
    }

    public void delete(Long id) {
        User from = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User id "+id+" not found"));
        authService.isUserTheAuthenticated(from);
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        friendshipRepository.delete(friendship);
    }

    public FriendshipDto update(Long id, FriendshipPutDTO friendshipDTO) {
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        User to = userRepository.findById(friendship.getTo_user().getId()).orElseThrow(()-> new EntityNotFoundException("User id "+id+" not found"));
        authService.isUserTheAuthenticated(to);
        friendship.setStatus(friendshipDTO.status());
        friendshipRepository.save(friendship);
        return FriendshipMapper.toDto(friendship);
    }
}