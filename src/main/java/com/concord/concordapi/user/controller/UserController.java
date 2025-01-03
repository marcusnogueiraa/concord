package com.concord.concordapi.user.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.concord.concordapi.friendship.dto.response.FriendshipDTO;
import com.concord.concordapi.friendship.service.FriendshipService;
import com.concord.concordapi.user.dto.UserRequestDto;
import com.concord.concordapi.user.dto.UserPutDto;
import com.concord.concordapi.user.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    public UserService userService;
    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/{username}")
    public ResponseEntity<UserRequestDto> getByUsername(@PathVariable("username") String username){
        UserRequestDto user = userService.getByUsername(username);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{username}")
    public ResponseEntity<UserRequestDto> update(@RequestBody UserPutDto userPutDto, @PathVariable("username") String username){
        UserRequestDto user = userService.update(userPutDto, username);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/{id}/friendships")
    public ResponseEntity<List<FriendshipDTO>> getAllFriendships(@PathVariable Long id){
        List<FriendshipDTO> friendship = friendshipService.getAllFriendships(id);
        return ResponseEntity.status(HttpStatus.OK).body(friendship);
    }
}
