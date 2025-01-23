package com.concord.concordapi.friendship.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.friendship.dto.request.FriendshipCreateDTO;
import com.concord.concordapi.friendship.dto.request.FriendshipPutDTO;
import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.friendship.service.FriendshipService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {
    
    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/{id}")
    public ResponseEntity<FriendshipDto> get(@PathVariable Long id){
        FriendshipDto friendship = friendshipService.get(id);
        return ResponseEntity.status(HttpStatus.OK).body(friendship);
    }

    @PostMapping
    public ResponseEntity<FriendshipDto> create(@RequestBody @Valid FriendshipCreateDTO friendshipDTO) {
        FriendshipDto friendship = friendshipService.create(friendshipDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(friendship); 
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        friendshipService.cancel(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null); 
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FriendshipDto> update(@RequestBody @Valid FriendshipPutDTO friendshipPutDTO, @PathVariable Long id) {
        FriendshipDto friendship = friendshipService.update(id, friendshipPutDTO);
        return ResponseEntity.status(HttpStatus.OK).body(friendship); 
    }
}
