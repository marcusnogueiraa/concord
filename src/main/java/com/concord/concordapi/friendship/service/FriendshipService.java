package com.concord.concordapi.friendship.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;
import com.concord.concordapi.friendship.entity.Friendship;
import com.concord.concordapi.friendship.repository.FriendshipRepository;
import com.concord.concordapi.shared.exception.EntityNotFoundException;
import com.concord.concordapi.auth.service.AuthService;
import com.concord.concordapi.friendship.dto.FriendshipCreateDTO;
import com.concord.concordapi.friendship.dto.FriendshipDTO;
import com.concord.concordapi.friendship.dto.FriendshipPutDTO;
import com.concord.concordapi.user.dto.UserRequestDto;
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
    private AuthService authInfoService;

    public FriendshipDTO get(Long id) {
        Friendship friendship = friendshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Friendship not found"));
        UserRequestDto userFromRequest = makeUserRequestDTOByUser(friendship.getFrom_user());
        UserRequestDto userToRequest = makeUserRequestDTOByUser(friendship.getTo_user());
        return new FriendshipDTO(friendship.getId(), userFromRequest, userToRequest, friendship.getStatus());
    }

    public List<FriendshipDTO> getAllFriendships(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new EntityNotFoundException("User id "+userId+" not found"));
        List<Friendship> friendships = friendshipRepository.findAllByUser(user);
        List<FriendshipDTO> friendshipDTOs = new ArrayList<>();
        for(Friendship friendship : friendships){
            UserRequestDto userFromRequest = makeUserRequestDTOByUser(friendship.getFrom_user());
            UserRequestDto userToRequest = makeUserRequestDTOByUser(friendship.getTo_user());
            friendshipDTOs.add(new FriendshipDTO(friendship.getId(), userFromRequest, userToRequest, friendship.getStatus()));
        }
        return friendshipDTOs;
    }

    public FriendshipDTO create(FriendshipCreateDTO friendshipDTO) {
        Friendship friendship = new Friendship();
        
        User from = userRepository.findById(friendshipDTO.fromId()).orElseThrow(()-> new EntityNotFoundException("User id "+friendshipDTO.fromId()+" not found"));
        if (!from.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        User to = userRepository.findById(friendshipDTO.toId()).orElseThrow(()-> new EntityNotFoundException("User id "+friendshipDTO.toId()+" not found"));
        friendship.setFrom_user(from);
        friendship.setTo_user(to);
        friendship.setStatus("PENDING");
        friendship = friendshipRepository.save(friendship); // Salva no banco
        UserRequestDto userFromRequest = makeUserRequestDTOByUser(friendship.getFrom_user());
        UserRequestDto userToRequest = makeUserRequestDTOByUser(friendship.getTo_user());
        return new FriendshipDTO(friendship.getId(), userFromRequest, userToRequest, friendship.getStatus()); // Retorna o DTO correspondente
    }

    public void delete(Long id) {
        User from = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User id "+id+" not found"));
        if (!from.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        friendshipRepository.delete(friendship); // Deleta a amizade
    }

    public FriendshipDTO update(Long id, FriendshipPutDTO friendshipDTO) {
        User from = userRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("User id "+id+" not found"));
        if (!from.getUsername().equals(authInfoService.getAuthenticatedUsername())) {
            throw new AuthorizationDeniedException("User doesn't match the logged-in user");
        }
        Friendship friendship = friendshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Friendship id "+id+" not found"));
        friendship.setStatus(friendshipDTO.status());
        friendshipRepository.save(friendship);
        UserRequestDto userFromRequest = makeUserRequestDTOByUser(friendship.getFrom_user());
        UserRequestDto userToRequest = makeUserRequestDTOByUser(friendship.getTo_user());
        return new FriendshipDTO(friendship.getId(), userFromRequest, userToRequest, friendship.getStatus());
    }

    private UserRequestDto makeUserRequestDTOByUser(User user){
        return new UserRequestDto(user.getId(),user.getName(), user.getUsername(), user.getImagePath(), user.getEmail(), user.getCreatedAt());
    }
}