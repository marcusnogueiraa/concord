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

import com.concord.concordapi.friendship.dto.response.FriendshipDto;
import com.concord.concordapi.friendship.service.FriendshipService;
import com.concord.concordapi.user.dto.request.UserPutDto;
import com.concord.concordapi.user.dto.response.UserDto;
import com.concord.concordapi.user.service.UserService;


@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    public UserService userService;
    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable("id") Long id){
        UserDto user = userService.getById(id);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@RequestBody UserPutDto userPutDto, @PathVariable("id") Long id){
        UserDto user = userService.update(userPutDto, id);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/{id}/friendships")
    public ResponseEntity<List<FriendshipDto>> getAllFriendships(@PathVariable Long id){
        List<FriendshipDto> friendship = friendshipService.getAllFriendships(id);
        return ResponseEntity.status(HttpStatus.OK).body(friendship);
    }
}
