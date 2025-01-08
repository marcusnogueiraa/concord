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
import com.concord.concordapi.server.dto.response.ServerSummaryDto;
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

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getById(@PathVariable("username") String username){
        UserDto user = userService.getByUsername(username);
        return ResponseEntity.ok(user);
    }
    @PutMapping("/{username}")
    public ResponseEntity<UserDto> update(@RequestBody UserPutDto userPutDto, @PathVariable("username") String username){
        UserDto user = userService.update(userPutDto, username);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/{username}/friendships")
    public ResponseEntity<List<FriendshipDto>> getAllFriendships(@PathVariable String username){
        List<FriendshipDto> friendship = friendshipService.getAllFriendships(username);
        return ResponseEntity.status(HttpStatus.OK).body(friendship);
    }
    @GetMapping("/{username}/pending-friendships")
    public ResponseEntity<List<FriendshipDto>> getAllPendingFriendships(@PathVariable String username){
        List<FriendshipDto> friendship = friendshipService.getAllPendingFriendships(username);
        return ResponseEntity.status(HttpStatus.OK).body(friendship);
    }
    @GetMapping("/{username}/servers")
    public ResponseEntity<List<ServerSummaryDto>> getServers(@PathVariable String username){
        List<ServerSummaryDto> servers = userService.getServersSummary(username);
        return ResponseEntity.status(HttpStatus.OK).body(servers);
    }
}
